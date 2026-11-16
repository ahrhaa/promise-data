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

class ToolBarEditDialog extends EnhancedDialog
{
	//{{{ ToolBarEditDialog constructor
	public ToolBarEditDialog(Component comp,
		DefaultComboBoxModel iconListModel,
		ToolBarOptionPane.Button current)
	{
		super(GUIUtilities.getParentDialog(comp),
			jEdit.getProperty("options.toolbar.edit.title"),
			true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		ActionHandler actionHandler = new ActionHandler();
		ButtonGroup grp = new ButtonGroup();

		JPanel typePanel = new JPanel(new GridLayout(3,1,6,6));
		typePanel.setBorder(new EmptyBorder(0,0,6,0));
		typePanel.add(new JLabel(
			jEdit.getProperty("options.toolbar.edit.caption")));

		separator = new JRadioButton(jEdit.getProperty("options.toolbar"
			+ ".edit.separator"));
		separator.addActionListener(actionHandler);
		grp.add(separator);
		typePanel.add(separator);

		action = new JRadioButton(jEdit.getProperty("options.toolbar"
			+ ".edit.action"));
		action.addActionListener(actionHandler);
		grp.add(action);
		typePanel.add(action);

		content.add(BorderLayout.NORTH,typePanel);

		JPanel actionPanel = new JPanel(new BorderLayout(6,6));

		ActionSet[] actionsList = jEdit.getActionSets();
		Vector vec = new Vector(actionsList.length);
		for(int i = 0; i < actionsList.length; i++)
		{
			ActionSet actionSet = actionsList[i];
			if(actionSet.getActionCount() != 0)
				vec.addElement(actionSet);
		}
		combo = new JComboBox(vec);
		combo.addActionListener(actionHandler);
		actionPanel.add(BorderLayout.NORTH,combo);

		list = new JList();
		list.setVisibleRowCount(8);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		actionPanel.add(BorderLayout.CENTER,new JScrollPane(list));

		// Icon selection
		JPanel iconPanel = new JPanel(new BorderLayout(0,3));
		JPanel labelPanel = new JPanel(new GridLayout(2,1));
		labelPanel.setBorder(new EmptyBorder(0,0,0,12));
		JPanel compPanel = new JPanel(new GridLayout(2,1));
		grp = new ButtonGroup();
		labelPanel.add(builtin = new JRadioButton(jEdit.getProperty(
			"options.toolbar.edit.builtin")));
		builtin.addActionListener(actionHandler);
		grp.add(builtin);
		labelPanel.add(file = new JRadioButton(jEdit.getProperty(
			"options.toolbar.edit.file")));
		grp.add(file);
		file.addActionListener(actionHandler);
		iconPanel.add(BorderLayout.WEST,labelPanel);
		builtinCombo = new JComboBox(iconListModel);
		builtinCombo.setRenderer(new ToolBarOptionPane.IconCellRenderer());
		compPanel.add(builtinCombo);

		fileButton = new JButton(jEdit.getProperty("options.toolbar.edit.no-icon"));
		fileButton.setMargin(new Insets(1,1,1,1));
		fileButton.setIcon(GUIUtilities.loadIcon("Blank24.gif"));
		fileButton.setHorizontalAlignment(SwingConstants.LEFT);
		fileButton.addActionListener(actionHandler);
		compPanel.add(fileButton);
		iconPanel.add(BorderLayout.CENTER,compPanel);
		actionPanel.add(BorderLayout.SOUTH,iconPanel);

		content.add(BorderLayout.CENTER,actionPanel);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel,BoxLayout.X_AXIS));
		southPanel.setBorder(new EmptyBorder(12,0,0,0));
		southPanel.add(Box.createGlue());
		ok = new JButton(jEdit.getProperty("common.ok"));
		ok.addActionListener(actionHandler);
		getRootPane().setDefaultButton(ok);
		southPanel.add(ok);
		southPanel.add(Box.createHorizontalStrut(6));
		cancel = new JButton(jEdit.getProperty("common.cancel"));
		cancel.addActionListener(actionHandler);
		southPanel.add(cancel);
		southPanel.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,southPanel);

		if(current == null)
		{
			action.setSelected(true);
			builtin.setSelected(true);
			updateList();
		}
		else
		{
			if(current.actionName.equals("-"))
			{
				separator.setSelected(true);
				builtin.setSelected(true);
			}
			else
			{
				action.setSelected(true);
				ActionSet set = jEdit.getActionSetForAction(
					current.actionName);
				combo.setSelectedItem(set);
				updateList();
				list.setSelectedValue(current,true);

				if(MiscUtilities.isURL(current.iconName))
				{
					file.setSelected(true);
					fileIcon = current.iconName;
					try
					{
						fileButton.setIcon(new ImageIcon(new URL(
							fileIcon)));
					}
					catch(MalformedURLException mf)
					{
						Log.log(Log.ERROR,this,mf);
					}
					fileButton.setText(MiscUtilities.getFileName(fileIcon));
				}
				else
				{
					String iconName = MiscUtilities.getFileName(current.iconName);
					builtin.setSelected(true);
					ListModel model = builtinCombo.getModel();
					for(int i = 0; i < model.getSize(); i++)
					{
						ToolBarOptionPane.IconListEntry entry
							= (ToolBarOptionPane.IconListEntry)
							model.getElementAt(i);
						if(entry.name.equals(iconName))
						{
							builtinCombo.setSelectedIndex(i);
							break;
						}
					}
				}
			}
		}

		updateEnabled();

		pack();
		setLocationRelativeTo(GUIUtilities.getParentDialog(comp));
		setVisible(true);
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		isOK = true;
		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}

	//{{{ getSelection() method
	public ToolBarOptionPane.Button getSelection()
	{
		if(!isOK)
			return null;

		if(separator.isSelected())
			return new ToolBarOptionPane.Button("-",null,null,"-");
		else
		{
			Icon icon;
			String iconName;
			if(builtin.isSelected())
			{
				ToolBarOptionPane.IconListEntry selectedIcon =
					(ToolBarOptionPane.IconListEntry)
					builtinCombo.getSelectedItem();
				icon = selectedIcon.icon;
				iconName = selectedIcon.name;
			}
			else
			{
				icon = fileButton.getIcon();
				iconName = fileIcon;
				if(iconName == null)
					iconName = "Blank24.gif";
			}

			String label;
			String actionName;
			if(action.isSelected())
			{
				ToolBarOptionPane.Button button =
					(ToolBarOptionPane.Button)list
					.getSelectedValue();
				label = button.label;
				actionName = button.actionName;
			}
			else
				throw new InternalError();

			return new ToolBarOptionPane.Button(actionName,
				iconName,icon,label);
		}
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private boolean isOK;
	private JRadioButton separator, action;
	private JComboBox combo;
	private JList list;
	private JRadioButton builtin;
	private JComboBox builtinCombo;
	private JRadioButton file;
	private JButton fileButton;
	private String fileIcon;
	private JButton ok, cancel;
	//}}}

	//{{{ updateEnabled() method
	private void updateEnabled()
	{
		combo.setEnabled(action.isSelected());
		list.setEnabled(action.isSelected());

		boolean iconControlsEnabled = !separator.isSelected();
		builtin.setEnabled(iconControlsEnabled);
		file.setEnabled(iconControlsEnabled);
		builtinCombo.setEnabled(iconControlsEnabled && builtin.isSelected());
		fileButton.setEnabled(iconControlsEnabled && file.isSelected());
	} //}}}

	//{{{ updateList() method
	private void updateList()
	{
		ActionSet actionSet = (ActionSet)combo.getSelectedItem();
		EditAction[] actions = actionSet.getActions();
		Vector listModel = new Vector(actions.length);

		for(int i = 0; i < actions.length; i++)
		{
			EditAction action = actions[i];
			String label = action.getLabel();
			if(label == null)
				continue;

			listModel.addElement(new ToolBarOptionPane.Button(
				action.getName(),null,null,label));
		}

		MiscUtilities.quicksort(listModel,new ToolBarOptionPane.ButtonCompare());
		list.setListData(listModel);
	} //}}}

	//}}}

	//{{{ ActionHandler class
	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Object source = evt.getSource();
			if(source instanceof JRadioButton)
				updateEnabled();
			if(source == ok)
				ok();
			else if(source == cancel)
				cancel();
			else if(source == combo)
				updateList();
			else if(source == fileButton)
			{
				String directory;
				if(fileIcon == null)
					directory = null;
				else
					directory = MiscUtilities.getParentOfPath(fileIcon);
				String paths[] = GUIUtilities.showVFSFileDialog(null,directory,
					VFSBrowser.OPEN_DIALOG,false);
				if(paths == null)
					return;

				fileIcon = "file:" + paths[0];

				try
				{
					fileButton.setIcon(new ImageIcon(new URL(
						fileIcon)));
				}
				catch(MalformedURLException mf)
				{
					Log.log(Log.ERROR,this,mf);
				}
				fileButton.setText(MiscUtilities.getFileName(fileIcon));
			}
		}
	} //}}}
}
