package com.adventofcode;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Day3 {
    /**
     * --- Day 3: Crossed Wires ---
     * The gravity assist was successful, and you're well on your way to the Venus refuelling station. During the rush
     * back on Earth, the fuel management system wasn't completely installed, so that's next on the priority list.
     * <p>
     * Opening the front panel reveals a jumble of wires. Specifically, two wires are connected to a central port and
     * extend outward on a grid. You trace the path each wire takes as it leaves the central port, one wire per line of
     * text (your puzzle input).
     * <p>
     * The wires twist and turn, but the two wires occasionally cross paths. To fix the circuit, you need to find the
     * intersection point closest to the central port. Because the wires are on a grid, use the Manhattan distance for
     * this measurement. While the wires do technically cross right at the central port where they both start, this point
     * does not count, nor does a wire count as crossing with itself.
     * <p>
     * For example, if the first wire's path is R8,U5,L5,D3, then starting from the central port (o), it goes right 8,
     * up 5, left 5, and finally down 3:
     * <p>
     * ...........
     * ...........
     * ...........
     * ....+----+.
     * ....|....|.
     * ....|....|.
     * ....|....|.
     * .........|.
     * .o-------+.
     * ...........
     * Then, if the second wire's path is U7,R6,D4,L4, it goes up 7, right 6, down 4, and left 4:
     * <p>
     * ...........
     * .+-----+...
     * .|.....|...
     * .|..+--X-+.
     * .|..|..|.|.
     * .|.-X--+.|.
     * .|..|....|.
     * .|.......|.
     * .o-------+.
     * ...........
     * These wires cross at two locations (marked X), but the lower-left one is closer to the central port: its distance is 3 + 3 = 6.
     * <p>
     * Here are a few more examples:
     * <p>
     * R75,D30,R83,U83,L12,D49,R71,U7,L72
     * U62,R66,U55,R34,D71,R55,D58,R83 = distance 159
     * R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
     * U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = distance 135
     * What is the Manhattan distance from the central port to the closest intersection?
     */
    public static int intersection(String path1, String path2) {
        List<Pair<Integer, Integer>> positions1 = readPath(path1);
        List<Pair<Integer, Integer>> positions2 = readPath(path2);

        Sets.SetView<Pair<Integer, Integer>> intersection = Sets.intersection(Sets.newHashSet(positions1.subList(1, positions1.size() - 1)), Sets.newHashSet(positions2.subList(1, positions2.size() - 1)));
        int distance = Integer.MAX_VALUE;
        for (Pair<Integer, Integer> position : intersection) {
            int d = Math.abs(position.getLeft()) + Math.abs(position.getRight());
            if (d < distance) {
                distance = d;
            }
        }
        return distance;
    }

    public static List<Pair<Integer, Integer>> readPath(String path) {
        String[] steps = path.split(",");
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        Pair<Integer, Integer> position = Pair.of(0, 0);
        result.add(position);
        for (String move : steps) {
            char direction = move.charAt(0);
            int distance = Integer.parseInt(move.substring(1));

            for (int i = 1; i < distance + 1; ++i) {
                switch (direction) {
                    case 'R':
                        result.add(Pair.of(position.getLeft() + i, position.getRight()));
                        break;
                    case 'U':
                        result.add(Pair.of(position.getLeft(), position.getRight() + i));
                        break;
                    case 'L':
                        result.add(Pair.of(position.getLeft() - i, position.getRight()));
                        break;
                    case 'D':
                        result.add(Pair.of(position.getLeft(), position.getRight() - i));
                        break;
                    default:
                        result.add(Pair.of(Integer.MIN_VALUE, Integer.MIN_VALUE));
                        break;
                }
            }
            position = Iterables.getLast(result);
        }
        return result;
    }

    /**
     * --- Part Two ---
     * It turns out that this circuit is very timing-sensitive; you actually need to minimize the signal delay.
     * <p>
     * To do this, calculate the number of steps each wire takes to reach each intersection; choose the intersection where
     * the sum of both wires' steps is lowest. If a wire visits a position on the grid multiple times, use the steps
     * value from the first time it visits that position when calculating the total value of a specific intersection.
     * <p>
     * The number of steps a wire takes is the total number of grid squares the wire has entered to get to that location,
     * including the intersection being considered. Again consider the example from above:
     * <p>
     * ...........
     * .+-----+...
     * .|.....|...
     * .|..+--X-+.
     * .|..|..|.|.
     * .|.-X--+.|.
     * .|..|....|.
     * .|.......|.
     * .o-------+.
     * ...........
     * In the above example, the intersection closest to the central port is reached after 8+5+5+2 = 20 steps by the first
     * wire and 7+6+4+3 = 20 steps by the second wire for a total of 20+20 = 40 steps.
     * <p>
     * However, the top-right intersection is better: the first wire takes only 8+5+2 = 15 and the second wire takes only
     * 7+6+2 = 15, a total of 15+15 = 30 steps.
     * <p>
     * Here are the best steps for the extra examples from above:
     * <p>
     * R75,D30,R83,U83,L12,D49,R71,U7,L72
     * U62,R66,U55,R34,D71,R55,D58,R83 = 610 steps
     * R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51
     * U98,R91,D20,R16,D67,R40,U7,R15,U6,R7 = 410 steps
     * What is the fewest combined steps the wires must take to reach an intersection?
     */
    public static int intersectionSteps(String path1, String path2) {
        List<Pair<Integer, Integer>> positions1 = readPath(path1);
        List<Pair<Integer, Integer>> positions2 = readPath(path2);

        Sets.SetView<Pair<Integer, Integer>> intersection = Sets.intersection(Sets.newHashSet(positions1.subList(1, positions1.size() - 1)), Sets.newHashSet(positions2.subList(1, positions2.size() - 1)));
        int steps = Integer.MAX_VALUE;
        for (Pair<Integer, Integer> position : intersection) {
            int steps1 = positions1.indexOf(position);
            int steps2 = positions2.indexOf(position);

            if (steps > steps1 + steps2) {
                steps = steps1 + steps2;
            }

        }
        return steps;
    }
}
