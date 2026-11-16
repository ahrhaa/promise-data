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

class JTableAppenderModel extends AbstractTableModel {

  CyclicBuffer cb;
  
  JTableAppenderModel(int size) {
    cb = new CyclicBuffer(size);
  }

  public
  void add(LoggingEvent event) {
    //System.out.println("JListViewModel.add called");
    cb.add(event);
    int j = cb.length();

    fireTableDataChanged();

  }
  public 
  int getColumnCount() { 
    return 1; 
  }

  public int getRowCount() { 
    return cb.length();
  }

  //public
  //Class getColumnClass(int index) {
  //  System.out.println("getColumnClass called " + index);
  //  return LoggingEvent.class;
  //}

  public 
  Object getValueAt(int row, int col) {
    return cb.get(row);
  }
}
