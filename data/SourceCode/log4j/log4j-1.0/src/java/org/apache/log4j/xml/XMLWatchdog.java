package org.apache.log4j.xml;

import java.util.*;
import java.net.URL;
import org.w3c.dom.*;
import java.lang.reflect.Method;
import org.apache.log4j.Category;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.FileWatchdog;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;

class XMLWatchdog extends FileWatchdog {

  XMLWatchdog(String filename) {
    super(filename);
  }

  /**
     Call {@link PropertyConfigurator#configure(String)} with the
     <code>filename</code> to reconfigure log4j. */
  public
  void doOnChange() {
    new DOMConfigurator().doConfigure(filename, Category.getDefaultHierarchy());
  }
}
