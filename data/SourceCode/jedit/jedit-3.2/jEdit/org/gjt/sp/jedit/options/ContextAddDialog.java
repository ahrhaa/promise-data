package org.gjt.sp.jedit.options;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class ContextAddDialog extends EnhancedDialog
{
	public ContextAddDialog(Component comp, ListModel actionsListModel)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("options.context.add.title"),
			true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		content.add(BorderLayout.NORTH,new JLabel(
			jEdit.getProperty("options.context.add.caption")));

		JPanel mainPanel = new JPanel(new BorderLayout(6,0));

		ActionHandler actionHandler = new ActionHandler();
		ButtonGroup grp = new ButtonGroup();

		// Add separator
		separator = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.separator"));
		separator.setSelected(true);
		separator.addActionListener(actionHandler);
		grp.add(separator);
		mainPanel.add(BorderLayout.NORTH,separator);

		// Add action
		JPanel actionPanel = new JPanel(new BorderLayout(6,0));
		action = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.action"));
		action.addActionListener(actionHandler);
		grp.add(action);
		actionPanel.add(BorderLayout.NORTH,action);

		actionsList = new JList(actionsListModel);
		actionsList.setVisibleRowCount(8);
		actionsList.setEnabled(false);
		actionPanel.add(BorderLayout.CENTER,new JScrollPane(actionsList));

		mainPanel.add(BorderLayout.CENTER,actionPanel);

		// Add macro
		JPanel macroPanel = new JPanel(new BorderLayout(6,0));
		macro = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.macro"));
		macro.addActionListener(actionHandler);
		grp.add(macro);
		macroPanel.add(BorderLayout.NORTH,macro);

		macrosList = new JList(Macros.getMacroList());
		macrosList.setVisibleRowCount(8);
		macrosList.setEnabled(false);
		macroPanel.add(BorderLayout.CENTER,new JScrollPane(macrosList));

		mainPanel.add(BorderLayout.SOUTH,macroPanel);

		content.add(BorderLayout.CENTER,mainPanel);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.X_AXIS));
		southPanel.setBorder(new EmptyBorder(12,0,0,0));
		southPanel.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(actionHandler);
		getRootPane().setDefaultButton(ok);
		southPanel.add(ok);
		southPanel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(actionHandler);
		southPanel.add(cancel);
		southPanel.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,southPanel);

		pack();
		setLocationRelativeTo(JOptionPane.getFrameForComponent(comp));
		show();
	}

	public void ok()
	{
		isOK = true;
		dispose();
	}

	public void cancel()
	{
		dispose();
	}

	public ContextOptionPane.MenuItem getSelection()
	{
		if(!isOK)
			return null;

		if(separator.isSelected())
			return new ContextOptionPane.MenuItem("-","-");
		else if(action.isSelected())
			return (ContextOptionPane.MenuItem)actionsList.getSelectedValue();
		else if(macro.isSelected())
		{
			String selectedMacro = macrosList.getSelectedValue().toString();
			selectedMacro = "play-macro@" + selectedMacro;
			return new ContextOptionPane.MenuItem(selectedMacro,
				ContextOptionPane.getActionLabel(selectedMacro));
		}
		else
			throw new InternalError();
	}

	// private members
	private boolean isOK;
	private JRadioButton separator, action, macro;
	private JList actionsList, macrosList;
	private JButton ok, cancel;

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source instanceof JRadioButton)
			{
				actionsList.setEnabled(action.isSelected());
				macrosList.setEnabled(macro.isSelected());
			}
			if(source == ok)
				ok();
			else if(source == cancel)
				cancel();
		}
	}
}
