package org.apache.poi.hssf.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.hssf.record.formula.*;

class Node {
        private Ptg value=null;
        private Node[] children=new Node[0];
        private int numChild=0;
        public Node(Ptg val) {
            value = val; 
        }
        public void setChildren(Node[] child) {children = child;numChild=child.length;}
        public int getNumChildren() {return numChild;}
        public Node getChild(int number) {return children[number];}
        public Ptg getValue() {return value;}
    }
