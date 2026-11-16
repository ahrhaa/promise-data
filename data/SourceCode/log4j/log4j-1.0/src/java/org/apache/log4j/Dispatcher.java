package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.helpers.BoundedFIFO;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.helpers.AppenderAttachableImpl;
import org.apache.log4j.helpers.LogLog;
import java.util.Enumeration;

class Dispatcher extends Thread {

  BoundedFIFO bf;
  AppenderAttachableImpl aai;
  
  Dispatcher(BoundedFIFO bf, AppenderAttachableImpl aai) {
    this.bf = bf;
    this.aai = aai;
    // set the dispatcher priority to lowest possible value
    this.setPriority(Thread.MIN_PRIORITY);
    
    // set the dispatcher priority to MIN_PRIORITY plus or minus 2
    // depending on the direction of MIN to MAX_PRIORITY.
    //+ (Thread.MAX_PRIORITY > Thread.MIN_PRIORITY ? 1 : -1)*2);

  }


  /**
     The dispatching strategy is to wait until there are events in the
     buffer to process. After having processed an event, we release
     the monitor (variable bf) so that new events can be placed in the
     buffer, instead of keeping the monitor and processing remaning
     events in the buffer. 

    <p>Other approaches might yield better results.

  */
  public
  void run() {

    //Category cat = Category.getInstance(Dispatcher.class.getName());

    LoggingEvent event;
    
    while(true) {
      synchronized(bf) {
	if(bf.length() == 0) {
	  // exit loop if we are interrupted but only if the the
	  // buffer is empty.
	  if(interrupted()) { 
	    //cat.info("Exiting.");
	    return;
	  }
	  try {
	    //cat.debug("Waiting for new event to dispatch.");
	    bf.wait();
	  } catch(InterruptedException e) {
	    //cat.info("Dispatcher interrupted.");
	    break;
	  }
	}
	//cat.debug("About to get new event.");
	event = bf.get();
	if(bf.wasFull()) {
	  //cat.debug("Notifiying AsyncAppender about freed space.");
	  bf.notify();
	}
      } // synchronized
      
      if(aai != null)
	aai.appendLoopOnAppenders(event);
    } // while
  }
}
