package org.apache.lucene.index;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Arrays;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.search.Similarity;

final class Posting {				  // info about a Term in a doc
  Term term;					  // the Term
  int freq;					  // its frequency in doc
  int[] positions;				  // positions it occurs at
  TermVectorOffsetInfo [] offsets;

  Posting(Term t, int position, TermVectorOffsetInfo offset) {
    term = t;
    freq = 1;
    positions = new int[1];
    positions[0] = position;
    if(offset != null){
    offsets = new TermVectorOffsetInfo[1];
    offsets[0] = offset;
    }
    else
      offsets = null;
  }
}
