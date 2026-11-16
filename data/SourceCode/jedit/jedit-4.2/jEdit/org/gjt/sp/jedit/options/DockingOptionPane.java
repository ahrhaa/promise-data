package org.gjt.sp.jedit.options;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.msg.DockableWindowUpdate;
import org.gjt.sp.jedit.*;

public class DockingOptionPane extends AbstractOptionPane
{
	//{{{ DockingOptionPane constructor
	public DockingOptionPane()
	{
		super("docking");
	} //}}}

	//{{{ _init() method
	public void _init()
	{
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,createWindowTableScroller());
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		windowModel.save();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JTable windowTable;
	private WindowTableModel windowModel;
	//}}}

	//{{{ createWindowTableScroller() method
	private JScrollPane createWindowTableScroller()
	{
		windowModel = createWindowModel();
		windowTable = new JTable(windowModel);
		windowTable.getTableHeader().setReorderingAllowed(false);
		windowTable.setColumnSelectionAllowed(false);
		windowTable.setRowSelectionAllowed(false);
		windowTable.setCellSelectionEnabled(false);

		DockPositionCellRenderer comboBox = new DockPositionCellRenderer();
		windowTable.setRowHeight(comboBox.getPreferredSize().height);
		TableColumn column = windowTable.getColumnModel().getColumn(1);
		column.setCellRenderer(comboBox);
		column.setCellEditor(new DefaultCellEditor(new DockPositionCellRenderer()));

		Dimension d = windowTable.getPreferredSize();
		d.height = Math.min(d.height,50);
		JScrollPane scroller = new JScrollPane(windowTable);
		scroller.setPreferredSize(d);
		return scroller;
	} //}}}

	//{{{ createWindowModel() method
	private WindowTableModel createWindowModel()
	{
		return new WindowTableModel();
	} //}}}

	//}}}

	//{{{ DockPositionCellRenderer class
	class DockPositionCellRenderer extends JComboBox
		implements TableCellRenderer
	{
		DockPositionCellRenderer()
		{
			super(new String[] {
				DockableWindowManager.FLOATING,
				DockableWindowManager.TOP,
				DockableWindowManager.LEFT,
				DockableWindowManager.BOTTOM,
				DockableWindowManager.RIGHT
			});
			DockPositionCellRenderer.this.setRequestFocusEnabled(false);
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			setSelectedItem(value);
			return this;
		}
	} //}}}
}
