package com.adventofcode;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra<E> {
    private final Map<E, List<Pair<E, Integer>>> graph;

    public Dijkstra(Map<E, List<Pair<E, Integer>>> graph) {
        this.graph = graph;
    }

    public Map<E, Integer> computeDistance(E start) {
        Map<E, Integer> distance = new HashMap<>();
        graph.keySet().forEach(k -> distance.put(k, Integer.MAX_VALUE));

        // Map<E, E> precedent = new HashMap<>();
        // graph.keySet().forEach(k -> precedent.put(k, start));

        distance.put(start, 0);

        Set<E> nodes = new HashSet<>(graph.keySet());

        while (!nodes.isEmpty()) {
            E next = null;
            int minimum = Integer.MAX_VALUE;
            for (E node : nodes) {
                if (distance.getOrDefault(node, Integer.MAX_VALUE) < minimum) {
                    next = node;
                    minimum = distance.get(node);
                }
            }

            nodes.remove(next);

            for (Pair<E, Integer> edge : graph.get(next)) {
                if (distance.get(edge.getKey()) > distance.get(next) + edge.getValue()) {
                    distance.put(edge.getKey(), distance.get(next) + edge.getValue());
                    // precedent.put(edge.getKey(), next);
                }
            }
        }
        return distance;
    }
}
