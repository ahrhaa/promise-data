package org.apache.log4j.gui;

import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.awt.Rectangle;

class JTableAddAction implements ActionListener {
    
  int j;
  JTableAppender appender;

  Category cat = Category.getInstance("x");
  
  public
  JTableAddAction(JTableAppender appender) {
    this.appender = appender;
    j = 0;
  }
    
  public
  void actionPerformed(ActionEvent e) {
    System.out.println("Action occured");

    LoggingEvent event = new LoggingEvent("x", cat, Priority.DEBUG, 
					    "Message "+j, null);
    
    if(j % 5 == 0) {
      event.throwable = new Exception("hello "+j);
    }
    j++;
    appender.add(event);
  }
}
