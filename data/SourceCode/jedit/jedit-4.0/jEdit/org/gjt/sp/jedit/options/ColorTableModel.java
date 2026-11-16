package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.*;

class ColorTableModel extends AbstractTableModel
{
	private Vector colorChoices;

	//{{{ ColorTableModel constructor
	ColorTableModel()
	{
		colorChoices = new Vector(16);
		addColorChoice("options.color.bgColor","view.bgColor");
		addColorChoice("options.color.fgColor","view.fgColor");
		addColorChoice("options.color.caretColor","view.caretColor");
		addColorChoice("options.color.selectionColor",
			"view.selectionColor");
		addColorChoice("options.color.foldedLineColor",
			"view.foldedLineColor");
		addColorChoice("options.color.lineHighlightColor",
			"view.lineHighlightColor");
		addColorChoice("options.color.bracketHighlightColor",
			"view.bracketHighlightColor");
		addColorChoice("options.color.eolMarkerColor",
			"view.eolMarkerColor");
		addColorChoice("options.color.wrapGuideColor",
			"view.wrapGuideColor");
		addColorChoice("options.color.gutterBgColor",
			"view.gutter.bgColor");
		addColorChoice("options.color.gutterFgColor",
			"view.gutter.fgColor");
		addColorChoice("options.color.gutterHighlightColor",
			"view.gutter.highlightColor");
		addColorChoice("options.color.gutterCurrentLineColor",
			"view.gutter.currentLineColor");
		addColorChoice("options.color.gutterBracketHighlightColor",
			"view.bracketHighlightColor");
		addColorChoice("options.color.gutterMarkerColor",
			"view.gutter.markerColor");
		addColorChoice("options.color.gutterFoldColor",
			"view.gutter.foldColor");
		addColorChoice("options.color.gutterFocusBorderColor",
			"view.gutter.focusBorderColor");
		addColorChoice("options.color.gutterNoFocusBorderColor",
			"view.gutter.noFocusBorderColor");
		MiscUtilities.quicksort(colorChoices,new MiscUtilities.StringCompare());
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return colorChoices.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		ColorChoice ch = (ColorChoice)colorChoices.elementAt(row);
		switch(col)
		{
		case 0:
			return ch.label;
		case 1:
			return ch.color;
		default:
			return null;
		}
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		ColorChoice ch = (ColorChoice)colorChoices.elementAt(row);
		if(col == 1)
			ch.color = (Color)value;
		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.color.object");
		case 1:
			return jEdit.getProperty("options.color.color");
		default:
			return null;
		}
	} //}}}

	//{{{ save() method
	public void save()
	{
		for(int i = 0; i < colorChoices.size(); i++)
		{
			ColorChoice ch = (ColorChoice)colorChoices
				.elementAt(i);
			jEdit.setColorProperty(ch.property,ch.color);
		}
	} //}}}

	//{{{ addColorChoice() method
	private void addColorChoice(String label, String property)
	{
		colorChoices.addElement(new ColorChoice(jEdit.getProperty(label),
			property,jEdit.getColorProperty(property)));
	} //}}}

	//{{{ ColorChoice class
	static class ColorChoice
	{
		String label;
		String property;
		Color color;

		ColorChoice(String label, String property, Color color)
		{
			this.label = label;
			this.property = property;
			this.color = color;
		}

		// for sorting
		public String toString()
		{
			return label;
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
			setBorder(StyleOptionPane.noFocusBorder);
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
				: StyleOptionPane.noFocusBorder);
			return this;
		} //}}}
	} //}}}
}
