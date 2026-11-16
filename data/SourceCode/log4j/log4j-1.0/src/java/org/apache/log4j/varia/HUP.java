package org.apache.log4j.varia;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.helpers.LogLog;

class HUP extends Thread {

  int port;
  ExternallyRolledFileAppender er;

  HUP(ExternallyRolledFileAppender er, int port) {
    this.er = er;
    this.port = port;
  }
  
  public
  void run() {
    while(!isInterrupted()) {
      try {	
	ServerSocket serverSocket = new ServerSocket(port);
	while(true) {
	  Socket socket = serverSocket.accept();
	  LogLog.debug("Connected to client at " + socket.getInetAddress());
	  new Thread(new HUPNode(socket, er)).start();
	}
      }
      catch(Exception e) {
	e.printStackTrace();
      }
    } 
  }
}
