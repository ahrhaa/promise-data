package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class AbbrevsModel extends AbstractTableModel
{
	Vector abbrevs;
	int lastSort;

	//{{{ AbbrevsModel constructor
	AbbrevsModel(Hashtable abbrevHash)
	{
		abbrevs = new Vector();

		if(abbrevHash != null)
		{
			Enumeration abbrevEnum = abbrevHash.keys();
			Enumeration expandEnum = abbrevHash.elements();

			while(abbrevEnum.hasMoreElements())
			{
				abbrevs.addElement(new Abbrev((String)abbrevEnum.nextElement(),
					(String)expandEnum.nextElement()));
			}

			sort(0);
		}
	} //}}}

	//{{{ sort() method
	void sort(int col)
	{
		lastSort = col;
		MiscUtilities.quicksort(abbrevs,new AbbrevCompare(col));
		fireTableDataChanged();
	} //}}}

	//{{{ add() method
	void add(String abbrev, String expansion)
	{
		abbrevs.addElement(new Abbrev(abbrev,expansion));
		sort(lastSort);
	} //}}}

	//{{{ remove() method
	void remove(int index)
	{
		abbrevs.removeElementAt(index);
		fireTableStructureChanged();
	} //}}}

	//{{{ toHashtable() method
	public Hashtable toHashtable()
	{
		Hashtable hash = new Hashtable();
		for(int i = 0; i < abbrevs.size(); i++)
		{
			Abbrev abbrev = (Abbrev)abbrevs.elementAt(i);
			if(abbrev.abbrev.length() > 0
				&& abbrev.expand.length() > 0)
			{
				hash.put(abbrev.abbrev,abbrev.expand);
			}
		}
		return hash;
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return abbrevs.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		Abbrev abbrev = (Abbrev)abbrevs.elementAt(row);
		switch(col)
		{
		case 0:
			return abbrev.abbrev;
		case 1:
			return abbrev.expand;
		default:
			return null;
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return false;
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		if(value == null)
			value = "";

		Abbrev abbrev = (Abbrev)abbrevs.elementAt(row);

		if(col == 0)
			abbrev.abbrev = (String)value;
		else
			abbrev.expand = (String)value;

		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.abbrevs.abbrev");
		case 1:
			return jEdit.getProperty("options.abbrevs.expand");
		default:
			return null;
		}
	} //}}}

	//{{{ AbbrevCompare class
	class AbbrevCompare implements MiscUtilities.Compare
	{
		int col;

		AbbrevCompare(int col)
		{
			this.col = col;
		}

		public int compare(Object obj1, Object obj2)
		{
			Abbrev a1 = (Abbrev)obj1;
			Abbrev a2 = (Abbrev)obj2;

			if(col == 0)
			{
				String abbrev1 = a1.abbrev.toLowerCase();
				String abbrev2 = a2.abbrev.toLowerCase();

				return MiscUtilities.compareStrings(
					abbrev1,abbrev2,true);
			}
			else
			{
				String expand1 = a1.expand.toLowerCase();
				String expand2 = a2.expand.toLowerCase();

				return MiscUtilities.compareStrings(
					expand1,expand2,true);
			}
		}
	} //}}}
}
