package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.EditAbbrevDialog;
import org.gjt.sp.jedit.*;

/**
 * Abbrev editor.
 * @author Slava Pestov
 * @version $Id: AbbrevsOptionPane.java,v 1.5 2002/02/15 03:03:16 spestov Exp $
 */
public class AbbrevsOptionPane extends AbstractOptionPane
{
	//{{{ AbbrevsOptionPane constructor
	public AbbrevsOptionPane()
	{
		super("abbrevs");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new BorderLayout());

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.X_AXIS));
		panel2.setBorder(new EmptyBorder(0,0,6,0));

		panel2.add(Box.createGlue());

		expandOnInput = new JCheckBox(jEdit.getProperty("options.abbrevs"
			+ ".expandOnInput"),Abbrevs.getExpandOnInput());
		panel2.add(expandOnInput);

		panel2.add(Box.createGlue());

		panel.add(panel2,BorderLayout.NORTH);

		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3,BoxLayout.X_AXIS));
		panel3.setBorder(new EmptyBorder(0,0,6,0));
		panel3.add(Box.createGlue());
		JLabel label = new JLabel(jEdit.getProperty("options.abbrevs.set"));
		label.setBorder(new EmptyBorder(0,0,0,12));
		panel3.add(label);

		Hashtable _modeAbbrevs = Abbrevs.getModeAbbrevs();
		modeAbbrevs = new Hashtable();
		Mode[] modes = jEdit.getModes();
		String[] sets = new String[modes.length + 1];
		sets[0] = "global";
		for(int i = 0; i < modes.length; i++)
		{
			String name = modes[i].getName();
			sets[i+1] = name;
			modeAbbrevs.put(name,new AbbrevsModel((Hashtable)_modeAbbrevs.get(name)));
		}

		setsComboBox = new JComboBox(sets);
		ActionHandler actionHandler = new ActionHandler();
		setsComboBox.addActionListener(actionHandler);
		panel3.add(setsComboBox);
		panel3.add(Box.createGlue());
		panel.add(panel3,BorderLayout.SOUTH);

		add(BorderLayout.NORTH,panel);

		globalAbbrevs = new AbbrevsModel(Abbrevs.getGlobalAbbrevs());
		abbrevsTable = new JTable(globalAbbrevs);
		abbrevsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		abbrevsTable.getTableHeader().setReorderingAllowed(false);
		abbrevsTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
		abbrevsTable.getSelectionModel().addListSelectionListener(
			new SelectionHandler());
		abbrevsTable.addMouseListener(new TableMouseHandler());
		Dimension d = abbrevsTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(abbrevsTable);
		scroller.setPreferredSize(d);
		add(BorderLayout.CENTER,scroller);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.setBorder(new EmptyBorder(6,0,0,0));

		buttons.add(Box.createGlue());
		add = new JButton(jEdit.getProperty("options.abbrevs.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		edit = new JButton(jEdit.getProperty("options.abbrevs.edit"));
		edit.addActionListener(actionHandler);
		buttons.add(edit);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new JButton(jEdit.getProperty("options.abbrevs.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		buttons.add(Box.createGlue());

		add(BorderLayout.SOUTH,buttons);

		updateEnabled();
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		if(abbrevsTable.getCellEditor() != null)
			abbrevsTable.getCellEditor().stopCellEditing();

		Abbrevs.setExpandOnInput(expandOnInput.isSelected());

		Abbrevs.setGlobalAbbrevs(globalAbbrevs.toHashtable());

		Hashtable modeHash = new Hashtable();
		Enumeration keys = modeAbbrevs.keys();
		Enumeration values = modeAbbrevs.elements();
		while(keys.hasMoreElements())
		{
			modeHash.put(keys.nextElement(),((AbbrevsModel)values.nextElement())
				.toHashtable());
		}
		Abbrevs.setModeAbbrevs(modeHash);
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JComboBox setsComboBox;
	private JCheckBox expandOnInput;
	private JTable abbrevsTable;
	private AbbrevsModel globalAbbrevs;
	private Hashtable modeAbbrevs;
	private JButton add;
	private JButton edit;
	private JButton remove;
	//}}}

	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		int selectedRow = abbrevsTable.getSelectedRow();
		edit.setEnabled(selectedRow != -1);
		remove.setEnabled(selectedRow != -1);
	} //}}}

	//{{{ edit() method
	private void edit()
	{
		AbbrevsModel abbrevsModel = (AbbrevsModel)abbrevsTable.getModel();

		int row = abbrevsTable.getSelectedRow();

		String abbrev = (String)abbrevsModel.getValueAt(row,0);
		String expansion = (String)abbrevsModel.getValueAt(row,1);
	
		EditAbbrevDialog dialog = new EditAbbrevDialog(
			AbbrevsOptionPane.this,
			abbrev,expansion);
		abbrev = dialog.getAbbrev();
		expansion = dialog.getExpansion();
		if(abbrev != null && expansion != null)
		{
			abbrevsModel.setValueAt(abbrev,row,0);
			abbrevsModel.setValueAt(expansion,row,1);
		}
	} //}}}

	//{{{ HeaderMouseHandler class
	class HeaderMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			switch(abbrevsTable.getTableHeader().columnAtPoint(evt.getPoint()))
			{
			case 0:
				((AbbrevsModel)abbrevsTable.getModel()).sort(0);
				break;
			case 1:
				((AbbrevsModel)abbrevsTable.getModel()).sort(1);
				break;
			}
		}
	} //}}}

	//{{{ TableMouseHandler class
	class TableMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(evt.getClickCount() == 2)
				edit();
		}
	} //}}}

	//{{{ SelectionHandler class
	class SelectionHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateEnabled();
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			AbbrevsModel abbrevsModel = (AbbrevsModel)abbrevsTable.getModel();

			Object source = evt.getSource();
			if(source == setsComboBox)
			{
				String selected = (String)setsComboBox.getSelectedItem();
				if(selected.equals("global"))
				{
					abbrevsTable.setModel(globalAbbrevs);
				}
				else
				{
					abbrevsTable.setModel((AbbrevsModel)
						modeAbbrevs.get(selected));
				}
				updateEnabled();
			}
			else if(source == add)
			{
				EditAbbrevDialog dialog = new EditAbbrevDialog(
					AbbrevsOptionPane.this,
					null,null);
				String abbrev = dialog.getAbbrev();
				String expansion = dialog.getExpansion();
				if(abbrev != null && abbrev.length() != 0
					&& expansion != null
					&& expansion.length() != 0)
				{
					abbrevsModel.add(abbrev,expansion);
					int index = abbrevsModel.getRowCount() - 1;
					abbrevsTable.getSelectionModel()
						.setSelectionInterval(index,index);
					Rectangle rect = abbrevsTable.getCellRect(
						index,0,true);
					abbrevsTable.scrollRectToVisible(rect);
					updateEnabled();
				}
			}
			else if(source == edit)
			{
				edit();
			}
			else if(source == remove)
			{
				int selectedRow = abbrevsTable.getSelectedRow();
				abbrevsModel.remove(selectedRow);
				updateEnabled();
			}
		}
	} //}}}
}
