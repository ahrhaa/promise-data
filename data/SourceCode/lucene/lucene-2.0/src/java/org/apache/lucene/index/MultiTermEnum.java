package org.apache.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.*;

class MultiTermEnum extends TermEnum {
  private SegmentMergeQueue queue;

  private Term term;
  private int docFreq;

  public MultiTermEnum(IndexReader[] readers, int[] starts, Term t)
    throws IOException {
    queue = new SegmentMergeQueue(readers.length);
    for (int i = 0; i < readers.length; i++) {
      IndexReader reader = readers[i];
      TermEnum termEnum;

      if (t != null) {
        termEnum = reader.terms(t);
      } else
        termEnum = reader.terms();

      SegmentMergeInfo smi = new SegmentMergeInfo(starts[i], termEnum, reader);
      if (t == null ? smi.next() : termEnum.term() != null)
        queue.put(smi);          // initialize queue
      else
        smi.close();
    }

    if (t != null && queue.size() > 0) {
      next();
    }
  }

  public boolean next() throws IOException {
    SegmentMergeInfo top = (SegmentMergeInfo)queue.top();
    if (top == null) {
      term = null;
      return false;
    }

    term = top.term;
    docFreq = 0;

    while (top != null && term.compareTo(top.term) == 0) {
      queue.pop();
      docFreq += top.termEnum.docFreq();    // increment freq
      if (top.next())
        queue.put(top);          // restore queue
      else
        top.close();          // done with a segment
      top = (SegmentMergeInfo)queue.top();
    }
    return true;
  }

  public Term term() {
    return term;
  }

  public int docFreq() {
    return docFreq;
  }

  public void close() throws IOException {
    queue.close();
  }
}
