package org.gjt.sp.jedit.options;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

/**
 * Right-click context menu editor.
 * @author Slava Pestov
 * @version $Id: ContextOptionPane.java,v 1.11 2004/06/28 06:45:27 spestov Exp $
 */
public class ContextOptionPane extends AbstractOptionPane
{
	public ContextOptionPane()
	{
		super("context");
	}

	// protected members
	protected void _init()
	{
		setLayout(new BorderLayout());

		JLabel caption = new JLabel(jEdit.getProperty(
			"options.context.caption"));
		add(BorderLayout.NORTH,caption);

		String contextMenu = jEdit.getProperty("view.context");
		StringTokenizer st = new StringTokenizer(contextMenu);
		listModel = new DefaultListModel();
		while(st.hasMoreTokens())
		{
			String actionName = (String)st.nextToken();
			if(actionName.equals("-"))
				listModel.addElement(new ContextOptionPane.MenuItem("-","-"));
			else
			{
				EditAction action = jEdit.getAction(actionName);
				if(action == null)
					continue;
				String label = action.getLabel();
				if(label == null)
					continue;
				listModel.addElement(new ContextOptionPane.MenuItem(actionName,label));
			}
		}
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListHandler());

		add(BorderLayout.CENTER,new JScrollPane(list));

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(3,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		ActionHandler actionHandler = new ActionHandler();
		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.setToolTipText(jEdit.getProperty("common.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.setToolTipText(jEdit.getProperty("common.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		buttons.add(Box.createHorizontalStrut(6));
		moveUp = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		moveUp.setToolTipText(jEdit.getProperty("common.moveUp"));
		moveUp.addActionListener(actionHandler);
		buttons.add(moveUp);
		buttons.add(Box.createHorizontalStrut(6));
		moveDown = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		moveDown.setToolTipText(jEdit.getProperty("common.moveDown"));
		moveDown.addActionListener(actionHandler);
		buttons.add(moveDown);
		buttons.add(Box.createGlue());

		updateButtons();
		add(BorderLayout.SOUTH,buttons);
	}

	static class MenuItemCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return MiscUtilities.compareStrings(
				((MenuItem)obj1).label,
				((MenuItem)obj2).label,
				true);
		}
	}

	protected void _save()
	{
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < listModel.getSize(); i++)
		{
			if(i != 0)
				buf.append(' ');
			buf.append(((MenuItem)listModel.elementAt(i)).actionName);
		}
		jEdit.setProperty("view.context",buf.toString());
	}

	// private members
	private DefaultListModel listModel;
	private JList list;
	private JButton add;
	private JButton remove;
	private JButton moveUp, moveDown;

	private void updateButtons()
	{
		int index = list.getSelectedIndex();
		remove.setEnabled(index != -1 && listModel.getSize() != 0);
		moveUp.setEnabled(index > 0);
		moveDown.setEnabled(index != -1 && index != listModel.getSize() - 1);
	}

	static class MenuItem
	{
		String actionName;
		String label;

		MenuItem(String actionName, String label)
		{
			this.actionName = actionName;
			this.label = GUIUtilities.prettifyMenuLabel(label);
		}

		public String toString()
		{
			return label;
		}
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == add)
			{
				ContextAddDialog dialog = new ContextAddDialog(
					ContextOptionPane.this);
				String selection = dialog.getSelection();
				if(selection == null)
					return;

				int index = list.getSelectedIndex();
				if(index == -1)
					index = listModel.getSize();
				else
					index++;

				MenuItem menuItem;
				if(selection.equals("-"))
					menuItem = new ContextOptionPane.MenuItem("-","-");
				else
				{
					menuItem = new ContextOptionPane.MenuItem(selection,
						jEdit.getAction(selection)
						.getLabel());
				}

				listModel.insertElementAt(menuItem,index);
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
			else if(source == remove)
			{
				int index = list.getSelectedIndex();
				listModel.removeElementAt(index);
				if(listModel.getSize() != 0)
				{
					list.setSelectedIndex(
						Math.min(listModel.getSize()-1,
						index));
				}
				updateButtons();
			}
			else if(source == moveUp)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index-1);
				list.setSelectedIndex(index-1);
				list.ensureIndexIsVisible(index - 1);
			}
			else if(source == moveDown)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index+1);
				list.setSelectedIndex(index+1);
				list.ensureIndexIsVisible(index+1);
			}
		}
	}

	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateButtons();
		}
	}
}
