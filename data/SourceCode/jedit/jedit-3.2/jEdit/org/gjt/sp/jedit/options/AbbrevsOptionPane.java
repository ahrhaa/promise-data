package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
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
 * @version $Id: AbbrevsOptionPane.java,v 1.10 2001/02/05 09:15:30 sp Exp $
 */
public class AbbrevsOptionPane extends AbstractOptionPane
{
	public AbbrevsOptionPane()
	{
		super("abbrevs");
	}

	// protected members
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
		setsComboBox.addActionListener(new ActionHandler());
		panel3.add(setsComboBox);
		panel.add(panel3,BorderLayout.SOUTH);

		add(BorderLayout.NORTH,panel);

		globalAbbrevs = new AbbrevsModel(Abbrevs.getGlobalAbbrevs());
		abbrevsTable = new JTable(globalAbbrevs);
		abbrevsTable.getTableHeader().setReorderingAllowed(false);
		abbrevsTable.getTableHeader().addMouseListener(new HeaderMouseHandler());
		abbrevsTable.addMouseListener(new TableMouseHandler());
		Dimension d = abbrevsTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(abbrevsTable);
		scroller.setPreferredSize(d);
		add(BorderLayout.CENTER,scroller);
	}

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
	}

	// private members
	private JComboBox setsComboBox;
	private JCheckBox expandOnInput;
	private JTable abbrevsTable;
	private AbbrevsModel globalAbbrevs;
	private Hashtable modeAbbrevs;

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
	}

	class TableMouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			if(abbrevsTable.getSelectedColumn() == 1)
			{
				TableModel abbrevsModel = abbrevsTable.getModel();
				int row = abbrevsTable.getSelectedRow();

				String abbrev = (String)abbrevsModel.getValueAt(row,0);
				String expansion = (String)abbrevsModel.getValueAt(row,1);

				expansion = new EditAbbrevDialog(AbbrevsOptionPane.this,
					abbrev,expansion).getExpansion();
				if(expansion != null)
					abbrevsModel.setValueAt(expansion,row,1);
			}
		}
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
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
			}
		}
	}
}
