package org.apache.log4j.spi;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.Vector;

class VectorWriter extends PrintWriter {
    
  private Vector v;
  
  VectorWriter() {
    super(new NullWriter());
    v = new Vector();
  }
  
  public
  void println(Object o) {      
    v.addElement(o.toString());
  }
  
  // JDK 1.1.x apprenly uses this form of println while in
  // printStackTrace()
  public
  void println(char[] s) {
    v.addElement(new String(s));
  }
  
  public  
  void println(String s) {
    v.addElement(s);
  }

  public
  String[] toStringArray() {
    int len = v.size();
    String[] sa = new String[len];
    for(int i = 0; i < len; i++) {
      sa[i] = (String) v.elementAt(i);
    }
    return sa;
  }

  public
  void clear() {
    v.setSize(0);
  }
}
