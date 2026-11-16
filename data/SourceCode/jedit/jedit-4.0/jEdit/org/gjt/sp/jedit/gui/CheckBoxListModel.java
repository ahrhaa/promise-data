package org.gjt.sp.jedit.gui;

import javax.swing.table.*;
import javax.swing.*;
import java.util.Vector;

class CheckBoxListModel extends AbstractTableModel
{
	Vector items;

	CheckBoxListModel(Vector _items)
	{
		items = new Vector(_items.size());
		for(int i = 0; i < _items.size(); i++)
		{
			items.addElement(createEntry(_items.elementAt(i)));
		}
	}

	CheckBoxListModel(Object[] _items)
	{
		items = new Vector(_items.length);
		for(int i = 0; i < _items.length; i++)
		{
			items.addElement(createEntry(_items[i]));
		}
	}

	private JCheckBoxList.Entry createEntry(Object obj)
	{
		if(obj instanceof JCheckBoxList.Entry)
			return (JCheckBoxList.Entry)obj;
		else
			return new JCheckBoxList.Entry(false,obj);
	}

	public int getRowCount()
	{
		return items.size();
	}

	public int getColumnCount()
	{
		return 2;
	}

	public String getColumnName(int col)
	{
		return null;
	}

	public Object getValueAt(int row, int col)
	{
		JCheckBoxList.Entry entry = (JCheckBoxList.Entry)items.elementAt(row);
		switch(col)
		{
		case 0:
			return new Boolean(entry.checked);
		case 1:
			return entry.value;
		default:
			throw new InternalError();
		}
	}

	public Class getColumnClass(int col)
	{
		switch(col)
		{
		case 0:
			return Boolean.class;
		case 1:
			return String.class;
		default:
			throw new InternalError();
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == 0;
	}

	public void setValueAt(Object value, int row, int col)
	{
		if(col == 0)
		{
			((JCheckBoxList.Entry)items.elementAt(row)).checked =
				(value.equals(Boolean.TRUE));
			fireTableRowsUpdated(row,row);
		}
	}
}
