package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import org.gjt.sp.jedit.syntax.*;
import org.gjt.sp.jedit.gui.ColorWellButton;
import org.gjt.sp.jedit.gui.EnhancedDialog;
import org.gjt.sp.jedit.*;

class StyleTableModel extends AbstractTableModel
{
	private Vector styleChoices;

	//{{{ StyleTableModel constructor
	StyleTableModel()
	{
		styleChoices = new Vector(Token.ID_COUNT + 4);
		// start at 1 not 0 to skip Token.NULL
		for(int i = 1; i < Token.ID_COUNT; i++)
		{
			String tokenName = Token.tokenToString((byte)i);
			addStyleChoice(tokenName,"view.style." + tokenName.toLowerCase());
		}

		addStyleChoice(jEdit.getProperty("options.syntax.foldLine.1"),
			"view.style.foldLine.1");
		addStyleChoice(jEdit.getProperty("options.syntax.foldLine.2"),
			"view.style.foldLine.2");
		addStyleChoice(jEdit.getProperty("options.syntax.foldLine.3"),
			"view.style.foldLine.3");
		addStyleChoice(jEdit.getProperty("options.syntax.foldLine.0"),
			"view.style.foldLine.0");

		MiscUtilities.quicksort(styleChoices,new MiscUtilities.StringICaseCompare());
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
			return jEdit.getProperty("options.syntax.object");
		case 1:
			return jEdit.getProperty("options.syntax.style");
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
		styleChoices.addElement(new StyleChoice(label,
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
			setBorder(SyntaxHiliteOptionPane.noFocusBorder);
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
				: SyntaxHiliteOptionPane.noFocusBorder);
			return this;
		} //}}}
	} //}}}
}
