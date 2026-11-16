package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.syntax.SyntaxStyle;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.*;

class StyleTableModel extends AbstractTableModel
{
	private Vector styleChoices;

	//{{{ StyleTableModel constructor
	StyleTableModel()
	{
		styleChoices = new Vector(13);
		addStyleChoice("options.style.comment1Style","view.style.comment1");
		addStyleChoice("options.style.comment2Style","view.style.comment2");
		addStyleChoice("options.style.literal1Style","view.style.literal1");
		addStyleChoice("options.style.literal2Style","view.style.literal2");
		addStyleChoice("options.style.labelStyle","view.style.label");
		addStyleChoice("options.style.keyword1Style","view.style.keyword1");
		addStyleChoice("options.style.keyword2Style","view.style.keyword2");
		addStyleChoice("options.style.keyword3Style","view.style.keyword3");
		addStyleChoice("options.style.functionStyle","view.style.function");
		addStyleChoice("options.style.markupStyle","view.style.markup");
		addStyleChoice("options.style.operatorStyle","view.style.operator");
		addStyleChoice("options.style.digitStyle","view.style.digit");
		addStyleChoice("options.style.invalidStyle","view.style.invalid");
		MiscUtilities.quicksort(styleChoices,new MiscUtilities.StringCompare());
	} //}}}

	//{{{ getColumnCount() method
	public int getColumnCount()
	{
		return 2;
	} //}}}

	//{{{ getRowCount() method
	public int getRowCount()
	{
		return styleChoices.size();
	} //}}}

	//{{{ getValueAt() method
	public Object getValueAt(int row, int col)
	{
		StyleChoice ch = (StyleChoice)styleChoices.elementAt(row);
		switch(col)
		{
		case 0:
			return ch.label;
		case 1:
			return ch.style;
		default:
			return null;
		}
	} //}}}

	//{{{ setValueAt() method
	public void setValueAt(Object value, int row, int col)
	{
		StyleChoice ch = (StyleChoice)styleChoices.elementAt(row);
		if(col == 1)
			ch.style = (SyntaxStyle)value;
		fireTableRowsUpdated(row,row);
	} //}}}

	//{{{ getColumnName() method
	public String getColumnName(int index)
	{
		switch(index)
		{
		case 0:
			return jEdit.getProperty("options.style.object");
		case 1:
			return jEdit.getProperty("options.style.style");
		default:
			return null;
		}
	} //}}}

	//{{{ save() method
	public void save()
	{
		for(int i = 0; i < styleChoices.size(); i++)
		{
			StyleChoice ch = (StyleChoice)styleChoices
				.elementAt(i);
			jEdit.setProperty(ch.property,
				GUIUtilities.getStyleString(ch.style));
		}
	} //}}}

	//{{{ addStyleChoice() method
	private void addStyleChoice(String label, String property)
	{
		styleChoices.addElement(new StyleChoice(jEdit.getProperty(label),
			property,
			GUIUtilities.parseStyle(jEdit.getProperty(property),
			"Dialog",12)));
	} //}}}

	//{{{ StyleChoice class
	static class StyleChoice
	{
		String label;
		String property;
		SyntaxStyle style;

		StyleChoice(String label, String property, SyntaxStyle style)
		{
			this.label = label;
			this.property = property;
			this.style = style;
		}

		// for sorting
		public String toString()
		{
			return label;
		}
	} //}}}

	//{{{ StyleRenderer class
	static class StyleRenderer extends JLabel
		implements TableCellRenderer
	{
		//{{{ StyleRenderer constructor
		public StyleRenderer()
		{
			setOpaque(true);
			setBorder(StyleOptionPane.noFocusBorder);
			setText("Hello World");
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
			if (value != null)
			{
				SyntaxStyle style = (SyntaxStyle)value;
				setForeground(style.getForegroundColor());
				if (style.getBackgroundColor() != null) 
					setBackground(style.getBackgroundColor());
				else
				{
					// this part sucks
					setBackground(jEdit.getColorProperty(
						"view.bgColor"));
				}
				setFont(style.getFont());
			}

			setBorder((cellHasFocus) ? UIManager.getBorder(
				"Table.focusCellHighlightBorder")
				: StyleOptionPane.noFocusBorder);
			return this;
		} //}}}
	} //}}}
}
