package org.apache.lucene.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import java.io.IOException;
import java.util.*;

class MultiTermPositions extends MultiTermDocs implements TermPositions {
  public MultiTermPositions(IndexReader[] r, int[] s) {
    super(r,s);
  }

  protected TermDocs termDocs(IndexReader reader) throws IOException {
    return (TermDocs)reader.termPositions();
  }

  public int nextPosition() throws IOException {
    return ((TermPositions)current).nextPosition();
  }

}
