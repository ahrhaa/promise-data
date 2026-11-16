package org.apache.lucene.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import org.apache.lucene.index.IndexFileNameFilter;

class FSIndexInput extends BufferedIndexInput {

  private class Descriptor extends RandomAccessFile {
    public long position;
    public Descriptor(File file, String mode) throws IOException {
      super(file, mode);
    }
  }

  private Descriptor file = null;
  boolean isClone;
  private long length;

  public FSIndexInput(File path) throws IOException {
    file = new Descriptor(path, "r");
    length = file.length();
  }

  /** IndexInput methods */
  protected void readInternal(byte[] b, int offset, int len)
       throws IOException {
    synchronized (file) {
      long position = getFilePointer();
      if (position != file.position) {
        file.seek(position);
        file.position = position;
      }
      int total = 0;
      do {
        int i = file.read(b, offset+total, len-total);
        if (i == -1)
          throw new IOException("read past EOF");
        file.position += i;
        total += i;
      } while (total < len);
    }
  }

  public void close() throws IOException {
    if (!isClone)
      file.close();
  }

  protected void seekInternal(long position) {
  }

  public long length() {
    return length;
  }

  protected void finalize() throws IOException {
    close();            // close the file
  }

  public Object clone() {
    FSIndexInput clone = (FSIndexInput)super.clone();
    clone.isClone = true;
    return clone;
  }

  /** Method used for testing. Returns true if the underlying
   *  file descriptor is valid.
   */
  boolean isFDValid() throws IOException {
    return file.getFD().valid();
  }
}
