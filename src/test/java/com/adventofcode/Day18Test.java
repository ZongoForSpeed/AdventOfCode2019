package com.adventofcode;

import com.adventofcode.graph.Dijkstra;
import com.adventofcode.map.Direction;
import com.adventofcode.map.Point2D;
import com.adventofcode.utils.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class Day18Test {

    /**
     * --- Day 18: Many-Worlds Interpretation ---
     * As you approach Neptune, a planetary security system detects you and activates a giant tractor beam on Triton!
     * You have no choice but to land.
     * <p>
     * A scan of the local area reveals only one interesting feature: a massive underground vault. You generate a map of
     * the tunnels (your puzzle input). The tunnels are too narrow to move diagonally.
     * <p>
     * Only one entrance (marked @) is present among the open passages (marked .) and stone walls (#), but you also
     * detect an assortment of keys (shown as lowercase letters) and doors (shown as uppercase letters). Keys of a given
     * letter open the door of the same letter: a opens A, b opens B, and so on. You aren't sure which key you need to
     * disable the tractor beam, so you'll need to collect all of them.
     * <p>
     * For example, suppose you have the following map:
     * <p>
     * #########
     * #b.A.@.a#
     * #########
     * Starting from the entrance (@), you can only access a large door (A) and a key (a). Moving toward the door
     * doesn't help you, but you can move 2 steps to collect the key, unlocking A in the process:
     * <p>
     * #########
     * #b.....@#
     * #########
     * Then, you can move 6 steps to collect the only other key, b:
     * <p>
     * #########
     * #@......#
     * #########
     * So, collecting every key took a total of 8 steps.
     * <p>
     * Here is a larger example:
     * <p>
     * ########################
     * #f.D.E.e.C.b.A.@.a.B.c.#
     * ######################.#
     * #d.....................#
     * ########################
     * The only reasonable move is to take key a and unlock door A:
     * <p>
     * ########################
     * #f.D.E.e.C.b.....@.B.c.#
     * ######################.#
     * #d.....................#
     * ########################
     * Then, do the same with key b:
     * <p>
     * ########################
     * #f.D.E.e.C.@.........c.#
     * ######################.#
     * #d.....................#
     * ########################
     * ...and the same with key c:
     * <p>
     * ########################
     * #f.D.E.e.............@.#
     * ######################.#
     * #d.....................#
     * ########################
     * Now, you have a choice between keys d and e. While key e is closer, collecting it now would be slower in the long
     * run than collecting key d first, so that's the best choice:
     * <p>
     * ########################
     * #f...E.e...............#
     * ######################.#
     * #@.....................#
     * ########################
     * Finally, collect key e to unlock door E, then collect key f, taking a grand total of 86 steps.
     * <p>
     * Here are a few more examples:
     * <p>
     * ########################
     * #...............b.C.D.f#
     * #.######################
     * #.....@.a.B.c.d.A.e.F.g#
     * ########################
     * Shortest path is 132 steps: b, a, c, d, f, e, g
     * <p>
     * #################
     * #i.G..c...e..H.p#
     * ########.########
     * #j.A..b...f..D.o#
     * ########@########
     * #k.E..a...g..B.n#
     * ########.########
     * #l.F..d...h..C.m#
     * #################
     * Shortest paths are 136 steps;
     * one is: a, f, b, j, g, n, h, d, l, o, e, p, c, i, k, m
     * <p>
     * ########################
     * #@..............ac.GI.b#
     * ###d#e#f################
     * ###A#B#C################
     * ###g#h#i################
     * ########################
     * Shortest paths are 81 steps; one is: a, c, f, i, d, g, b, e, h
     * <p>
     * How many steps is the shortest path that collects all of the keys?
     */
    @Test
    void testSimple() {
        String input = "#########\n" +
                "#b.A.@.a#\n" +
                "#########";
        String[] lines = input.split("\\n");
        char[][] map = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);

        assertThat(keys.algorithm()).isEqualTo(8);
    }

    @Test
    void testLargerExample1() {
        String input = "########################\n" +
                "#f.D.E.e.C.b.A.@.a.B.c.#\n" +
                "######################.#\n" +
                "#d.....................#\n" +
                "########################";
        String[] lines = input.split("\\n");
        char[][] map = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);
        assertThat(keys.algorithm()).isEqualTo(86);
    }

    @Test
    void testLargerExample2() {
        String input = "########################\n" +
                "#...............b.C.D.f#\n" +
                "#.######################\n" +
                "#.....@.a.B.c.d.A.e.F.g#\n" +
                "########################";
        String[] lines = input.split("\\n");
        char[][] map = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);
        assertThat(keys.algorithm()).isEqualTo(132);
    }

    @Test
    void testLargerExample3() {
        String input = "#################\n" +
                "#i.G..c...e..H.p#\n" +
                "########.########\n" +
                "#j.A..b...f..D.o#\n" +
                "########@########\n" +
                "#k.E..a...g..B.n#\n" +
                "########.########\n" +
                "#l.F..d...h..C.m#\n" +
                "#################";
        String[] lines = input.split("\\n");
        char[][] map = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);
        assertThat(keys.algorithm()).isEqualTo(136);
    }

    @Test
    void testLargerExample4() {
        String input = "########################\n" +
                "#@..............ac.GI.b#\n" +
                "###d#e#f################\n" +
                "###A#B#C################\n" +
                "###g#h#i################\n" +
                "########################";
        String[] lines = input.split("\\n");
        char[][] map = Arrays.stream(lines).map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);
        assertThat(keys.algorithm()).isEqualTo(81);
    }

    void testInputPartOne() throws IOException {
        char[][] map = FileUtils.readLines("/day/18/input").stream().map(String::toCharArray).toArray(char[][]::new);
        CollectKeys keys = new CollectKeys(map);
        System.out.println(keys.algorithm());
    }

    static class CollectKeys {
        private final Map<Point2D, List<Pair<Point2D, Integer>>> graph = new HashMap<>();
        private final Map<Point2D, List<Pair<Point2D, Integer>>> graphDoors = new HashMap<>();
        private final Map<Character, Point2D> doors = new HashMap<>();
        private final Map<Character, Point2D> keys = new HashMap<>();
        private Point2D origin;

        public CollectKeys(char[][] map) {
            Set<Point2D> nodes = new HashSet<>();

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    char codePoint = map[i][j];
                    if (codePoint != '#') {
                        Point2D d = new Point2D(i, j);
                        nodes.add(d);
                        if (codePoint == '@') {
                            origin = d;
                        } else if (Character.isUpperCase(codePoint)) {
                            doors.put(codePoint, d);
                        } else if (Character.isLowerCase(codePoint)) {
                            keys.put(codePoint, d);
                        }
                    }
                }
            }

            for (Point2D node : nodes) {
                for (Direction direction : Direction.values()) {
                    Point2D move = node.move(direction);
                    if (nodes.contains(move)) {
                        graph.computeIfAbsent(node, (ignore) -> new ArrayList<>()).add(Pair.of(move, 1));
                    }
                }
            }

            for (Map.Entry<Character, Point2D> entry : doors.entrySet()) {
                List<Pair<Point2D, Integer>> remove = graph.remove(entry.getValue());
                if (remove != null) {
                    graphDoors.put(entry.getValue(), remove);
                }
            }
        }

        public int algorithm() {
            Map<Pair<Point2D, Long>, Integer> cache = new HashMap<>();
            long keysBitSet = 0;
            for (Character character : keys.keySet()) {
                int offset = character - 'a';
                keysBitSet += 1 << offset;
            }
            return algorithm(cache, origin, keysBitSet);
        }

        private int algorithm(Map<Pair<Point2D, Long>, Integer> cache,
                              Point2D position,
                              long keysToFind) {
            if (keysToFind == 0) {
                return 0;
            }

            Integer cachedValue = cache.get(Pair.of(position, keysToFind));
            if (cachedValue != null) {
                return cachedValue;
            }

            Dijkstra<Point2D> dijkstra = new Dijkstra<>(graph);
            Map<Point2D, Integer> distances = dijkstra.computeDistance(position);
            int result = Integer.MAX_VALUE;
            char key = 'a';
            for (int offset = 1; offset <= keysToFind; offset *= 2, key++) {
                if ((keysToFind & offset) != 0) {

                    Point2D keyPoint = keys.get(key);
                    int distance = distances.get(keyPoint);
                    if (distance == Integer.MAX_VALUE) {
                        continue;
                    }
                    Point2D doorPoint = doors.get(Character.toUpperCase(key));
                    if (doorPoint != null) {
                        graph.put(doorPoint, graphDoors.get(doorPoint));
                    }

                    distance += algorithm(cache, keyPoint, keysToFind - offset);
                    if (result > distance) {
                        result = distance;
                    }

                    if (doorPoint != null) {
                        graph.remove(doorPoint);
                    }
                }
            }

            cache.put(Pair.of(position, keysToFind), result);
            return result;
        }
    }
}
