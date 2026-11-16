package org.gjt.sp.jedit.gui;

import javax.swing.table.*;
import javax.swing.*;
import java.util.Vector;

/**
 * @since jEdit 3.2pre9
 */
public class JCheckBoxList extends JTable
{
	/**
	 * Creates a checkbox list with the given list of objects. The elements
	 * of this array can either be Entry instances, or other objects (if the
	 * latter, they will default to being unchecked).
	 */
	public JCheckBoxList(Object[] items)
	{
		setModel(items);
	}

	/**
	 * Creates a checkbox list with the given list of objects. The elements
	 * of this vector can either be Entry instances, or other objects (if the
	 * latter, they will default to being unchecked).
	 */
	public JCheckBoxList(Vector items)
	{
		setModel(items);
	}

	/**
	 * Sets the model to the given list of objects. The elements of this
	 * array can either be Entry instances, or other objects (if the
	 * latter, they will default to being unchecked).
	 */
	public void setModel(Object[] items)
	{
		setModel(new CheckBoxListModel(items));
		init();
	}

	/**
	 * Sets the model to the given list of objects. The elements of this
	 * vector can either be Entry instances, or other objects (if the
	 * latter, they will default to being unchecked).
	 */
	public void setModel(Vector items)
	{
		setModel(new CheckBoxListModel(items));
		init();
	}

	public Object[] getCheckedValues()
	{
		Vector values = new Vector();
		CheckBoxListModel model = (CheckBoxListModel)getModel();
		for(int i = 0; i < model.items.size(); i++)
		{
			Entry entry = (Entry)model.items.elementAt(i);
			if(entry.checked)
				values.addElement(entry.value);
		}

		Object[] retVal = new Object[values.size()];
		values.copyInto(retVal);
		return retVal;
	}

	public Entry[] getValues()
	{
		CheckBoxListModel model = (CheckBoxListModel)getModel();
		Entry[] retVal = new Entry[model.items.size()];
		model.items.copyInto(retVal);
		return retVal;
	}

	public Object getSelectedValue()
	{
		int row = getSelectedRow();
		if(row == -1)
			return null;
		else
			return getModel().getValueAt(row,1);
	}

	// private members
	private void init()
	{
		getSelectionModel().setSelectionMode(ListSelectionModel
			.SINGLE_SELECTION);
		setShowGrid(false);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumn column = getColumnModel().getColumn(0);
		int checkBoxWidth = new JCheckBox().getPreferredSize().width;
		column.setPreferredWidth(checkBoxWidth);
		column.setMinWidth(checkBoxWidth);
		column.setWidth(checkBoxWidth);
		column.setMaxWidth(checkBoxWidth);
		column.setResizable(false);
	}

	public static class Entry
	{
		boolean checked;
		Object value;

		public Entry(boolean checked, Object value)
		{
			this.checked = checked;
			this.value = value;
		}

		public boolean isChecked()
		{
			return checked;
		}

		public Object getValue()
		{
			return value;
		}
	}
}
