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
	public ContextAddDialog(Component comp)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("options.context.add.title"),
			true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		ActionHandler actionHandler = new ActionHandler();
		ButtonGroup grp = new ButtonGroup();

		JPanel typePanel = new JPanel(new GridLayout(3,1,6,6));
		typePanel.setBorder(new EmptyBorder(0,0,6,0));
		typePanel.add(new JLabel(
			jEdit.getProperty("options.context.add.caption")));

		separator = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.separator"));
		separator.addActionListener(actionHandler);
		grp.add(separator);
		typePanel.add(separator);

		action = new JRadioButton(jEdit.getProperty("options.context"
			+ ".add.action"));
		action.addActionListener(actionHandler);
		grp.add(action);
		action.setSelected(true);
		typePanel.add(action);

		content.add(BorderLayout.NORTH,typePanel);

		JPanel actionPanel = new JPanel(new BorderLayout(6,6));

		ActionSet[] actionsList = jEdit.getActionSets();
		Vector vec = new Vector(actionsList.length);
		for(int i = 0; i < actionsList.length; i++)
		{
			ActionSet actionSet = actionsList[i];
			if(actionSet.getActionCount() != 0)
				vec.addElement(actionSet);
		}
		combo = new JComboBox(vec);
		combo.addActionListener(actionHandler);
		actionPanel.add(BorderLayout.NORTH,combo);

		list = new JList();
		list.setVisibleRowCount(8);
		actionPanel.add(BorderLayout.CENTER,new JScrollPane(list));

		content.add(BorderLayout.CENTER,actionPanel);

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

		updateList();

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

	public String getSelection()
	{
		if(!isOK)
			return null;

		if(separator.isSelected())
			return "-";
		else if(action.isSelected())
		{
			return ((ContextOptionPane.MenuItem)list.getSelectedValue())
				.actionName;
		}
		else
			throw new InternalError();
	}

	// private members
	private boolean isOK;
	private JRadioButton separator, action;
	private JComboBox combo;
	private JList list;
	private JButton ok, cancel;

	private void updateList()
	{
		ActionSet actionSet = (ActionSet)combo.getSelectedItem();
		EditAction[] actions = actionSet.getActions();
		Vector listModel = new Vector(actions.length);

		for(int i = 0; i < actions.length; i++)
		{
			EditAction action = actions[i];
			String label = action.getLabel();
			if(label == null)
				continue;

			listModel.addElement(new ContextOptionPane.MenuItem(
				action.getName(),label));
		}

		MiscUtilities.quicksort(listModel,new ContextOptionPane.MenuItemCompare());

		list.setListData(listModel);
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source instanceof JRadioButton)
			{
				combo.setEnabled(action.isSelected());
				list.setEnabled(action.isSelected());
			}
			if(source == ok)
				ok();
			else if(source == cancel)
				cancel();
			else if(source == combo)
				updateList();
		}
	}
}
