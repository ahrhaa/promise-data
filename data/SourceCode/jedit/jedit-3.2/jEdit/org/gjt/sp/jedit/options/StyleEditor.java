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
	StyleEditor(Component comp, SyntaxStyle style)
	{
		super(JOptionPane.getFrameForComponent(comp),
			jEdit.getProperty("style-editor.title"),true);

		JPanel content = new JPanel(new BorderLayout());
		content.setBorder(new EmptyBorder(12,12,12,12));
		setContentPane(content);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
		panel.setBorder(new EmptyBorder(0,0,12,0));
		panel.add(italics = new JCheckBox(
			jEdit.getProperty("style-editor.italics")));
		italics.setSelected(style.getFont().isItalic());
		panel.add(Box.createHorizontalStrut(2));
		panel.add(bold = new JCheckBox(
			jEdit.getProperty("style-editor.bold")));
		bold.setSelected(style.getFont().isBold());
		panel.add(Box.createHorizontalStrut(12));
		panel.add(new JLabel(jEdit.getProperty("style-editor.fgColor")));
		panel.add(Box.createHorizontalStrut(12));
		panel.add(fgColor = new JButton("    "));
		fgColor.setBackground(style.getForegroundColor());
		fgColor.setRequestFocusEnabled(false);
		fgColor.addActionListener(this);
		fgColor.setMargin(new Insets(0,0,0,0));
		panel.add(Box.createHorizontalStrut(12));
		panel.add(new JLabel(jEdit.getProperty("style-editor.bgColor")));
		panel.add(Box.createHorizontalStrut(12));
		panel.add(bgColor = new JButton("    "));
		if(style.getBackgroundColor() == null)
			bgColor.setBackground(GUIUtilities.parseColor(jEdit.getProperty("view.bgColor")));
		else
			bgColor.setBackground(style.getBackgroundColor());
		bgColor.setRequestFocusEnabled(false);
		bgColor.addActionListener(this);
		bgColor.setMargin(new Insets(0,0,0,0));

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
	}

	public void actionPerformed(ActionEvent evt)
	{
		Object source = evt.getSource();
		if(source == ok)
			ok();
		else if(source == cancel)
			cancel();
		else if (source == fgColor || source == bgColor)
		{
			JButton b = (JButton)source;
			Color c = JColorChooser.showDialog(this,
				jEdit.getProperty("colorChooser.title"),
				b.getBackground());
			if(c != null)
				b.setBackground(c);
		}
	}

	// EnhancedDialog implementation
	public void ok()
	{
		okClicked = true;
		dispose();
	}

	public void cancel()
	{
		dispose();
	}

	public SyntaxStyle getStyle()
	{
		if(!okClicked)
			return null;

		Color background = bgColor.getBackground();

		if (background.equals(GUIUtilities.parseColor(jEdit.getProperty("view.bgColor"))))
			background = null;

		return new SyntaxStyle(fgColor.getBackground(),background,
				new Font("Dialog",
				(italics.isSelected() ? Font.ITALIC : 0)
				| (bold.isSelected() ? Font.BOLD : 0),
				12));
	}

	// private members
	private JCheckBox italics;
	private JCheckBox bold;
	private JButton fgColor;
	private JButton bgColor;
	private JButton ok;
	private JButton cancel;
	private boolean okClicked;
}
