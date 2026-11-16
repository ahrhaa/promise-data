package org.gjt.sp.jedit.gui;

import java.awt.event.*;
import java.awt.*;
import java.util.Vector;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.*;
import org.gjt.sp.jedit.jEdit;

/**
 * A font chooser widget.
 * @author Slava Pestov
 * @version $Id: FontSelector.java,v 1.4 2001/03/14 05:37:27 sp Exp $
 */
public class FontSelector extends JButton
{
	public FontSelector(Font font)
	{
		setFont(font);

		updateText();

		setRequestFocusEnabled(false);

		addActionListener(new ActionHandler());
	}

	// private members
	private void updateText()
	{
		Font font = getFont();
		String styleString;
		switch(font.getStyle())
		{
		case Font.PLAIN:
			styleString = jEdit.getProperty("font-selector.plain");
			break;
		case Font.BOLD:
			styleString = jEdit.getProperty("font-selector.bold");
			break;
		case Font.ITALIC:
			styleString = jEdit.getProperty("font-selector.italic");
			break;
		case Font.BOLD | Font.ITALIC:
			styleString = jEdit.getProperty("font-selector.bolditalic");
			break;
		default:
			styleString = "UNKNOWN!!!???";
			break;
		}

		setText(font.getFamily() + " " + font.getSize() + " " + styleString);
	}

	class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			Font font = new FontSelectorDialog(FontSelector.this,getFont())
				.getSelectedFont();
			if(font != null)
			{
				setFont(font);
				updateText();
			}
		}
	}
}
