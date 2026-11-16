package org.gjt.sp.jedit.options;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

public class DockingOptionPane extends AbstractOptionPane
{
	public DockingOptionPane()
	{
		super("docking");
	}

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

		// reuse properties defined by the general option pane
		String[] positions = {
			jEdit.getProperty("options.docking.top"),
			jEdit.getProperty("options.docking.bottom"),
		};

		tabsPos = new JComboBox(positions);
		tabsPos.setSelectedIndex(Integer.parseInt(jEdit.getProperty(
			"view.docking.tabsPos")));
		addComponent(jEdit.getProperty("options.docking.tabsPos"),tabsPos);

		addComponent(Box.createVerticalStrut(6));

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridy = 3;
		cons.gridwidth = cons.gridheight = GridBagConstraints.REMAINDER;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = cons.weighty = 1.0f;

		JScrollPane windowScroller = createWindowTableScroller();
		gridBag.setConstraints(windowScroller,cons);
		add(windowScroller);
	}

	public void _save()
	{
		jEdit.setBooleanProperty("view.docking.alternateLayout",
			layout2.isSelected());
		jEdit.setProperty("view.docking.tabsPos",String.valueOf(
			tabsPos.getSelectedIndex()));
		windowModel.save();
	}

	// private members
	private JToggleButton layout1;
	private JToggleButton layout2;
	private JComboBox tabsPos;
	private JTable windowTable;
	private WindowTableModel windowModel;

	private JScrollPane createWindowTableScroller()
	{
		windowModel = createWindowModel();
		windowTable = new JTable(windowModel);
		windowTable.getTableHeader().setReorderingAllowed(false);
		windowTable.setColumnSelectionAllowed(false);
		windowTable.setRowSelectionAllowed(false);
		windowTable.setCellSelectionEnabled(false);

		DockPositionCellRenderer comboBox = new DockPositionCellRenderer();
		comboBox.setRequestFocusEnabled(false);
		windowTable.setRowHeight(comboBox.getPreferredSize().height);
		TableColumn column = windowTable.getColumnModel().getColumn(1);
		column.setCellRenderer(comboBox);
		comboBox = new DockPositionCellRenderer();
		comboBox.setRequestFocusEnabled(false);
		column.setCellEditor(new DefaultCellEditor(comboBox));

		Dimension d = windowTable.getPreferredSize();
		d.height = Math.min(d.height,200);
		JScrollPane scroller = new JScrollPane(windowTable);
		scroller.setPreferredSize(d);
		return scroller;
	}

	private WindowTableModel createWindowModel()
	{
		return new WindowTableModel();
	}

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
		}

		public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus,
			int row, int column)
		{
			setSelectedItem(value);
			return this;
		}
	}
}
