package org.apache.log4j.test;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestFailure;
import junit.framework.Test;
import java.io.Serializable;

class UTObjectRenderer implements ObjectRenderer {
  
  String name;

  UTObjectRenderer(String name) {
    this.name = name;
  }

  public
  String doRender(Object o) {
    return name;
  }

  public
  String toString() {
    return("UTObjectRenderer: "+name);
  }
}
