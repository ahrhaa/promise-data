package org.apache.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.*;

class MultiTermDocs implements TermDocs {
  protected IndexReader[] readers;
  protected int[] starts;
  protected Term term;

  protected int base = 0;
  protected int pointer = 0;

  private TermDocs[] readerTermDocs;
  protected TermDocs current;              // == readerTermDocs[pointer]

  public MultiTermDocs(IndexReader[] r, int[] s) {
    readers = r;
    starts = s;

    readerTermDocs = new TermDocs[r.length];
  }

  public int doc() {
    return base + current.doc();
  }
  public int freq() {
    return current.freq();
  }

  public void seek(Term term) {
    this.term = term;
    this.base = 0;
    this.pointer = 0;
    this.current = null;
  }

  public void seek(TermEnum termEnum) throws IOException {
    seek(termEnum.term());
  }

  public boolean next() throws IOException {
    if (current != null && current.next()) {
      return true;
    } else if (pointer < readers.length) {
      base = starts[pointer];
      current = termDocs(pointer++);
      return next();
    } else
      return false;
  }

  /** Optimized implementation. */
  public int read(final int[] docs, final int[] freqs) throws IOException {
    while (true) {
      while (current == null) {
        if (pointer < readers.length) {      // try next segment
          base = starts[pointer];
          current = termDocs(pointer++);
        } else {
          return 0;
        }
      }
      int end = current.read(docs, freqs);
      if (end == 0) {          // none left in segment
        current = null;
      } else {            // got some
        final int b = base;        // adjust doc numbers
        for (int i = 0; i < end; i++)
         docs[i] += b;
        return end;
      }
    }
  }

  /** As yet unoptimized implementation. */
  public boolean skipTo(int target) throws IOException {
    do {
      if (!next())
        return false;
    } while (target > doc());
      return true;
  }

  private TermDocs termDocs(int i) throws IOException {
    if (term == null)
      return null;
    TermDocs result = readerTermDocs[i];
    if (result == null)
      result = readerTermDocs[i] = termDocs(readers[i]);
    result.seek(term);
    return result;
  }

  protected TermDocs termDocs(IndexReader reader)
    throws IOException {
    return reader.termDocs();
  }

  public void close() throws IOException {
    for (int i = 0; i < readerTermDocs.length; i++) {
      if (readerTermDocs[i] != null)
        readerTermDocs[i].close();
    }
  }
}
