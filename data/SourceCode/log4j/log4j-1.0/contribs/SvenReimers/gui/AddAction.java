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

class AddAction implements ActionListener {

  Thread t;

  static int counter = 0;

  public
  AddAction(JListView view, int burst) {
    this.t = new AddThread(view, burst);
    t.start();
  }
    
  public
  void actionPerformed(ActionEvent e) {
    System.out.println("Action occured");
    synchronized(t) {
      t.notify();
    }
  }

  class AddThread extends Thread {
    int burst;
    JListView view;

    Category cat = Category.getInstance("x");
    
    AddThread(JListView view, int burst) {
      super();
      this.burst = burst;
      this.view = view;
      setName("AddThread"+burst);
    }

    public
    void run() {

      while(true) {
	synchronized(this) {
	  try {
	    this.wait();
	  } catch(Exception e) {
	  }
	}
	for(int i = 0; i < burst; i++) {
	  LoggingEvent event = new LoggingEvent("x", cat, Priority.DEBUG, 
						"Message "+counter, null);

	  event.getThreadName();    
	  if(counter % 50 == 0) {
	    event.throwable = new Exception("hello "+counter);
	  }
	  counter++;
	  view.add(event);
	}
      }
    }
  }
}
