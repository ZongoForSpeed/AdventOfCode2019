package com.adventofcode;

import com.adventofcode.map.Direction;
import com.adventofcode.map.Map2D;
import com.adventofcode.map.Point2D;
import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day11Test {
    /**
     * --- Day 11: Space Police ---
     * On the way to Jupiter, you're pulled over by the Space Police.
     * <p>
     * "Attention, unmarked spacecraft! You are in violation of Space Law! All spacecraft must have a clearly visible
     * registration identifier! You have 24 hours to comply or be sent to Space Jail!"
     * <p>
     * Not wanting to be sent to Space Jail, you radio back to the Elves on Earth for help. Although it takes almost
     * three hours for their reply signal to reach you, they send instructions for how to power up the emergency hull
     * painting robot and even provide a small Intcode program (your puzzle input) that will cause it to paint your ship
     * appropriately.
     * <p>
     * There's just one problem: you don't have an emergency hull painting robot.
     * <p>
     * You'll need to build a new emergency hull painting robot. The robot needs to be able to move around on the grid
     * of square panels on the side of your ship, detect the color of its current panel, and paint its current panel
     * black or white. (All of the panels are currently black.)
     * <p>
     * The Intcode program will serve as the brain of the robot. The program uses input instructions to access the
     * robot's camera: provide 0 if the robot is over a black panel or 1 if the robot is over a white panel. Then, the
     * program will output two values:
     * <p>
     * First, it will output a value indicating the color to paint the panel the robot is over: 0 means to paint the
     * panel black, and 1 means to paint the panel white.
     * Second, it will output a value indicating the direction the robot should turn: 0 means it should turn left 90
     * degrees, and 1 means it should turn right 90 degrees.
     * After the robot turns, it should always move forward exactly one panel. The robot starts facing up.
     * <p>
     * The robot will continue running for a while like this and halt when it is finished drawing. Do not restart the
     * Intcode computer inside the robot during this process.
     * <p>
     * For example, suppose the robot is about to start running. Drawing black panels as ., white panels as #, and the
     * robot pointing the direction it is facing (< ^ > v), the initial state and region near the robot looks like this:
     * <p>
     * .....
     * .....
     * ..^..
     * .....
     * .....
     * The panel under the robot (not visible here because a ^ is shown instead) is also black, and so any input
     * instructions at this point should be provided 0. Suppose the robot eventually outputs 1 (paint white) and then 0
     * (turn left). After taking these actions and moving forward one panel, the region now looks like this:
     * <p>
     * .....
     * .....
     * .<#..
     * .....
     * .....
     * Input instructions should still be provided 0. Next, the robot might output 0 (paint black) and then 0 (turn left):
     * <p>
     * .....
     * .....
     * ..#..
     * .v...
     * .....
     * After more outputs (1,0, 1,0):
     * <p>
     * .....
     * .....
     * ..^..
     * .##..
     * .....
     * The robot is now back where it started, but because it is now on a white panel, input instructions should be
     * provided 1. After several more outputs (0,1, 1,0, 1,0), the area looks like this:
     * <p>
     * .....
     * ..<#.
     * ...#.
     * .##..
     * .....
     * Before you deploy the robot, you should probably have an estimate of the area it will cover: specifically, you
     * need to know the number of panels it paints at least once, regardless of color. In the example above, the robot
     * painted 6 panels at least once. (It painted its starting panel twice, but that panel is still only counted once;
     * it also never painted the panel it ended on.)
     * <p>
     * Build a new emergency hull painting robot and run the Intcode program on it. How many panels does it paint at
     * least once?
     */
    @Test
    void testInputPartOne() throws IOException {
        String line = FileUtils.readLine("/day/11/input");
        HullPaintingRobot robot = new HullPaintingRobot();
        Intcode.intcode(line, robot::programInput, robot::programOutput);
        assertThat(robot.getHull()).hasSize(1732);
        robot.getHull().print(v -> v == 1 ? 'X' : ' ');
    }

    /**
     * --- Part Two ---
     * You're not sure what it's trying to paint, but it's definitely not a registration identifier. The Space Police
     * are getting impatient.
     * <p>
     * Checking your external ship cameras again, you notice a white panel marked "emergency hull painting robot
     * starting panel". The rest of the panels are still black, but it looks like the robot was expecting to start on a
     * white panel, not a black one.
     * <p>
     * Based on the Space Law Space Brochure that the Space Police attached to one of your windows, a valid registration
     * identifier is always eight capital letters. After starting the robot on a single white panel instead, what
     * registration identifier does it paint on your hull?
     */
    @Test
    void testInputPartTwo() throws IOException {
        String line = FileUtils.readLine("/day/11/input");
        HullPaintingRobot robot = new HullPaintingRobot();
        robot.getHull().put(robot.getPosition(), 1L);
        Intcode.intcode(line, robot::programInput, robot::programOutput);
        assertThat(robot.getHull()).hasSize(249);
        List<String> hull = robot.hull.print(v -> v == 1 ? 'X' : ' ');
        assertThat(hull).containsExactly(
                "  XX  XXX   XX  X    XXXX X  X X  X   XX   ",
                " X  X X  X X  X X    X    X  X X  X    X   ",
                " X  X XXX  X    X    XXX  X  X XXXX    X   ",
                " XXXX X  X X    X    X    X  X X  X    X   ",
                " X  X X  X X  X X    X    X  X X  X X  X   ",
                " X  X XXX   XX  XXXX X     XX  X  X  XX    "
        );
    }

    static class HullPaintingRobot {
        Map2D hull;
        Point2D position;
        Direction direction;
        long count;

        public HullPaintingRobot() {
            hull = new Map2D();
            position = new Point2D(0, 0);
            direction = Direction.NORTH;
            count = 0;
        }

        public long programInput() {
            return hull.getOrDefault(position, 0L);
        }

        public void programOutput(long output) {
            if (count % 2 == 0) {
                // System.out.println("Painting " + position + " in " + output);
                hull.put(position, output);
            } else {
                direction = output == 0 ? direction.left() : direction.right();
                move();
                // System.out.println("Moving robot in " + direction + " to " + position);
            }
            count++;
        }

        private void move() {
            position = position.move(direction);
        }

        public Map2D getHull() {
            return hull;
        }

        public Point2D getPosition() {
            return position;
        }

        public Direction getDirection() {
            return direction;
        }

        public long getCount() {
            return count;
        }

    }
}
