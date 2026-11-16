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

class StyleEditor extends EnhancedDialog implements ActionListener
{
	//{{{ StyleEditor constructor
	StyleEditor(Component comp, SyntaxStyle style)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("style-editor.title"),true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);

		GridBagConstraints cons = new GridBagConstraints();
		cons.gridx = cons.gridy = 0;
		cons.gridwidth = 2;
		cons.gridheight = 1;
		cons.fill = GridBagConstraints.BOTH;
		cons.weightx = 0.0f;
		cons.insets = new Insets(0,0,12,0);

		italics = new JCheckBox(jEdit.getProperty("style-editor.italics"));
		italics.setSelected(style.getFont().isItalic());
		layout.setConstraints(italics,cons);
		panel.add(italics);

		cons.gridy++;
		bold = new JCheckBox(jEdit.getProperty("style-editor.bold"));
		bold.setSelected(style.getFont().isBold());
		layout.setConstraints(bold,cons);
		panel.add(bold);

		cons.gridy++;
		cons.gridwidth = 1;
		Color fg = style.getForegroundColor();
		fgColorCheckBox = new JCheckBox(jEdit.getProperty("style-editor.fgColor"));
		fgColorCheckBox.setSelected(fg != null);
		fgColorCheckBox.addActionListener(this);
		layout.setConstraints(fgColorCheckBox,cons);
		panel.add(fgColorCheckBox);

		cons.gridx++;
		fgColor = new JButton("    ");
		fgColor.setEnabled(fg != null);
		fgColor.setRequestFocusEnabled(false);
		fgColor.addActionListener(this);
		fgColor.setMargin(new Insets(0,0,0,0));
		if(fg == null)
			fgColor.setBackground(jEdit.getColorProperty("view.fgColor"));
		else
			fgColor.setBackground(fg);
		layout.setConstraints(fgColor,cons);
		panel.add(fgColor);

		cons.gridx = 0;
		cons.gridy++;
		Color bg = style.getBackgroundColor();
		bgColorCheckBox = new JCheckBox(jEdit.getProperty("style-editor.bgColor"));
		bgColorCheckBox.setSelected(bg != null);
		bgColorCheckBox.addActionListener(this);
		layout.setConstraints(bgColorCheckBox,cons);
		panel.add(bgColorCheckBox);

		cons.gridx++;
		bgColor = new JButton("    ");
		bgColor.setEnabled(bg != null);
		bgColor.setRequestFocusEnabled(false);
		bgColor.addActionListener(this);
		bgColor.setMargin(new Insets(0,0,0,0));
		if(bg == null)
			bgColor.setBackground(jEdit.getColorProperty("view.bgColor"));
		else
			bgColor.setBackground(bg);
		layout.setConstraints(bgColor,cons);
		panel.add(bgColor);

		content.add(BorderLayout.CENTER,panel);

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createGlue());
		box.add(ok = new JButton(jEdit.getProperty("common.ok")));
		getRootPane().setDefaultButton(ok);
		ok.addActionListener(this);
		box.add(Box.createHorizontalStrut(6));
		box.add(cancel = new JButton(jEdit.getProperty("common.cancel")));
		cancel.addActionListener(this);
		box.add(Box.createGlue());

		content.add(BorderLayout.SOUTH,box);

		Dimension screen = getToolkit().getScreenSize();
		pack();
		setLocationRelativeTo(JOptionPane.getFrameForComponent(comp));
		show();
	} //}}}

	//{{{ actionPerformed() method
	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if(source == ok)
			ok();
		else if(source == cancel)
			cancel();
		else if(source == fgColor || source == bgColor)
		{
			JButton b = (JButton)source;
			Color c = JColorChooser.showDialog(this,
				jEdit.getProperty("colorChooser.title"),
				b.getBackground());
			if(c != null)
				b.setBackground(c);
		}
		else if(source == fgColorCheckBox)
			fgColor.setEnabled(fgColorCheckBox.isSelected());
		else if(source == bgColorCheckBox)
			bgColor.setEnabled(bgColorCheckBox.isSelected());
	} //}}}

	//{{{ ok() method
	public void ok()
	{
		okClicked = true;
		dispose();
	} //}}}

	//{{{ cancel() method
	public void cancel()
	{
		dispose();
	} //}}}

	//{{{ getStyle() method
	public SyntaxStyle getStyle()
	{
		if(!okClicked)
			return null;

		Color foreground = (fgColorCheckBox.isSelected()
			? fgColor.getBackground()
			: null);

		Color background = (bgColorCheckBox.isSelected()
			? bgColor.getBackground()
			: null);

		return new SyntaxStyle(foreground,background,
				new Font("Dialog",
				(italics.isSelected() ? Font.ITALIC : 0)
				| (bold.isSelected() ? Font.BOLD : 0),
				12));
	} //}}}

	//{{{ Private members
	private JCheckBox italics;
	private JCheckBox bold;
	private JCheckBox fgColorCheckBox;
	private JButton fgColor;
	private JCheckBox bgColorCheckBox;
	private JButton bgColor;
	private JButton ok;
	private JButton cancel;
	private boolean okClicked;
	//}}}
}
