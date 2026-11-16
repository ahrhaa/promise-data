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

/**
 * Color option pane.
 * @author Slava Pestov
 * @version $Id: ColorOptionPane.java,v 1.6 2001/11/23 09:08:49 spestov Exp $
 */
public class ColorOptionPane extends AbstractOptionPane
{
	public static final EmptyBorder noFocusBorder = new EmptyBorder(1,1,1,1);

	//{{{ ColorOptionPane constructor
	public ColorOptionPane()
	{
		super("color");
	} //}}}

	//{{{ Protected members

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER,createColorTableScroller());
	} //}}}

	//{{{ _save() method
	protected void _save()
	{
		colorModel.save();
	} //}}}

	//}}}

	//{{{ Private members
	private ColorTableModel colorModel;
	private JTable colorTable;

	//{{{ createColorTableScroller() method
	private JScrollPane createColorTableScroller()
	{
		colorModel = createColorTableModel();
		colorTable = new JTable(colorModel);
		colorTable.setRowSelectionAllowed(false);
		colorTable.setColumnSelectionAllowed(false);
		colorTable.setCellSelectionEnabled(false);
		colorTable.getTableHeader().setReorderingAllowed(false);
		colorTable.addMouseListener(new MouseHandler());
		TableColumnModel tcm = colorTable.getColumnModel();
 		TableColumn colorColumn = tcm.getColumn(1);
		colorColumn.setCellRenderer(new ColorTableModel.ColorRenderer());
		Dimension d = colorTable.getPreferredSize();
		d.height = Math.min(d.height,100);
		JScrollPane scroller = new JScrollPane(colorTable);
		scroller.setPreferredSize(d);
		return scroller;
	} //}}}

	//{{{ createColorTableModel() method
	private ColorTableModel createColorTableModel()
	{
		return new ColorTableModel();
	} //}}}

	//}}}

	//{{{ MouseHandler class
	class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent evt)
		{
			int row = colorTable.rowAtPoint(evt.getPoint());
			if(row == -1)
				return;

			Color color = JColorChooser.showDialog(
				ColorOptionPane.this,
				jEdit.getProperty("colorChooser.title"),
				(Color)colorModel.getValueAt(row,1));
			if(color != null)
				colorModel.setValueAt(color,row,1);
		}
	} //}}}
}
