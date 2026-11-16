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
 * @version $Id: ContextOptionPane.java,v 1.13 2001/03/26 07:09:20 sp Exp $
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
			String label = getActionLabel(actionName);
			if(label == null)
				continue;
			listModel.addElement(new MenuItem(actionName,label));
		}
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListHandler());

		add(BorderLayout.CENTER,new JScrollPane(list));

		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(3,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		buttons.add(Box.createGlue());
		ActionHandler actionHandler = new ActionHandler();
		add = new JButton(jEdit.getProperty("options.context.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new JButton(jEdit.getProperty("options.context.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		buttons.add(Box.createHorizontalStrut(6));
		moveUp = new JButton(jEdit.getProperty("options.context.moveUp"));
		moveUp.addActionListener(actionHandler);
		buttons.add(moveUp);
		buttons.add(Box.createHorizontalStrut(6));
		moveDown = new JButton(jEdit.getProperty("options.context.moveDown"));
		moveDown.addActionListener(actionHandler);
		buttons.add(moveDown);
		buttons.add(Box.createGlue());

		updateButtons();
		add(BorderLayout.SOUTH,buttons);

		// create actions list
		EditAction[] actions = jEdit.getActions();
		Vector vector = new Vector(actions.length);
		for(int i = 0; i < actions.length; i++)
		{
			String actionName = actions[i].getName();
			String label = jEdit.getProperty(actionName + ".label");
			if(label == null)
				continue;
			vector.addElement(new MenuItem(actionName,label));
		}
		MiscUtilities.quicksort(vector,new MenuItemCompare());

		actionsList = new DefaultListModel();
		actionsList.ensureCapacity(vector.size());
		for(int i = 0; i < vector.size(); i++)
		{
			actionsList.addElement(vector.elementAt(i));
		}
	}

	class MenuItemCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return ((MenuItem)obj1).label.toLowerCase().compareTo(
				((MenuItem)obj2).label.toLowerCase());
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

	// package-private members
	static String getActionLabel(String actionName)
	{
		if(actionName.equals("-"))
			return "-";
		else
		{
			if(actionName.startsWith("play-macro@"))
			{
				int index = Math.max(11,actionName
					.indexOf('/') + 1);
				return actionName.substring(index)
					.replace('_',' ');
			}
			else
				return jEdit.getProperty(actionName + ".label");
		}
	}

	// private members
	private DefaultListModel listModel;
	private JList list;
	private JButton add;
	private JButton remove;
	private JButton moveUp, moveDown;

	private DefaultListModel actionsList;

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
			if(label.equals("-"))
				this.label = label;
			else
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
					ContextOptionPane.this,
					actionsList);
				MenuItem selection = dialog.getSelection();
				if(selection == null)
					return;

				int index = list.getSelectedIndex();
				if(index == -1)
					index = listModel.getSize();
				else
					index++;

				listModel.insertElementAt(selection,index);
				list.setSelectedIndex(index);
			}
			else if(source == remove)
			{
				int index = list.getSelectedIndex();
				listModel.removeElementAt(index);
				updateButtons();
			}
			else if(source == moveUp)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index-1);
				list.setSelectedIndex(index-1);
			}
			else if(source == moveDown)
			{
				int index = list.getSelectedIndex();
				Object selected = list.getSelectedValue();
				listModel.removeElementAt(index);
				listModel.insertElementAt(selected,index+1);
				list.setSelectedIndex(index+1);
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
