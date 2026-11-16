package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.*;

/**
 * Browser color editor.
 * @author Slava Pestov
 * @version $Id: BrowserColorsOptionPane.java,v 1.3 2001/11/25 03:42:15 spestov Exp $
 */
public class BrowserColorsOptionPane extends AbstractOptionPane
{
	//{{{ BrowserColorsOptionPane constructor
	public BrowserColorsOptionPane()
	{
		super("browser.colors");
	} //}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		colorsModel = new BrowserColorsModel();
		colorsTable = new JTable(colorsModel);
		colorsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		colorsTable.getTableHeader().setReorderingAllowed(false);
		colorsTable.addMouseListener(new MouseHandler());
		colorsTable.getSelectionModel().addListSelectionListener(
			new SelectionHandler());
		TableColumnModel tcm = colorsTable.getColumnModel();
		tcm.getColumn(1).setCellRenderer(new BrowserColorsModel.ColorRenderer());
		Dimension d = colorsTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(colorsTable);
		scroller.setPreferredSize(d);
		add(BorderLayout.CENTER,scroller);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.setBorder(new EmptyBorder(6,0,0,0));

		buttons.add(Box.createGlue());
		add = new JButton(jEdit.getProperty("options.browser.colors.add"));
		add.addActionListener(new ActionHandler());
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new JButton(jEdit.getProperty("options.browser.colors.remove"));
		remove.addActionListener(new ActionHandler());
		buttons.add(remove);
		buttons.add(Box.createGlue());

		add(BorderLayout.SOUTH,buttons);

		updateEnabled();
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		colorsModel.save();
	} //}}}

	//}}}

	//{{{ Private members
	private BrowserColorsModel colorsModel;
	private JTable colorsTable;
	private JButton add;
	private JButton remove;

	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		int selectedRow = colorsTable.getSelectedRow();
		remove.setEnabled(selectedRow != -1);
	} //}}}

	//}}}

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
			Object source = evt.getSource();
			if(source == add)
			{
				colorsModel.add();
			}
			else if(source == remove)
			{
				int selectedRow = colorsTable.getSelectedRow();
				colorsModel.remove(selectedRow);
				updateEnabled();
			}
		}
	} //}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			Point p = evt.getPoint();
			int row = colorsTable.rowAtPoint(p);
			int column = colorsTable.columnAtPoint(p);
			if(row == -1 || column != 1)
				return;

			Color color = JColorChooser.showDialog(
				BrowserColorsOptionPane.this,
				jEdit.getProperty("colorChooser.title"),
				(Color)colorsModel.getValueAt(row,1));
			if(color != null)
				colorsModel.setValueAt(color,row,1);
		}
	} //}}}
}
