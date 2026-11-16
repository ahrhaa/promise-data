package org.apache.xalan.xsltc.runtime;

import java.util.Enumeration;

/**
 * IMPORTANT NOTE:
 * This code was taken from Sun's Java1.1 JDK java.util.HashTable.java
 * All "synchronized" keywords and some methods we do not need have been 
 * all been removed.
 */

/**
 * Object that wraps entries in the hash-table
 */
class HashtableEntry {
    int hash;
    Object key;
    Object value;
    HashtableEntry next;

    protected Object clone() {
	HashtableEntry entry = new HashtableEntry();
	entry.hash = hash;
	entry.key = key;
	entry.value = value;
	entry.next = (next != null) ? (HashtableEntry)next.clone() : null;
	return entry;
    }
}
