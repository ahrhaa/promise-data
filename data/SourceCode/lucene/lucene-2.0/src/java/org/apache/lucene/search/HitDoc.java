package org.apache.lucene.search;

import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;
import org.apache.lucene.document.Document;

final class HitDoc {
  float score;
  int id;
  Document doc = null;

  HitDoc next;  // in doubly-linked cache
  HitDoc prev;  // in doubly-linked cache

  HitDoc(float s, int i) {
    score = s;
    id = i;
  }
}
