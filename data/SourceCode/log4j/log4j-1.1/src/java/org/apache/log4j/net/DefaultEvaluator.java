package org.apache.log4j.net;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.TriggeringEventEvaluator;
import java.util.Properties;
import java.util.Date;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeUtility;

class DefaultEvaluator implements TriggeringEventEvaluator {
  /**
     Is this <code>event</code> the e-mail triggering event?
     
     <p>This method returns <code>true</code>, if the event priority
     has ERROR priority or higher. Otherwisem it returns
     <code>false</code>. */
  public 
  boolean isTriggeringEvent(LoggingEvent event) {
    return event.priority.isGreaterOrEqual(Priority.ERROR); 
  }
}
