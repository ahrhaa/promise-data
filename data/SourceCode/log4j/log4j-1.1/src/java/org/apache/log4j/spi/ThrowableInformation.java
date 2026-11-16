package org.apache.log4j.spi;

import java.io.Writer;
import java.io.PrintWriter;
import java.util.Vector;

/**
   


 */
public class ThrowableInformation implements java.io.Serializable {


  private transient Throwable throwable;
  private String[] rep;
  
  static private VectorWriter vw = new VectorWriter();
  
  public
  ThrowableInformation(Throwable throwable) {
    this.throwable = throwable;
  }

  public
  Throwable getThrowable() {
    return throwable;
  }
  
  public
  String[] getThrowableStrRep() {
    if(rep != null) {
      return (String[]) rep.clone();
    } else {
      throwable.printStackTrace(vw);
      rep = vw.toStringArray();
      vw.clear();
      return rep;
    }
  }

  
}
