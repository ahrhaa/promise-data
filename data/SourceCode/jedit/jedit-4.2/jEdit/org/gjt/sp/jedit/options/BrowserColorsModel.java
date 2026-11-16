package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.RolloverButton;
import org.gjt.sp.jedit.*;

class BrowserColorsModel extends AbstractTableModel
{
	//{{{ BrowserColorsModel constructor
	BrowserColorsModel()
	{
		entries = new ArrayList();

		int i = 0;
		String glob;
		while((glob = jEdit.getProperty("vfs.browser.colors." + i + ".glob")) != null)
		{
			entries.add(new Entry(glob,
				jEdit.getColorProperty(
				"vfs.browser.colors." + i + ".color",
				Color.black)));
			i++;
		}
	} //}}}

	//{{{ add() method
	void add()
	{
		entries.add(new Entry("",UIManager.getColor("Tree.foreground")));
		fireTableRowsInserted(entries.size() - 1,entries.size() - 1);
	} //}}}

	//{{{ remove() method
	void remove(int index)
	{
		entries.remove(index);
		fireTableRowsDeleted(entries.size(),entries.size());
	} //}}}

	//{{{ moveUp() method
	public void moveUp(int index)
	{
		Object obj = entries.get(index);
		entries.remove(index);
		entries.add(index - 1,obj);
		fireTableRowsUpdated(index - 1,index);
	} //}}}

	//{{{ moveDown() method
	public void moveDown(int index)
	{
		Object obj = entries.get(index);
		entries.remove(index);
		entries.add(index + 1,obj);
		fireTableRowsUpdated(index,index + 1);
	} //}}}

	//{{{ save() method
	void save()
	{
		int i;
		for(i = 0; i < entries.size(); i++)
		{
			Entry entry = (Entry)entries.get(i);
			jEdit.setProperty("vfs.browser.colors." + i + ".glob",
				entry.glob);
			jEdit.setColorProperty("vfs.browser.colors." + i + ".color",
				entry.color);
		}
		jEdit.unsetProperty("vfs.browser.colors." + i + ".glob");
		jEdit.unsetProperty("vfs.browser.colors." + i + ".color");
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return entries.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		Entry entry = (Entry)entries.get(row);

		switch(col)
		{
		case 0:
			return entry.glob;
		case 1:
			return entry.color;
		default:
			return null;
		}
	} //}}}

	//{{{ isCellEditable() method
	public boolean isCellEditable(int row, int col)
	{
		return (col == 0);
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		Entry entry = (Entry)entries.get(row);

		if(col == 0)
			entry.glob = (String)value;
		else
			entry.color = (Color)value;

		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.browser.colors.glob");
		case 1:
			return jEdit.getProperty("options.browser.colors.color");
		default:
			return null;
		}
	} //}}}

	//{{{ getColumnClass() method
	public Class getColumnClass(int col)
	{
		switch(col)
		{
		case 0:
			return String.class;
		case 1:
			return Color.class;
		default:
			throw new InternalError();
		}
	} //}}}

	private ArrayList entries;

	//{{{ Entry class
	static class Entry
	{
		String glob;
		Color color;

		Entry(String glob, Color color)
		{
			this.glob = glob;
			this.color = color;
		}
	} //}}}

	//{{{ ColorRenderer class
	static class ColorRenderer extends JLabel
		implements TableCellRenderer
	{
		//{{{ ColorRenderer constructor
		public ColorRenderer()
		{
			setOpaque(true);
			setBorder(SyntaxHiliteOptionPane.noFocusBorder);
		} //}}}

		//{{{ getTableCellRendererComponent() method
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean cellHasFocus,
			int row,
			int col)
		{
			if (isSelected)
			{
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			}
			else
			{
				setBackground(table.getBackground());
				setForeground(table.getForeground());
			}

			if (value != null)
				setBackground((Color)value);

			setBorder((cellHasFocus) ? UIManager.getBorder(
				"Table.focusCellHighlightBorder")
				: SyntaxHiliteOptionPane.noFocusBorder);
			return this;
		} //}}}
	} //}}}
}
