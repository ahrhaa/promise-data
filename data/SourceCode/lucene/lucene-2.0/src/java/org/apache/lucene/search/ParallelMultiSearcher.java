package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.PriorityQueue;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.PriorityQueue;

/** Implements parallel search over a set of <code>Searchables</code>.
 *
 * <p>Applications usually need only call the inherited {@link #search(Query)}
 * or {@link #search(Query,Filter)} methods.
 */
public class ParallelMultiSearcher extends MultiSearcher {

  private Searchable[] searchables;
  private int[] starts;
	
  /** Creates a searcher which searches <i>searchables</i>. */
  public ParallelMultiSearcher(Searchable[] searchables) throws IOException {
    super(searchables);
    this.searchables=searchables;
    this.starts=getStarts();
  }

  /**
   * TODO: parallelize this one too
   */
  public int docFreq(Term term) throws IOException {
    return super.docFreq(term);
  }

  /**
   * A search implementation which spans a new thread for each
   * Searchable, waits for each search to complete and merge
   * the results back together.
   */
  public TopDocs search(Weight weight, Filter filter, int nDocs)
    throws IOException {
    HitQueue hq = new HitQueue(nDocs);
    int totalHits = 0;
    MultiSearcherThread[] msta =
      new MultiSearcherThread[searchables.length];
    for (int i = 0; i < searchables.length; i++) { // search each searcher
      // Assume not too many searchables and cost of creating a thread is by far inferior to a search
      msta[i] =
        new MultiSearcherThread(
                                searchables[i],
                                weight,
                                filter,
                                nDocs,
                                hq,
                                i,
                                starts,
                                "MultiSearcher thread #" + (i + 1));
      msta[i].start();
    }

    for (int i = 0; i < searchables.length; i++) {
      try {
        msta[i].join();
      } catch (InterruptedException ie) {
        ; // TODO: what should we do with this???
      }
      IOException ioe = msta[i].getIOException();
      if (ioe == null) {
        totalHits += msta[i].hits();
      } else {
        // if one search produced an IOException, rethrow it
        throw ioe;
      }
    }

    ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
    for (int i = hq.size() - 1; i >= 0; i--) // put docs in array
      scoreDocs[i] = (ScoreDoc) hq.pop();

    float maxScore = (totalHits==0) ? Float.NEGATIVE_INFINITY : scoreDocs[0].score;
    
    return new TopDocs(totalHits, scoreDocs, maxScore);
  }

  /**
   * A search implementation allowing sorting which spans a new thread for each
   * Searchable, waits for each search to complete and merges
   * the results back together.
   */
  public TopFieldDocs search(Weight weight, Filter filter, int nDocs, Sort sort)
    throws IOException {
    // don't specify the fields - we'll wait to do this until we get results
    FieldDocSortedHitQueue hq = new FieldDocSortedHitQueue (null, nDocs);
    int totalHits = 0;
    MultiSearcherThread[] msta = new MultiSearcherThread[searchables.length];
    for (int i = 0; i < searchables.length; i++) { // search each searcher
      // Assume not too many searchables and cost of creating a thread is by far inferior to a search
      msta[i] =
        new MultiSearcherThread(
                                searchables[i],
                                weight,
                                filter,
                                nDocs,
                                hq,
                                sort,
                                i,
                                starts,
                                "MultiSearcher thread #" + (i + 1));
      msta[i].start();
    }

    float maxScore=Float.NEGATIVE_INFINITY;
    
    for (int i = 0; i < searchables.length; i++) {
      try {
        msta[i].join();
      } catch (InterruptedException ie) {
        ; // TODO: what should we do with this???
      }
      IOException ioe = msta[i].getIOException();
      if (ioe == null) {
        totalHits += msta[i].hits();
        maxScore=Math.max(maxScore, msta[i].getMaxScore());
      } else {
        // if one search produced an IOException, rethrow it
        throw ioe;
      }
    }

    ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
    for (int i = hq.size() - 1; i >= 0; i--) // put docs in array
      scoreDocs[i] = (ScoreDoc) hq.pop();

    return new TopFieldDocs(totalHits, scoreDocs, hq.getFields(), maxScore);
  }

  /** Lower-level search API.
   *
   * <p>{@link HitCollector#collect(int,float)} is called for every non-zero
   * scoring document.
   *
   * <p>Applications should only use this if they need <i>all</i> of the
   * matching documents.  The high-level search API ({@link
   * Searcher#search(Query)}) is usually more efficient, as it skips
   * non-high-scoring hits.
   *
   * @param weight to match documents
   * @param filter if non-null, a bitset used to eliminate some documents
   * @param results to receive hits
   * 
   * @todo parallelize this one too
   */
  public void search(Weight weight, Filter filter, final HitCollector results)
    throws IOException {
    for (int i = 0; i < searchables.length; i++) {

      final int start = starts[i];

      searchables[i].search(weight, filter, new HitCollector() {
          public void collect(int doc, float score) {
            results.collect(doc + start, score);
          }
        });

    }
  }

  /*
   * TODO: this one could be parallelized too
   * @see org.apache.lucene.search.Searchable#rewrite(org.apache.lucene.search.Query)
   */
  public Query rewrite(Query original) throws IOException {
    return super.rewrite(original);
  }

}
