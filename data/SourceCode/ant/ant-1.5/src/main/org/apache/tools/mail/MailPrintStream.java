package org.apache.tools.mail;

import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

class MailPrintStream extends PrintStream {

  int lastChar;

  public MailPrintStream(OutputStream out) {
    super(out, true);  // deprecated, but email is byte-oriented
  }

  // Mac does \n\r, but that's tough to distinguish from Windows \r\n\r\n.
  // Don't tackle that problem right now.
  public void write(int b) {
    if (b == '\n' && lastChar != '\r') {
      rawWrite('\r');  // ensure always \r\n
      rawWrite(b);
    } else if (b == '.' && lastChar == '\n') {
      rawWrite('.');  // add extra dot
      rawWrite(b);
    } else {
      rawWrite(b);
    }
    lastChar = b;
  }

  public void write(byte[] buf, int off, int len) {
    for (int i = 0; i < len; i++) {
      write(buf[off + i]);
    }
  }

  void rawWrite(int b) {
    super.write(b);
  }

  void rawPrint(String s) {
    int len = s.length();
    for (int i = 0; i < len; i++) {
      rawWrite(s.charAt(i));
    }
  }
}
