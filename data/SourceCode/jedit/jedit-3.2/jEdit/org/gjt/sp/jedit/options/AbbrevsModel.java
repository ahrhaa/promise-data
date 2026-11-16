package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.EditAbbrevDialog;
import org.gjt.sp.jedit.*;

class AbbrevsModel extends AbstractTableModel
{
	Vector abbrevs;

	AbbrevsModel()
	{
		abbrevs = new Vector();
	}

	AbbrevsModel(Hashtable abbrevHash)
	{
		this();

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
	}

	public void sort(int col)
	{
		MiscUtilities.quicksort(abbrevs,new AbbrevCompare(col));
		fireTableDataChanged();
	}

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
	}

	public int getColumnCount()
	{
		return 2;
	}

	public int getRowCount()
	{
		return abbrevs.size() + 1;
	}

	public Object getValueAt(int row, int col)
	{
		if(row == abbrevs.size())
			return null;

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
	}

	public boolean isCellEditable(int row, int col)
	{
		return (col == 0);
	}

	public void setValueAt(Object value, int row, int col)
	{
		if(value == null)
			value = "";

		Abbrev abbrev;
		if(row == abbrevs.size())
		{
			abbrev = new Abbrev();
			abbrevs.addElement(abbrev);
		}
		else
			abbrev = (Abbrev)abbrevs.elementAt(row);

		if(col == 0)
			abbrev.abbrev = (String)value;
		else
			abbrev.expand = (String)value;

		fireTableRowsUpdated(row,row + 1);
	}

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
	}

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

				return abbrev1.compareTo(abbrev2);
			}
			else
			{
				String expand1 = a1.expand.toLowerCase();
				String expand2 = a2.expand.toLowerCase();

				return expand1.compareTo(expand2);
			}
		}
	}
}
