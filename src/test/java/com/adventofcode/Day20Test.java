package com.adventofcode;

import com.adventofcode.graph.AStar;
import com.adventofcode.map.Direction;
import com.adventofcode.map.Point2D;
import com.adventofcode.map.Point3D;
import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class Day20Test {
    /**
     * --- Day 20: Donut Maze ---
     * You notice a strange pattern on the surface of Pluto and land nearby to get a closer look. Upon closer inspection,
     * you realize you've come across one of the famous space-warping mazes of the long-lost Pluto civilization!
     * <p>
     * Because there isn't much space on Pluto, the civilization that used to live here thrived by inventing a method for
     * folding spacetime. Although the technology is no longer understood, mazes like this one provide a small glimpse
     * into the daily life of an ancient Pluto citizen.
     * <p>
     * This maze is shaped like a donut. Portals along the inner and outer edge of the donut can instantly teleport you
     * from one side to the other. For example:
     * <p>
     * A
     * A
     * #######.#########
     * #######.........#
     * #######.#######.#
     * #######.#######.#
     * #######.#######.#
     * #####  B    ###.#
     * BC...##  C    ###.#
     * ##.##       ###.#
     * ##...DE  F  ###.#
     * #####    G  ###.#
     * #########.#####.#
     * DE..#######...###.#
     * #.#########.###.#
     * FG..#########.....#
     * ###########.#####
     * Z
     * Z
     * This map of the maze shows solid walls (#) and open passages (.). Every maze on Pluto has a start (the open tile
     * next to AA) and an end (the open tile next to ZZ). Mazes on Pluto also have portals; this maze has three pairs of
     * portals: BC, DE, and FG. When on an open tile next to one of these labels, a single step can take you to the other
     * tile with the same label. (You can only walk on . tiles; labels and empty space are not traversable.)
     * <p>
     * One path through the maze doesn't require any portals. Starting at AA, you could go down 1, right 8, down 12,
     * left 4, and down 1 to reach ZZ, a total of 26 steps.
     * <p>
     * However, there is a shorter path: You could walk from AA to the inner BC portal (4 steps), warp to the outer BC
     * portal (1 step), walk to the inner DE (6 steps), warp to the outer DE (1 step), walk to the outer FG (4 steps),
     * warp to the inner FG (1 step), and finally walk to ZZ (6 steps). In total, this is only 23 steps.
     * <p>
     * Here is a larger example:
     * <p>
     * A
     * A
     * #################.#############
     * #.#...#...................#.#.#
     * #.#.#.###.###.###.#########.#.#
     * #.#.#.......#...#.....#.#.#...#
     * #.#########.###.#####.#.#.###.#
     * #.............#.#.....#.......#
     * ###.###########.###.#####.#.#.#
     * #.....#        A   C    #.#.#.#
     * #######        S   P    #####.#
     * #.#...#                 #......VT
     * #.#.#.#                 #.#####
     * #...#.#               YN....#.#
     * #.###.#                 #####.#
     * DI....#.#                 #.....#
     * #####.#                 #.###.#
     * ZZ......#               QG....#..AS
     * ###.###                 #######
     * JO..#.#.#                 #.....#
     * #.#.#.#                 ###.#.#
     * #...#..DI             BU....#..LF
     * #####.#                 #.#####
     * YN......#               VT..#....QG
     * #.###.#                 #.###.#
     * #.#...#                 #.....#
     * ###.###    J L     J    #.#.###
     * #.....#    O F     P    #.#...#
     * #.###.#####.#.#####.#####.###.#
     * #...#.#.#...#.....#.....#.#...#
     * #.#####.###.###.#.#.#########.#
     * #...#.#.....#...#.#.#.#.....#.#
     * #.###.#####.###.###.#.#.#######
     * #.#.........#...#.............#
     * #########.###.###.#############
     * B   J   C
     * U   P   P
     * Here, AA has no direct path to ZZ, but it does connect to AS and CP. By passing through AS, QG, BU, and JO, you
     * can reach ZZ in 58 steps.
     * <p>
     * In your maze, how many steps does it take to get from the open tile marked AA to the open tile marked ZZ?
     */
    @Test
    void testSimpleExample() {
        String input = "         A           \n" +
                "         A           \n" +
                "  #######.#########  \n" +
                "  #######.........#  \n" +
                "  #######.#######.#  \n" +
                "  #######.#######.#  \n" +
                "  #######.#######.#  \n" +
                "  #####  B    ###.#  \n" +
                "BC...##  C    ###.#  \n" +
                "  ##.##       ###.#  \n" +
                "  ##...DE  F  ###.#  \n" +
                "  #####    G  ###.#  \n" +
                "  #########.#####.#  \n" +
                "DE..#######...###.#  \n" +
                "  #.#########.###.#  \n" +
                "FG..#########.....#  \n" +
                "  ###########.#####  \n" +
                "             Z       \n" +
                "             Z       ";
        char[][] map = Arrays.stream(input.split("\\n")).map(String::toCharArray).toArray(char[][]::new);
        long steps = new DonutMaze(map).solveMaze();
        assertThat(steps).isEqualTo(23);
    }

    @Test
    void testLargerExample() {
        String input = "                   A               \n" +
                "                   A               \n" +
                "  #################.#############  \n" +
                "  #.#...#...................#.#.#  \n" +
                "  #.#.#.###.###.###.#########.#.#  \n" +
                "  #.#.#.......#...#.....#.#.#...#  \n" +
                "  #.#########.###.#####.#.#.###.#  \n" +
                "  #.............#.#.....#.......#  \n" +
                "  ###.###########.###.#####.#.#.#  \n" +
                "  #.....#        A   C    #.#.#.#  \n" +
                "  #######        S   P    #####.#  \n" +
                "  #.#...#                 #......VT\n" +
                "  #.#.#.#                 #.#####  \n" +
                "  #...#.#               YN....#.#  \n" +
                "  #.###.#                 #####.#  \n" +
                "DI....#.#                 #.....#  \n" +
                "  #####.#                 #.###.#  \n" +
                "ZZ......#               QG....#..AS\n" +
                "  ###.###                 #######  \n" +
                "JO..#.#.#                 #.....#  \n" +
                "  #.#.#.#                 ###.#.#  \n" +
                "  #...#..DI             BU....#..LF\n" +
                "  #####.#                 #.#####  \n" +
                "YN......#               VT..#....QG\n" +
                "  #.###.#                 #.###.#  \n" +
                "  #.#...#                 #.....#  \n" +
                "  ###.###    J L     J    #.#.###  \n" +
                "  #.....#    O F     P    #.#...#  \n" +
                "  #.###.#####.#.#####.#####.###.#  \n" +
                "  #...#.#.#...#.....#.....#.#...#  \n" +
                "  #.#####.###.###.#.#.#########.#  \n" +
                "  #...#.#.....#...#.#.#.#.....#.#  \n" +
                "  #.###.#####.###.###.#.#.#######  \n" +
                "  #.#.........#...#.............#  \n" +
                "  #########.###.###.#############  \n" +
                "           B   J   C               \n" +
                "           U   P   P               ";
        char[][] map = Arrays.stream(input.split("\\n")).map(String::toCharArray).toArray(char[][]::new);
        long steps = new DonutMaze(map).solveMaze();
        assertThat(steps).isEqualTo(58);
    }

    @Test
    void testInputPartOne() throws IOException {
        char[][] map = FileUtils.readLines("/day/20/input").stream().map(String::toCharArray).toArray(char[][]::new);
        long steps = new DonutMaze(map).solveMaze();
        assertThat(steps).isEqualTo(684);
    }

    /**
     * --- Part Two ---
     * Strangely, the exit isn't open when you reach it. Then, you remember: the ancient Plutonians were famous for building recursive spaces.
     * <p>
     * The marked connections in the maze aren't portals: they physically connect to a larger or smaller copy of the maze. Specifically, the labeled tiles around the inside edge actually connect to a smaller copy of the same maze, and the smaller copy's inner labeled tiles connect to yet a smaller copy, and so on.
     * <p>
     * When you enter the maze, you are at the outermost level; when at the outermost level, only the outer labels AA and ZZ function (as the start and end, respectively); all other outer labeled tiles are effectively walls. At any other level, AA and ZZ count as walls, but the other outer labeled tiles bring you one level outward.
     * <p>
     * Your goal is to find a path through the maze that brings you back to ZZ at the outermost level of the maze.
     * <p>
     * In the first example above, the shortest path is now the loop around the right side. If the starting level is 0, then taking the previously-shortest path would pass through BC (to level 1), DE (to level 2), and FG (back to level 1). Because this is not the outermost level, ZZ is a wall, and the only option is to go back around to BC, which would only send you even deeper into the recursive maze.
     * <p>
     * In the second example above, there is no path that brings you to ZZ at the outermost level.
     * <p>
     * Here is a more interesting example:
     * <p>
     * Z L X W       C
     * Z P Q B       K
     * ###########.#.#.#.#######.###############
     * #...#.......#.#.......#.#.......#.#.#...#
     * ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###
     * #.#...#.#.#...#.#.#...#...#...#.#.......#
     * #.###.#######.###.###.#.###.###.#.#######
     * #...#.......#.#...#...#.............#...#
     * #.#########.#######.#.#######.#######.###
     * #...#.#    F       R I       Z    #.#.#.#
     * #.###.#    D       E C       H    #.#.#.#
     * #.#...#                           #...#.#
     * #.###.#                           #.###.#
     * #.#....OA                       WB..#.#..ZH
     * #.###.#                           #.#.#.#
     * CJ......#                           #.....#
     * #######                           #######
     * #.#....CK                         #......IC
     * #.###.#                           #.###.#
     * #.....#                           #...#.#
     * ###.###                           #.#.#.#
     * XF....#.#                         RF..#.#.#
     * #####.#                           #######
     * #......CJ                       NM..#...#
     * ###.#.#                           #.###.#
     * RE....#.#                           #......RF
     * ###.###        X   X       L      #.#.#.#
     * #.....#        F   Q       P      #.#.#.#
     * ###.###########.###.#######.#########.###
     * #.....#...#.....#.......#...#.....#.#...#
     * #####.#.###.#######.#######.###.###.#.#.#
     * #.......#.......#.#.#.#.#...#...#...#.#.#
     * #####.###.#####.#.#.#.#.###.###.#.###.###
     * #.......#.....#.#...#...............#...#
     * #############.#.#.###.###################
     * A O F   N
     * A A D   M
     * One shortest path through the maze is the following:
     * <p>
     * Walk from AA to XF (16 steps)
     * Recurse into level 1 through XF (1 step)
     * Walk from XF to CK (10 steps)
     * Recurse into level 2 through CK (1 step)
     * Walk from CK to ZH (14 steps)
     * Recurse into level 3 through ZH (1 step)
     * Walk from ZH to WB (10 steps)
     * Recurse into level 4 through WB (1 step)
     * Walk from WB to IC (10 steps)
     * Recurse into level 5 through IC (1 step)
     * Walk from IC to RF (10 steps)
     * Recurse into level 6 through RF (1 step)
     * Walk from RF to NM (8 steps)
     * Recurse into level 7 through NM (1 step)
     * Walk from NM to LP (12 steps)
     * Recurse into level 8 through LP (1 step)
     * Walk from LP to FD (24 steps)
     * Recurse into level 9 through FD (1 step)
     * Walk from FD to XQ (8 steps)
     * Recurse into level 10 through XQ (1 step)
     * Walk from XQ to WB (4 steps)
     * Return to level 9 through WB (1 step)
     * Walk from WB to ZH (10 steps)
     * Return to level 8 through ZH (1 step)
     * Walk from ZH to CK (14 steps)
     * Return to level 7 through CK (1 step)
     * Walk from CK to XF (10 steps)
     * Return to level 6 through XF (1 step)
     * Walk from XF to OA (14 steps)
     * Return to level 5 through OA (1 step)
     * Walk from OA to CJ (8 steps)
     * Return to level 4 through CJ (1 step)
     * Walk from CJ to RE (8 steps)
     * Return to level 3 through RE (1 step)
     * Walk from RE to IC (4 steps)
     * Recurse into level 4 through IC (1 step)
     * Walk from IC to RF (10 steps)
     * Recurse into level 5 through RF (1 step)
     * Walk from RF to NM (8 steps)
     * Recurse into level 6 through NM (1 step)
     * Walk from NM to LP (12 steps)
     * Recurse into level 7 through LP (1 step)
     * Walk from LP to FD (24 steps)
     * Recurse into level 8 through FD (1 step)
     * Walk from FD to XQ (8 steps)
     * Recurse into level 9 through XQ (1 step)
     * Walk from XQ to WB (4 steps)
     * Return to level 8 through WB (1 step)
     * Walk from WB to ZH (10 steps)
     * Return to level 7 through ZH (1 step)
     * Walk from ZH to CK (14 steps)
     * Return to level 6 through CK (1 step)
     * Walk from CK to XF (10 steps)
     * Return to level 5 through XF (1 step)
     * Walk from XF to OA (14 steps)
     * Return to level 4 through OA (1 step)
     * Walk from OA to CJ (8 steps)
     * Return to level 3 through CJ (1 step)
     * Walk from CJ to RE (8 steps)
     * Return to level 2 through RE (1 step)
     * Walk from RE to XQ (14 steps)
     * Return to level 1 through XQ (1 step)
     * Walk from XQ to FD (8 steps)
     * Return to level 0 through FD (1 step)
     * Walk from FD to ZZ (18 steps)
     * This path takes a total of 396 steps to move from AA at the outermost layer to ZZ at the outermost layer.
     * <p>
     * In your maze, when accounting for recursion, how many steps does it take to get from the open tile marked AA to the open tile marked ZZ, both at the outermost layer?
     */
    void testRecursiveDonutMaze() {
        String input =
                "             Z L X W       C                 \n" +
                        "             Z P Q B       K                 \n" +
                        "  ###########.#.#.#.#######.###############  \n" +
                        "  #...#.......#.#.......#.#.......#.#.#...#  \n" +
                        "  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###  \n" +
                        "  #.#...#.#.#...#.#.#...#...#...#.#.......#  \n" +
                        "  #.###.#######.###.###.#.###.###.#.#######  \n" +
                        "  #...#.......#.#...#...#.............#...#  \n" +
                        "  #.#########.#######.#.#######.#######.###  \n" +
                        "  #...#.#    F       R I       Z    #.#.#.#  \n" +
                        "  #.###.#    D       E C       H    #.#.#.#  \n" +
                        "  #.#...#                           #...#.#  \n" +
                        "  #.###.#                           #.###.#  \n" +
                        "  #.#....OA                       WB..#.#..ZH\n" +
                        "  #.###.#                           #.#.#.#  \n" +
                        "CJ......#                           #.....#  \n" +
                        "  #######                           #######  \n" +
                        "  #.#....CK                         #......IC\n" +
                        "  #.###.#                           #.###.#  \n" +
                        "  #.....#                           #...#.#  \n" +
                        "  ###.###                           #.#.#.#  \n" +
                        "XF....#.#                         RF..#.#.#  \n" +
                        "  #####.#                           #######  \n" +
                        "  #......CJ                       NM..#...#  \n" +
                        "  ###.#.#                           #.###.#  \n" +
                        "RE....#.#                           #......RF\n" +
                        "  ###.###        X   X       L      #.#.#.#  \n" +
                        "  #.....#        F   Q       P      #.#.#.#  \n" +
                        "  ###.###########.###.#######.#########.###  \n" +
                        "  #.....#...#.....#.......#...#.....#.#...#  \n" +
                        "  #####.#.###.#######.#######.###.###.#.#.#  \n" +
                        "  #.......#.......#.#.#.#.#...#...#...#.#.#  \n" +
                        "  #####.###.#####.#.#.#.#.###.###.#.###.###  \n" +
                        "  #.......#.....#.#...#...............#...#  \n" +
                        "  #############.#.#.###.###################  \n" +
                        "               A O F   N                     \n" +
                        "               A A D   M                     ";
        char[][] map = Arrays.stream(input.split("\\n")).map(String::toCharArray).toArray(char[][]::new);
        DonutMaze donutMaze = new DonutMaze(map);
        long steps = donutMaze.solveRecursiveMaze();
        assertThat(steps).isEqualTo(396);
    }

    @Test
    void testInputPartTwo() throws IOException {
        char[][] map = FileUtils.readLines("/day/20/input").stream().map(String::toCharArray).toArray(char[][]::new);
        DonutMaze donutMaze = new DonutMaze(map);
        long steps = donutMaze.solveRecursiveMaze();
        assertThat(steps).isEqualTo(7758L);
    }

    public static class DonutMaze {
        private final Map<Point2D, List<Point2D>> graph = new HashMap<>();
        private final Map<String, List<Point2D>> wrap = new HashMap<>();

        private final Map<Point2D, Point2D> innerWraps = new HashMap<>();
        private final Map<Point2D, Point2D> outerWraps = new HashMap<>();

        public DonutMaze(char[][] map) {
            int lineLength = Arrays.stream(map).mapToInt(s -> s.length).max().orElse(0);
            Set<Point2D> innerDoors = new HashSet<>();
            Set<Point2D> outerDoors = new HashSet<>();
            for (int y = 2; y < map.length - 2; y++) {
                for (int x = 2; x < lineLength - 2; x++) {
                    if (map[y][x] == '.') {
                        Point2D d = new Point2D(x, y);
                        for (Direction direction : Direction.values()) {
                            Point2D move = d.move(direction);
                            char c = map[(int) move.getY()][(int) move.getX()];
                            if (c == '.') {
                                graph.computeIfAbsent(d, ignore -> new ArrayList<>()).add(move);
                            } else if (Character.isAlphabetic(c)) {
                                Point2D shift = move.move(direction);
                                char cc = map[(int) shift.getY()][(int) shift.getX()];
                                String name = getWrapName(direction, c, cc);
                                if (shift.getX() < 2 || shift.getX() > lineLength - 2 || shift.getY() < 2 || shift.getY() > lineLength - 2) {
                                    outerDoors.add(d);
                                    // System.out.println(d + ": " + name + " is outer");
                                } else {
                                    innerDoors.add(d);
                                    // System.out.println(d + ": " + name + " is inner");
                                }
                                wrap.computeIfAbsent(name, ignore -> new ArrayList<>()).add(d);
                            }
                        }
                    }
                }
            }


            for (List<Point2D> value : wrap.values()) {
                if (value.size() == 2) {
                    Point2D outer = value.stream().filter(outerDoors::contains).findFirst().orElse(null);
                    Point2D inner = value.stream().filter(innerDoors::contains).findFirst().orElse(null);

                    innerWraps.put(inner, outer);
                    outerWraps.put(outer, inner);
                }
            }
        }

        private static String getWrapName(Direction d, char c1, char c2) {
            switch (d) {
                case NORTH:
                case WEST:
                    return "" + c2 + c1;
                case SOUTH:
                case EAST:
                    return "" + c1 + c2;
            }

            return "NaN";
        }

        private List<Point3D> flatNeighbours(Point3D node) {
            List<Point3D> voisins = new ArrayList<>();
            graph.getOrDefault(node.project(), Collections.emptyList()).stream().map(p -> new Point3D(p, node.getZ())).forEach(voisins::add);
            Point2D innerWrap = innerWraps.get(node.project());
            if (innerWrap != null) {
                voisins.add(new Point3D(innerWrap, node.getZ()));
            }
            Point2D outerWrap = outerWraps.get(node.project());
            if (outerWrap != null) {
                voisins.add(new Point3D(outerWrap, node.getZ()));
            }
            return voisins;
        }

        private List<Point3D> recursiveNeighbours(Point3D node) {
            List<Point3D> voisins = new ArrayList<>();
            graph.getOrDefault(node.project(), Collections.emptyList()).stream().map(p -> new Point3D(p, node.getZ())).forEach(voisins::add);
            Point2D innerWrap = innerWraps.get(node.project());
            if (innerWrap != null) {
                voisins.add(new Point3D(innerWrap, node.getZ() + 1));
            }
            Point2D outerWrap = outerWraps.get(node.project());
            if (outerWrap != null && node.getZ() > 0) {
                voisins.add(new Point3D(outerWrap, node.getZ() - 1));
            }
            return voisins;
        }

        public long solveMaze() {
            Point3D start = new Point3D(wrap.get("AA").get(0), 0);
            Point3D end = new Point3D(wrap.get("ZZ").get(0), 0);

            return AStar.algorithm(this::flatNeighbours, Point3D::ManhattanDistance, start, end, false);
        }

        public long solveRecursiveMaze() {
            Point3D start = new Point3D(wrap.get("AA").get(0), 0);
            Point3D end = new Point3D(wrap.get("ZZ").get(0), 0);

            return AStar.algorithm(this::recursiveNeighbours, Point3D::ManhattanDistance, start, end, false);
        }
    }
}
