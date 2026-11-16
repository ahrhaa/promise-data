package org.gjt.sp.jedit.options;

import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import org.gjt.sp.jedit.gui.*;
import org.gjt.sp.jedit.*;

class Abbrev
{
	Abbrev() {}

	Abbrev(String abbrev, String expand)
	{
		this.abbrev = abbrev;
		this.expand = expand;
	}

	String abbrev;
	String expand;
}
