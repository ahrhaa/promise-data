package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.DefaultCategoryFactory;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.CategoryFactory;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.FileWatchdog;
import java.util.NoSuchElementException;
import java.util.Enumeration;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Hashtable;

class PropertyWatchdog extends FileWatchdog {

  PropertyWatchdog(String filename) {
    super(filename);
  }

  /**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure log4j. */
  public
  void doOnChange() {
    new PropertyConfigurator().doConfigure(filename, Category.defaultHierarchy);
  }
}
