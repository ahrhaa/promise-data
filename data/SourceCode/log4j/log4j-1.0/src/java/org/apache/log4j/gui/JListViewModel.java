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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

class JListViewModel extends AbstractListModel {

  CyclicBuffer cb;
  
  JListViewModel(int size) {
    cb = new CyclicBuffer(size);
  }

  public
  void add(LoggingEvent event) {
    //System.out.println("JListViewModel.add called");
    cb.add(event);
    int j = cb.length();
    fireContentsChanged(this, 0, j);
  }
    


  public
  Object getElementAt(int index) {
    return cb.get(index);
  }

  public
  int getSize() {
    return cb.length();
  }
  
}
