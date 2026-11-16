package org.apache.log4j.spi;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.Vector;

class NullWriter extends Writer {    
  
  public 
  void close() {
  }

  public 
  void flush() {
  }

  public
  void write(char[] cbuf, int off, int len) {
  }
}
