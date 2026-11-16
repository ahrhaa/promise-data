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

/**
 * Style option pane.
 * @author Slava Pestov
 * @version $Id: StyleOptionPane.java,v 1.6 2001/11/23 09:08:49 spestov Exp $
 */
public class StyleOptionPane extends AbstractOptionPane
{
	public static final EmptyBorder noFocusBorder = new EmptyBorder(1,1,1,1);

	//{{{ StyleOptionPane constructor
	public StyleOptionPane()
	{
		super("style");
	}
	//}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,createStyleTableScroller());
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		styleModel.save();
	} //}}}

	//}}}

	//{{{ Private members
	private StyleTableModel styleModel;
	private JTable styleTable;

	//{{{ createStyleTableScroller() method
	private JScrollPane createStyleTableScroller()
	{
		styleModel = createStyleTableModel();
		styleTable = new JTable(styleModel);
		styleTable.setRowSelectionAllowed(false);
		styleTable.setColumnSelectionAllowed(false);
		styleTable.setCellSelectionEnabled(false);
		styleTable.getTableHeader().setReorderingAllowed(false);
		styleTable.addMouseListener(new MouseHandler());
		TableColumnModel tcm = styleTable.getColumnModel();
 		TableColumn styleColumn = tcm.getColumn(1);
		styleColumn.setCellRenderer(new StyleTableModel.StyleRenderer());
		Dimension d = styleTable.getPreferredSize();
		d.height = Math.min(d.height,100);
		JScrollPane scroller = new JScrollPane(styleTable);
		scroller.setPreferredSize(d);
		return scroller;
	} //}}}

	//{{{ createStyleTableModel() method
	private StyleTableModel createStyleTableModel()
	{
		return new StyleTableModel();
	} //}}}

	//}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			int row = styleTable.rowAtPoint(evt.getPoint());
			if(row == -1)
				return;

			SyntaxStyle style = new StyleEditor(
				StyleOptionPane.this,
				(SyntaxStyle)styleModel.getValueAt(
				row,1)).getStyle();
			if(style != null)
				styleModel.setValueAt(style,row,1);
		}
	} //}}}
}
