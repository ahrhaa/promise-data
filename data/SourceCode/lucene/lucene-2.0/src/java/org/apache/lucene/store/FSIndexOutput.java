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

class FSIndexOutput extends BufferedIndexOutput {
  RandomAccessFile file = null;

  public FSIndexOutput(File path) throws IOException {
    file = new RandomAccessFile(path, "rw");
  }

  /** output methods: */
  public void flushBuffer(byte[] b, int size) throws IOException {
    file.write(b, 0, size);
  }
  public void close() throws IOException {
    super.close();
    file.close();
  }

  /** Random-access methods */
  public void seek(long pos) throws IOException {
    super.seek(pos);
    file.seek(pos);
  }
  public long length() throws IOException {
    return file.length();
  }

  protected void finalize() throws IOException {
    file.close();          // close the file
  }

}
