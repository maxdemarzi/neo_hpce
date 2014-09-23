package com.hpce;

import org.neo4j.graphdb.Node;

import java.util.Comparator;
import java.util.Map;

public class ReverseComparator implements Comparator<Map.Entry<Node, int[]>> {
    public int compare(Map.Entry<Node, int[]> a, Map.Entry<Node, int[]> b) {
        // Reverse Order
        return b.getValue()[0] - a.getValue()[0];
    }
}