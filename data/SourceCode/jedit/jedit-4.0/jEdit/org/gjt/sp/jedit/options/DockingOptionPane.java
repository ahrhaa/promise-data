package org.gjt.sp.jedit.options;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
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
		Box box = new Box(BoxLayout.X_AXIS);
		ButtonGroup grp = new ButtonGroup();

		layout1 = new JToggleButton(GUIUtilities.loadIcon("dock_layout1.gif"));
		grp.add(layout1);
		box.add(layout1);

		box.add(Box.createHorizontalStrut(6));

		layout2 = new JToggleButton(GUIUtilities.loadIcon("dock_layout2.gif"));
		grp.add(layout2);
		box.add(layout2);

		if(jEdit.getBooleanProperty("view.docking.alternateLayout"))
			layout2.setSelected(true);
		else
			layout1.setSelected(true);

		addComponent(jEdit.getProperty("options.docking.layout"),box);

		addComponent(Box.createVerticalStrut(6));

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = 3;
		cons.gridwidth = cons.gridheight = GridBagConstraints.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = cons.weighty = 1.0f;

		JScrollPane windowScroller = createWindowTableScroller();
		gridBag.setConstraints(windowScroller,cons);
		add(windowScroller);
	} //}}}

	//{{{ _save() method
	public void _save()
	{
		jEdit.setBooleanProperty("view.docking.alternateLayout",
			layout2.isSelected());
		windowModel.save();
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JToggleButton layout1;
	private JToggleButton layout2;
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
		d.height = Math.min(d.height,200);
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
			setRequestFocusEnabled(false);
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
