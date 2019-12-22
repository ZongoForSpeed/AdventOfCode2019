package com.adventofcode.graph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AStar {
    /**
     * cf. https://fr.wikipedia.org/wiki/Algorithme_A*
     */
    public static <E> long algorithm(Function<E, List<E>> graph, BiFunction<E, E, Long> distance, E start, E end, boolean useHeuristic) {
        Set<E> closedList = new HashSet<>();
        Queue<Node<E>> queue = new PriorityQueue<>(useHeuristic ? Comparator.comparingLong(Node::getHeuristic) : Comparator.comparingLong(Node::getCost));
        queue.add(new Node<>(start, 0L, distance.apply(start, end)));
        while (!queue.isEmpty()) {
            Node<E> node = queue.poll();
            if (node.getVertex().equals(end)) {
                return node.getCost();
            }
            if (closedList.add(node.getVertex())) {
                List<E> moves = graph.apply(node.getVertex());
                for (E move : moves) {
                    if (!closedList.contains(move)) {
                        Node<E> suivant = new Node<>(move, node.getCost() + 1, node.getCost() + distance.apply(end, move) + 1);
                        queue.add(suivant);
                    }
                }
            }
        }

        return Long.MAX_VALUE;
    }

    private static class Node<E> {
        private final E vertex;
        private final long heuristic;
        private final long cost;

        private Node(E vertex, long cost, long heuristic) {
            this.vertex = vertex;
            this.cost = cost;
            this.heuristic = heuristic;
        }

        public E getVertex() {
            return vertex;
        }

        public long getHeuristic() {
            return heuristic;
        }

        public long getCost() {
            return cost;
        }
    }
}
