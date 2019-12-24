package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day24Test {
    private static BitSet nextState(BitSet bugs, List<List<Integer>> adjacents) {
        BitSet next = new BitSet(adjacents.size());
        for (int i = 0; i < adjacents.size(); i++) {
            long adjacent = adjacents.get(i).stream().filter(bugs::get).count();
            if (bugs.get(i)) {
                next.set(i, adjacent == 1);
            } else {
                next.set(i, adjacent == 1 || adjacent == 2);
            }
        }

        return next;
    }

    private static String printLayout(Map<Integer, BitSet> depthBugs, boolean show) {
        int[] depths = depthBugs.keySet().stream().mapToInt(t -> t).sorted().toArray();
        StringBuilder sb = new StringBuilder();
        for (int depth : depths) {
            sb.append("Depth ").append(depth).append(':').append('\n');
            for (int i = 0; i < 5; i++) {
                if (i > 0) {
                    sb.append('\n');
                }
                for (int j = 0; j < 5; j++) {
                    if (i == 2 && j == 2) {
                        sb.append('?');
                    } else {
                        sb.append(depthBugs.get(depth).get(5 * i + j) ? '#' : '.');
                    }
                }
            }

            sb.append("\n\n");
        }

        if (show)
            System.out.println(sb);
        return sb.toString();
    }

    private static String printLayout(BitSet bugs, boolean show) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            for (int j = 0; j < 5; j++) {
                sb.append(bugs.get(5 * i + j) ? '#' : '.');
            }
        }

        if (show)
            System.out.println(sb);
        return sb.toString();
    }

    private static List<List<Integer>> buildAdjacent(int size) {
        List<List<Integer>> adjacent = new ArrayList<>();
        IntStream.range(0, size * size).forEach(ignore -> adjacent.add(new ArrayList<>()));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int n = size * i + j;
                if (i > 0) {
                    adjacent.get(n).add(n - size);
                }
                if (i < size - 1) {
                    adjacent.get(n).add(n + size);
                }
                if (j > 0) {
                    adjacent.get(n).add(n - 1);
                }
                if (j < size - 1) {
                    adjacent.get(n).add(n + 1);
                }
            }
        }
        return adjacent;
    }

    private static List<List<Pair<Integer, Integer>>> buildDepthAdjacent(int size) {
        List<List<Pair<Integer, Integer>>> adjacent = new ArrayList<>();
        int middle = (size / 2) * size + (size / 2);
        IntStream.range(0, size * size).forEach(ignore -> adjacent.add(new ArrayList<>()));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int n = size * i + j;
                if (n == middle) {
                    continue;
                }
                if (i > 0) {
                    if (n - size == middle) {
                        for (int k = 0; k < size; ++k) {
                            adjacent.get(n).add(Pair.of(size * (size - 1) + k, 1));
                        }
                    } else {
                        adjacent.get(n).add(Pair.of(n - size, 0));
                    }
                } else {
                    adjacent.get(n).add(Pair.of(middle - size, -1));
                }
                if (i < size - 1) {
                    if (n + size == middle) {
                        for (int k = 0; k < size; ++k) {
                            adjacent.get(n).add(Pair.of(k, 1));
                        }
                    } else {
                        adjacent.get(n).add(Pair.of(n + size, 0));
                    }
                } else {
                    adjacent.get(n).add(Pair.of(middle + size, -1));
                }
                if (j > 0) {
                    if (n - 1 == middle) {
                        for (int k = 0; k < size; ++k) {
                            adjacent.get(n).add(Pair.of(size * (k + 1) - 1, 1));
                        }
                    } else {
                        adjacent.get(n).add(Pair.of(n - 1, 0));
                    }
                } else {
                    adjacent.get(n).add(Pair.of(middle - 1, -1));
                }
                if (j < size - 1) {
                    if (n + 1 == middle) {
                        for (int k = 0; k < size; ++k) {
                            adjacent.get(n).add(Pair.of(size * k, 1));
                        }
                    } else {
                        adjacent.get(n).add(Pair.of(n + 1, 0));
                    }
                } else {
                    adjacent.get(n).add(Pair.of(middle + 1, -1));
                }
            }
        }
        return adjacent;
    }

    private static BitSet parseLayout(String initialState) {
        initialState = String.join("", initialState.split("\\n"));
        BitSet bugs = new BitSet(25);
        char[] charArray = initialState.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '#') {
                bugs.set(i);
            }
        }
        return bugs;
    }

    private static long biodiversityPoints(BitSet bugs) {
        return bugs.stream().map(i -> 1 << i).sum();
    }

    private static boolean getBugs(Map<Integer, BitSet> depthBugs, int depth, int position) {
        BitSet bugs = depthBugs.get(depth);
        if (bugs == null) {
            return false;
        }

        return bugs.get(position);
    }

    private static Map<Integer, BitSet> nextState(Map<Integer, BitSet> depthBugs, List<List<Pair<Integer, Integer>>> adjacents) {
        Set<Integer> depths = depthBugs.keySet();
        int minDepth = depths.stream().mapToInt(t -> t).min().orElse(0) - 1;
        int maxDepth = depths.stream().mapToInt(t -> t).max().orElse(0) + 1;

        Map<Integer, BitSet> nextDepthBugs = new HashMap<>();
        for (int depth = minDepth; depth < maxDepth + 1; depth++) {
            int finalDepth = depth;
            BitSet bugs = new BitSet(adjacents.size());
            for (int i = 0; i < adjacents.size(); i++) {
                long adjacent = adjacents.get(i).stream().filter(p -> getBugs(depthBugs, finalDepth + p.getRight(), p.getLeft())).count();
                if (getBugs(depthBugs, depth, i)) {
                    bugs.set(i, adjacent == 1);
                } else {
                    bugs.set(i, adjacent == 1 || adjacent == 2);
                }
            }
            if (bugs.cardinality() != 0)
                nextDepthBugs.put(depth, bugs);
        }

        return nextDepthBugs;
    }

    /**
     * --- Day 24: Planet of Discord ---
     * You land on Eris, your last stop before reaching Santa. As soon as you do, your sensors start picking up strange
     * life forms moving around: Eris is infested with bugs! With an over 24-hour roundtrip for messages between you and
     * Earth, you'll have to deal with this problem on your own.
     * <p>
     * Eris isn't a very large place; a scan of the entire area fits into a 5x5 grid (your puzzle input). The scan shows
     * bugs (#) and empty spaces (.).
     * <p>
     * Each minute, The bugs live and die based on the number of bugs in the four adjacent tiles:
     * <p>
     * A bug dies (becoming an empty space) unless there is exactly one bug adjacent to it.
     * An empty space becomes infested with a bug if exactly one or two bugs are adjacent to it.
     * Otherwise, a bug or empty space remains the same. (Tiles on the edges of the grid have fewer than four adjacent
     * tiles; the missing tiles count as empty space.) This process happens in every location simultaneously; that is,
     * within the same minute, the number of adjacent bugs is counted for every tile first, and then the tiles are
     * updated.
     * <p>
     * Here are the first few minutes of an example scenario:
     * <p>
     * Initial state:
     * ....#
     * #..#.
     * #..##
     * ..#..
     * #....
     * <p>
     * After 1 minute:
     * #..#.
     * ####.
     * ###.#
     * ##.##
     * .##..
     * <p>
     * After 2 minutes:
     * #####
     * ....#
     * ....#
     * ...#.
     * #.###
     * <p>
     * After 3 minutes:
     * #....
     * ####.
     * ...##
     * #.##.
     * .##.#
     * <p>
     * After 4 minutes:
     * ####.
     * ....#
     * ##..#
     * .....
     * ##...
     * To understand the nature of the bugs, watch for the first time a layout of bugs and empty spaces matches any
     * previous layout. In the example above, the first layout to appear twice is:
     * <p>
     * .....
     * .....
     * .....
     * #....
     * .#...
     * To calculate the biodiversity rating for this layout, consider each tile left-to-right in the top row, then
     * left-to-right in the second row, and so on. Each of these tiles is worth biodiversity points equal to increasing
     * powers of two: 1, 2, 4, 8, 16, 32, and so on. Add up the biodiversity points for tiles with bugs; in this example,
     * the 16th tile (32768 points) and 22nd tile (2097152 points) have bugs, a total biodiversity rating of 2129920.
     * <p>
     * What is the biodiversity rating for the first layout that appears twice?
     */
    @Test
    void testSimpleExample() {
        List<List<Integer>> adjacent = buildAdjacent(5);
        System.out.println(adjacent);

        String initialState = "....#\n" +
                "#..#.\n" +
                "#..##\n" +
                "..#..\n" +
                "#....";
        BitSet bugs = parseLayout(initialState);

        System.out.println();
        System.out.println("Initial state:");
        assertThat(printLayout(bugs, false)).isEqualTo("....#\n" +
                "#..#.\n" +
                "#..##\n" +
                "..#..\n" +
                "#....");
        bugs = nextState(bugs, adjacent);

        System.out.println();
        System.out.println("After 1 minute:");
        assertThat(printLayout(bugs, false)).isEqualTo("#..#.\n" +
                "####.\n" +
                "###.#\n" +
                "##.##\n" +
                ".##..");
        bugs = nextState(bugs, adjacent);

        System.out.println();
        System.out.println("After 2 minute:");
        assertThat(printLayout(bugs, false)).isEqualTo("#####\n" +
                "....#\n" +
                "....#\n" +
                "...#.\n" +
                "#.###");
        bugs = nextState(bugs, adjacent);

        System.out.println();
        System.out.println("After 3 minute:");
        assertThat(printLayout(bugs, false)).isEqualTo("#....\n" +
                "####.\n" +
                "...##\n" +
                "#.##.\n" +
                ".##.#");
        bugs = nextState(bugs, adjacent);

        System.out.println();
        System.out.println("After 4 minute:");
        assertThat(printLayout(bugs, false)).isEqualTo("####.\n" +
                "....#\n" +
                "##..#\n" +
                ".....\n" +
                "##...");
    }

    @Test
    void testFindRepeatingLayout() {
        List<List<Integer>> adjacent = buildAdjacent(5);
        String initialState = "....#\n" +
                "#..#.\n" +
                "#..##\n" +
                "..#..\n" +
                "#....";
        BitSet bugs = parseLayout(initialState);

        Set<Long> layouts = new HashSet<>();
        while (layouts.add(bugs.toLongArray()[0])) {
            bugs = nextState(bugs, adjacent);
        }

        assertThat(printLayout(bugs, false)).isEqualTo(".....\n" +
                ".....\n" +
                ".....\n" +
                "#....\n" +
                ".#...");
        assertThat(biodiversityPoints(bugs)).isEqualTo(2129920);
    }

    @Test
    void testInputPartOne() throws IOException {
        List<List<Integer>> adjacent = buildAdjacent(5);

        List<String> lines = FileUtils.readLines("/day/24/input");
        String layout = String.join("\n", lines);

        BitSet bugs = parseLayout(layout);

        Set<Long> layouts = new HashSet<>();
        while (layouts.add(bugs.toLongArray()[0])) {
            bugs = nextState(bugs, adjacent);
        }

        assertThat(printLayout(bugs, false)).isEqualTo("....#\n" +
                "....#\n" +
                "##..#\n" +
                "##..#\n" +
                ".#..#");

        assertThat(biodiversityPoints(bugs)).isEqualTo(19516944);
    }

    /**
     * --- Part Two ---
     * After careful analysis, one thing is certain: you have no idea where all these bugs are coming from.
     * <p>
     * Then, you remember: Eris is an old Plutonian settlement! Clearly, the bugs are coming from recursively-folded
     * space.
     * <p>
     * This 5x5 grid is only one level in an infinite number of recursion levels. The tile in the middle of the grid is
     * actually another 5x5 grid, the grid in your scan is contained as the middle tile of a larger 5x5 grid, and so on.
     * Two levels of grids look like this:
     * <p>
     * |     |         |     |
     * |     |         |     |
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * |     |         |     |
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     | | | | | |     |
     * |     |-+-+-+-+-|     |
     * |     | | | | | |     |
     * |     |-+-+-+-+-|     |
     * |     | | |?| | |     |
     * |     |-+-+-+-+-|     |
     * |     | | | | | |     |
     * |     |-+-+-+-+-|     |
     * |     | | | | | |     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * |     |         |     |
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * |     |         |     |
     * |     |         |     |
     * (To save space, some of the tiles are not drawn to scale.) Remember, this is only a small part of the infinitely
     * recursive grid; there is a 5x5 grid that contains this diagram, and a 5x5 grid that contains that one, and so on.
     * Also, the ? in the diagram contains another 5x5 grid, which itself contains another 5x5 grid, and so on.
     * <p>
     * The scan you took (your puzzle input) shows where the bugs are on a single level of this structure. The middle
     * tile of your scan is empty to accommodate the recursive grids within it. Initially, no other levels contain bugs.
     * <p>
     * Tiles still count as adjacent if they are directly up, down, left, or right of a given tile. Some tiles have
     * adjacent tiles at a recursion level above or below its own level. For example:
     * <p>
     * |     |         |     |
     * 1  |  2  |    3    |  4  |  5
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * 6  |  7  |    8    |  9  |  10
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     |A|B|C|D|E|     |
     * |     |-+-+-+-+-|     |
     * |     |F|G|H|I|J|     |
     * |     |-+-+-+-+-|     |
     * 11  | 12  |K|L|?|N|O|  14 |  15
     * |     |-+-+-+-+-|     |
     * |     |P|Q|R|S|T|     |
     * |     |-+-+-+-+-|     |
     * |     |U|V|W|X|Y|     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * 16  | 17  |    18   |  19 |  20
     * |     |         |     |
     * -----+-----+---------+-----+-----
     * |     |         |     |
     * 21  | 22  |    23   |  24 |  25
     * |     |         |     |
     * Tile 19 has four adjacent tiles: 14, 18, 20, and 24.
     * Tile G has four adjacent tiles: B, F, H, and L.
     * Tile D has four adjacent tiles: 8, C, E, and I.
     * Tile E has four adjacent tiles: 8, D, 14, and J.
     * Tile 14 has eight adjacent tiles: 9, E, J, O, T, Y, 15, and 19.
     * Tile N has eight adjacent tiles: I, O, S, and five tiles within the sub-grid marked ?.
     * The rules about bugs living and dying are the same as before.
     * <p>
     * For example, consider the same initial state as above:
     * <p>
     * ....#
     * #..#.
     * #.?##
     * ..#..
     * #....
     * The center tile is drawn as ? to indicate the next recursive grid. Call this level 0; the grid within this one is
     * level 1, and the grid that contains this one is level -1. Then, after ten minutes, the grid at each level would
     * look like this:
     * <p>
     * Depth -5:
     * ..#..
     * .#.#.
     * ..?.#
     * .#.#.
     * ..#..
     * <p>
     * Depth -4:
     * ...#.
     * ...##
     * ..?..
     * ...##
     * ...#.
     * <p>
     * Depth -3:
     * #.#..
     * .#...
     * ..?..
     * .#...
     * #.#..
     * <p>
     * Depth -2:
     * .#.##
     * ....#
     * ..?.#
     * ...##
     * .###.
     * <p>
     * Depth -1:
     * #..##
     * ...##
     * ..?..
     * ...#.
     * .####
     * <p>
     * Depth 0:
     * .#...
     * .#.##
     * .#?..
     * .....
     * .....
     * <p>
     * Depth 1:
     * .##..
     * #..##
     * ..?.#
     * ##.##
     * #####
     * <p>
     * Depth 2:
     * ###..
     * ##.#.
     * #.?..
     * .#.##
     * #.#..
     * <p>
     * Depth 3:
     * ..###
     * .....
     * #.?..
     * #....
     * #...#
     * <p>
     * Depth 4:
     * .###.
     * #..#.
     * #.?..
     * ##.#.
     * .....
     * <p>
     * Depth 5:
     * ####.
     * #..#.
     * #.?#.
     * ####.
     * .....
     * In this example, after 10 minutes, a total of 99 bugs are present.
     * <p>
     * Starting with your scan, how many bugs are present after 200 minutes?
     */
    @Test
    void testBuildDepthAdjacent() {
        List<List<Pair<Integer, Integer>>> adjacent = buildDepthAdjacent(5);
        assertThat(adjacent.get(18)).hasSize(4).contains(
                Pair.of(13, 0),
                Pair.of(23, 0),
                Pair.of(17, 0),
                Pair.of(19, 0)
        );
        assertThat(adjacent.get(13)).hasSize(8).contains(
                Pair.of(8, 0),
                Pair.of(18, 0),
                Pair.of(4, 1),
                Pair.of(9, 1),
                Pair.of(14, 1),
                Pair.of(19, 1),
                Pair.of(24, 1),
                Pair.of(14, 0)
        );
        assertThat(adjacent.get(3)).hasSize(4).contains(
                Pair.of(7, -1),
                Pair.of(8, 0),
                Pair.of(2, 0),
                Pair.of(4, 0)
        );

        for (int i = 0; i < adjacent.size(); i++) {
            List<Pair<Integer, Integer>> pairs = adjacent.get(i);
            System.out.println(i + 1 + " ==> " + pairs.stream().map(t -> Pair.of(t.getLeft() + 1, t.getRight())).map(Objects::toString).collect(Collectors.joining(", ")));

        }
    }

    @Test
    void testDepthExample() {
        List<List<Pair<Integer, Integer>>> adjacent = buildDepthAdjacent(5);
        String layout = "....#\n" +
                "#..#.\n" +
                "#.?##\n" +
                "..#..\n" +
                "#....";

        BitSet bugs = parseLayout(layout);
        Map<Integer, BitSet> depthBugs = new HashMap<>();
        depthBugs.put(0, bugs);

        for (int minutes = 0; minutes < 10; minutes++) {
            depthBugs = nextState(depthBugs, adjacent);
        }

        assertThat(printLayout(depthBugs, true)).isEqualTo("Depth -5:\n" +
                "..#..\n" +
                ".#.#.\n" +
                "..?.#\n" +
                ".#.#.\n" +
                "..#..\n" +
                "\n" +
                "Depth -4:\n" +
                "...#.\n" +
                "...##\n" +
                "..?..\n" +
                "...##\n" +
                "...#.\n" +
                "\n" +
                "Depth -3:\n" +
                "#.#..\n" +
                ".#...\n" +
                "..?..\n" +
                ".#...\n" +
                "#.#..\n" +
                "\n" +
                "Depth -2:\n" +
                ".#.##\n" +
                "....#\n" +
                "..?.#\n" +
                "...##\n" +
                ".###.\n" +
                "\n" +
                "Depth -1:\n" +
                "#..##\n" +
                "...##\n" +
                "..?..\n" +
                "...#.\n" +
                ".####\n" +
                "\n" +
                "Depth 0:\n" +
                ".#...\n" +
                ".#.##\n" +
                ".#?..\n" +
                ".....\n" +
                ".....\n" +
                "\n" +
                "Depth 1:\n" +
                ".##..\n" +
                "#..##\n" +
                "..?.#\n" +
                "##.##\n" +
                "#####\n" +
                "\n" +
                "Depth 2:\n" +
                "###..\n" +
                "##.#.\n" +
                "#.?..\n" +
                ".#.##\n" +
                "#.#..\n" +
                "\n" +
                "Depth 3:\n" +
                "..###\n" +
                ".....\n" +
                "#.?..\n" +
                "#....\n" +
                "#...#\n" +
                "\n" +
                "Depth 4:\n" +
                ".###.\n" +
                "#..#.\n" +
                "#.?..\n" +
                "##.#.\n" +
                ".....\n" +
                "\n" +
                "Depth 5:\n" +
                "####.\n" +
                "#..#.\n" +
                "#.?#.\n" +
                "####.\n" +
                ".....\n\n");
        long totalBugs = depthBugs.values().stream().mapToInt(BitSet::cardinality).sum();
        assertThat(totalBugs).isEqualTo(99);
    }

    @Test
    void testInputPartTwo() throws IOException {
        List<List<Pair<Integer, Integer>>> adjacent = buildDepthAdjacent(5);

        List<String> lines = FileUtils.readLines("/day/24/input");
        String layout = String.join("\n", lines);

        BitSet bugs = parseLayout(layout);
        Map<Integer, BitSet> depthBugs = new HashMap<>();
        depthBugs.put(0, bugs);

        for (int minutes = 0; minutes < 200; minutes++) {
            depthBugs = nextState(depthBugs, adjacent);
        }

        // printLayout(depthBugs, true);
        long totalBugs = depthBugs.values().stream().mapToInt(BitSet::cardinality).sum();
        assertThat(totalBugs).isEqualTo(2006);
    }

}
