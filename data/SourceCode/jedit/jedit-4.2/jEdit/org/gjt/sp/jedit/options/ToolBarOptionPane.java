package org.gjt.sp.jedit.options;

import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.util.Log;

/**
 * Tool bar editor.
 * @author Slava Pestov
 * @version $Id: ToolBarOptionPane.java,v 1.14 2004/06/28 06:45:27 spestov Exp $
 */
public class ToolBarOptionPane extends AbstractOptionPane
{
	//{{{ ToolBarOptionPane constructor
	public ToolBarOptionPane()
	{
		super("toolbar");
	} //}}}

	//{{{ _init() method
	protected void _init()
	{
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new GridLayout(2,1));

		/* Show toolbar */
		showToolbar = new JCheckBox(jEdit.getProperty(
			"options.toolbar.showToolbar"));
		showToolbar.setSelected(jEdit.getBooleanProperty("view.showToolbar"));
		panel.add(showToolbar);

		panel.add(new JLabel(jEdit.getProperty(
			"options.toolbar.caption")));

		add(BorderLayout.NORTH,panel);

		String toolbar = jEdit.getProperty("view.toolbar");
		StringTokenizer st = new StringTokenizer(toolbar);
		listModel = new DefaultListModel();
		while(st.hasMoreTokens())
		{
			String actionName = (String)st.nextToken();
			if(actionName.equals("-"))
				listModel.addElement(new ToolBarOptionPane.Button("-",null,null,"-"));
			else
			{
				EditAction action = jEdit.getAction(actionName);
				if(action == null)
					continue;
				String label = action.getLabel();
				if(label == null)
					continue;

				Icon icon;
				String iconName;
				if(actionName.equals("-"))
				{
					iconName = null;
					icon = null;
				}
				else
				{
					iconName = jEdit.getProperty(actionName + ".icon");
					if(iconName == null)
						icon = GUIUtilities.loadIcon("BrokenImage.png");
					else
					{
						icon = GUIUtilities.loadIcon(iconName);
						if(icon == null)
							icon = GUIUtilities.loadIcon("BrokenImage.png");
					}
				}
				listModel.addElement(new Button(actionName,iconName,icon,label));
			}
		}

		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListHandler());
		list.setCellRenderer(new ButtonCellRenderer());

		add(BorderLayout.CENTER,new JScrollPane(list));

		//{{{ Create buttons
		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(3,0,0,0));
		buttons.setLayout(new BoxLayout(buttons,BoxLayout.X_AXIS));
		ActionHandler actionHandler = new ActionHandler();
		add = new RolloverButton(GUIUtilities.loadIcon("Plus.png"));
		add.setToolTipText(jEdit.getProperty("options.toolbar.add"));
		add.addActionListener(actionHandler);
		buttons.add(add);
		buttons.add(Box.createHorizontalStrut(6));
		remove = new RolloverButton(GUIUtilities.loadIcon("Minus.png"));
		remove.setToolTipText(jEdit.getProperty("options.toolbar.remove"));
		remove.addActionListener(actionHandler);
		buttons.add(remove);
		buttons.add(Box.createHorizontalStrut(6));
		moveUp = new RolloverButton(GUIUtilities.loadIcon("ArrowU.png"));
		moveUp.setToolTipText(jEdit.getProperty("options.toolbar.moveUp"));
		moveUp.addActionListener(actionHandler);
		buttons.add(moveUp);
		buttons.add(Box.createHorizontalStrut(6));
		moveDown = new RolloverButton(GUIUtilities.loadIcon("ArrowD.png"));
		moveDown.setToolTipText(jEdit.getProperty("options.toolbar.moveDown"));
		moveDown.addActionListener(actionHandler);
		buttons.add(moveDown);
		buttons.add(Box.createHorizontalStrut(6));
		edit = new RolloverButton(GUIUtilities.loadIcon("ButtonProperties.png"));
		edit.setToolTipText(jEdit.getProperty("options.toolbar.edit"));
		edit.addActionListener(actionHandler);
		buttons.add(edit);
		buttons.add(Box.createGlue());
		//}}}

		updateButtons();
		add(BorderLayout.SOUTH,buttons);

		//{{{ Ceate icons list
		iconList = new DefaultComboBoxModel();
		st = new StringTokenizer(jEdit.getProperty("icons"));
		while(st.hasMoreElements())
		{
			String icon = st.nextToken();
			iconList.addElement(new IconListEntry(
				GUIUtilities.loadIcon(icon),icon));
		} //}}}
	} ///}}}

	//{{{ _save() method
	protected void _save()
	{
		jEdit.setBooleanProperty("view.showToolbar",showToolbar
			.isSelected());

		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < listModel.getSize(); i++)
		{
			if(i != 0)
				buf.append(' ');
			Button button = (Button)listModel.elementAt(i);
			buf.append(button.actionName);
			jEdit.setProperty(button.actionName + ".icon",button.iconName);
		}
		jEdit.setProperty("view.toolbar",buf.toString());
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private JCheckBox showToolbar;
	private DefaultListModel listModel;
	private JList list;
	private RolloverButton add;
	private RolloverButton remove;
	private RolloverButton moveUp, moveDown;
	private RolloverButton edit;

	private DefaultComboBoxModel iconList;
	//}}}

	//{{{ updateButtons() method
	private void updateButtons()
	{
		int index = list.getSelectedIndex();
		remove.setEnabled(index != -1 && listModel.getSize() != 0);
		moveUp.setEnabled(index > 0);
		moveDown.setEnabled(index != -1 && index != listModel.getSize() - 1);
		edit.setEnabled(index != -1);
	} //}}}

	//}}}

	//{{{ Inner classes

	//{{{ ButtonCompare class
	static class ButtonCompare implements MiscUtilities.Compare
	{
		public int compare(Object obj1, Object obj2)
		{
			return MiscUtilities.compareStrings(
				((Button)obj1).label,
				((Button)obj2).label,
				true);
		}
	} //}}}

	//{{{ Button class
	static class Button
	{
		String actionName;
		String iconName;
		Icon icon;
		String label;

		Button(String actionName, String iconName, Icon icon, String label)
		{
			this.actionName = actionName;
			this.iconName = iconName;
			this.icon = icon;
			this.label = GUIUtilities.prettifyMenuLabel(label);
		}

		public String toString()
		{
			return label;
		}

		public boolean equals(Object o)
		{
			if(o instanceof Button)
				return ((Button)o).actionName.equals(actionName);
			else
				return false;
		}
	} //}}}

	//{{{ IconListEntry class
	static class IconListEntry
	{
		Icon icon;
		String name;

		IconListEntry(Icon icon, String name)
		{
			this.icon = icon;
			this.name = name;
		}

		public String toString()
		{
			return name;
		}
	} //}}}

	//{{{ ButtonCellRenderer class
	static class ButtonCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,value,index,
				isSelected,cellHasFocus);

			Button button = (Button)value;
			setIcon(button.icon);

			return this;
		}
	} //}}}

	//{{{ IconCellRenderer class
	static class IconCellRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list,value,index,
				isSelected,cellHasFocus);

			IconListEntry icon = (IconListEntry)value;
			setIcon(icon.icon);

			return this;
		}
	} //}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();

			if(source == add)
			{
				ToolBarEditDialog dialog = new ToolBarEditDialog(
					ToolBarOptionPane.this,iconList,null);
				Button selection = dialog.getSelection();
				if(selection == null)
					return;

				int index = list.getSelectedIndex();
				if(index == -1)
					index = listModel.getSize();
				else
					index++;

				listModel.insertElementAt(selection,index);
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
			else if(source == remove)
			{
				int index = list.getSelectedIndex();
				listModel.removeElementAt(index);
				if(listModel.getSize() != 0)
				{
					if(listModel.getSize() == index)
						list.setSelectedIndex(index-1);
					else
						list.setSelectedIndex(index);
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
				list.ensureIndexIsVisible(index-1);
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
			else if(source == edit)
			{
				ToolBarEditDialog dialog = new ToolBarEditDialog(
					ToolBarOptionPane.this,iconList,
					(Button)list.getSelectedValue());
				Button selection = dialog.getSelection();
				if(selection == null)
					return;

				int index = list.getSelectedIndex();

				listModel.setElementAt(selection,index);
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}
		}
	} //}}}

	//{{{ ListHandler class
	class ListHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent evt)
		{
			updateButtons();
		}
	} //}}}

	//}}}
}
