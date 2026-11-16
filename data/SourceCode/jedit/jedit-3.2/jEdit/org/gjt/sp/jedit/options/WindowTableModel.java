package org.gjt.sp.jedit.options;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class WindowTableModel extends AbstractTableModel
{
	private Vector windows;

	WindowTableModel()
	{
		Object[] list = EditBus.getNamedList(DockableWindow.DOCKABLE_WINDOW_LIST);
		windows = new Vector(list.length);
		for(int i = 0; i < list.length; i++)
		{
			windows.addElement(new Entry((String)list[i]));
		}

		sort();
	}

	public void sort()
	{
		MiscUtilities.quicksort(windows,new WindowCompare());
		fireTableDataChanged();
	}

	public int getColumnCount()
	{
		return 3;
	}

	public int getRowCount()
	{
		return windows.size();
	}

	public Class getColumnClass(int col)
	{
		switch(col)
		{
		case 0:
		case 1:
			return String.class;
		case 2:
			return Boolean.class;
		default:
			throw new InternalError();
		}
	}

	public Object getValueAt(int row, int col)
	{
		Entry window = (Entry)windows.elementAt(row);
		switch(col)
		{
		case 0:
			return window.title;
		case 1:
			return window.dockPosition;
		case 2:
			return new Boolean(window.autoOpen);
		default:
			throw new InternalError();
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return (col != 0);
	}

	public void setValueAt(Object value, int row, int col)
	{
		if(col == 0)
			return;

		Entry window = (Entry)windows.elementAt(row);
		switch(col)
		{
		case 1:
			window.dockPosition = (String)value;
			break;
		case 2:
			window.autoOpen = ((Boolean)value).booleanValue();
			break;
		default:
			throw new InternalError();
		}

		fireTableRowsUpdated(row,row);
	}

	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.docking.title");
		case 1:
			return jEdit.getProperty("options.docking.dockPosition");
		case 2:
			return jEdit.getProperty("options.docking.autoOpen");
		default:
			throw new InternalError();
		}
	}

	public void save()
	{
		for(int i = 0; i < windows.size(); i++)
		{
			((Entry)windows.elementAt(i)).save();
		}
	}

	class Entry
	{
		String name;
		String title;
		String dockPosition;
		boolean autoOpen;

		Entry(String name)
		{
			this.name = name;
			title = jEdit.getProperty(name + ".title");
			if(title == null)
				title = name;

			dockPosition = jEdit.getProperty(name + ".dock-position");
			if(dockPosition == null)
				dockPosition = DockableWindowManager.FLOATING;
			autoOpen = jEdit.getBooleanProperty(name + ".auto-open");
		}

		void save()
		{
			jEdit.setProperty(name + ".dock-position",dockPosition);
			jEdit.setBooleanProperty(name + ".auto-open",autoOpen);
		}
	}

	class WindowCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			Entry e1 = (Entry)obj1;
			Entry e2 = (Entry)obj2;

			return e1.title.compareTo(e2.title);
		}
	}
}
