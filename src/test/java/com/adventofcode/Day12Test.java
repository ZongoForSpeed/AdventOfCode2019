package com.adventofcode;

import com.adventofcode.maths.Arithmetic;
import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day12Test {
    /**
     * --- Day 12: The N-Body Problem ---
     * The space near Jupiter is not a very safe place; you need to be careful of a big distracting red spot, extreme
     * radiation, and a whole lot of moons swirling around. You decide to start by tracking the four largest moons: Io,
     * Europa, Ganymede, and Callisto.
     * <p>
     * After a brief scan, you calculate the position of each moon (your puzzle input). You just need to simulate their
     * motion so you can avoid them.
     * <p>
     * Each moon has a 3-dimensional position (x, y, and z) and a 3-dimensional velocity. The position of each moon is
     * given in your scan; the x, y, and z velocity of each moon starts at 0.
     * <p>
     * Simulate the motion of the moons in time steps. Within each time step, first update the velocity of every moon by
     * applying gravity. Then, once all moons' velocities have been updated, update the position of every moon by
     * applying velocity. Time progresses by one step once all of the positions are updated.
     * <p>
     * To apply gravity, consider every pair of moons. On each axis (x, y, and z), the velocity of each moon changes by
     * exactly +1 or -1 to pull the moons together. For example, if Ganymede has an x position of 3, and Callisto has a
     * x position of 5, then Ganymede's x velocity changes by +1 (because 5 > 3) and Callisto's x velocity changes by -1
     * (because 3 < 5). However, if the positions on a given axis are the same, the velocity on that axis does not
     * change for that pair of moons.
     * <p>
     * Once all gravity has been applied, apply velocity: simply add the velocity of each moon to its own position. For
     * example, if Europa has a position of x=1, y=2, z=3 and a velocity of x=-2, y=0,z=3, then its new position would
     * be x=-1, y=2, z=6. This process does not modify the velocity of any moon.
     * <p>
     * For example, suppose your scan reveals the following positions:
     * <p>
     * <x=-1, y=0, z=2>
     * <x=2, y=-10, z=-7>
     * <x=4, y=-8, z=8>
     * <x=3, y=5, z=-1>
     * Simulating the motion of these moons would produce the following:
     * <p>
     * After 0 steps:
     * pos=<x=-1, y=  0, z= 2>, vel=<x= 0, y= 0, z= 0>
     * pos=<x= 2, y=-10, z=-7>, vel=<x= 0, y= 0, z= 0>
     * pos=<x= 4, y= -8, z= 8>, vel=<x= 0, y= 0, z= 0>
     * pos=<x= 3, y=  5, z=-1>, vel=<x= 0, y= 0, z= 0>
     * <p>
     * After 1 step:
     * pos=<x= 2, y=-1, z= 1>, vel=<x= 3, y=-1, z=-1>
     * pos=<x= 3, y=-7, z=-4>, vel=<x= 1, y= 3, z= 3>
     * pos=<x= 1, y=-7, z= 5>, vel=<x=-3, y= 1, z=-3>
     * pos=<x= 2, y= 2, z= 0>, vel=<x=-1, y=-3, z= 1>
     * <p>
     * After 2 steps:
     * pos=<x= 5, y=-3, z=-1>, vel=<x= 3, y=-2, z=-2>
     * pos=<x= 1, y=-2, z= 2>, vel=<x=-2, y= 5, z= 6>
     * pos=<x= 1, y=-4, z=-1>, vel=<x= 0, y= 3, z=-6>
     * pos=<x= 1, y=-4, z= 2>, vel=<x=-1, y=-6, z= 2>
     * <p>
     * After 3 steps:
     * pos=<x= 5, y=-6, z=-1>, vel=<x= 0, y=-3, z= 0>
     * pos=<x= 0, y= 0, z= 6>, vel=<x=-1, y= 2, z= 4>
     * pos=<x= 2, y= 1, z=-5>, vel=<x= 1, y= 5, z=-4>
     * pos=<x= 1, y=-8, z= 2>, vel=<x= 0, y=-4, z= 0>
     * <p>
     * After 4 steps:
     * pos=<x= 2, y=-8, z= 0>, vel=<x=-3, y=-2, z= 1>
     * pos=<x= 2, y= 1, z= 7>, vel=<x= 2, y= 1, z= 1>
     * pos=<x= 2, y= 3, z=-6>, vel=<x= 0, y= 2, z=-1>
     * pos=<x= 2, y=-9, z= 1>, vel=<x= 1, y=-1, z=-1>
     * <p>
     * After 5 steps:
     * pos=<x=-1, y=-9, z= 2>, vel=<x=-3, y=-1, z= 2>
     * pos=<x= 4, y= 1, z= 5>, vel=<x= 2, y= 0, z=-2>
     * pos=<x= 2, y= 2, z=-4>, vel=<x= 0, y=-1, z= 2>
     * pos=<x= 3, y=-7, z=-1>, vel=<x= 1, y= 2, z=-2>
     * <p>
     * After 6 steps:
     * pos=<x=-1, y=-7, z= 3>, vel=<x= 0, y= 2, z= 1>
     * pos=<x= 3, y= 0, z= 0>, vel=<x=-1, y=-1, z=-5>
     * pos=<x= 3, y=-2, z= 1>, vel=<x= 1, y=-4, z= 5>
     * pos=<x= 3, y=-4, z=-2>, vel=<x= 0, y= 3, z=-1>
     * <p>
     * After 7 steps:
     * pos=<x= 2, y=-2, z= 1>, vel=<x= 3, y= 5, z=-2>
     * pos=<x= 1, y=-4, z=-4>, vel=<x=-2, y=-4, z=-4>
     * pos=<x= 3, y=-7, z= 5>, vel=<x= 0, y=-5, z= 4>
     * pos=<x= 2, y= 0, z= 0>, vel=<x=-1, y= 4, z= 2>
     * <p>
     * After 8 steps:
     * pos=<x= 5, y= 2, z=-2>, vel=<x= 3, y= 4, z=-3>
     * pos=<x= 2, y=-7, z=-5>, vel=<x= 1, y=-3, z=-1>
     * pos=<x= 0, y=-9, z= 6>, vel=<x=-3, y=-2, z= 1>
     * pos=<x= 1, y= 1, z= 3>, vel=<x=-1, y= 1, z= 3>
     * <p>
     * After 9 steps:
     * pos=<x= 5, y= 3, z=-4>, vel=<x= 0, y= 1, z=-2>
     * pos=<x= 2, y=-9, z=-3>, vel=<x= 0, y=-2, z= 2>
     * pos=<x= 0, y=-8, z= 4>, vel=<x= 0, y= 1, z=-2>
     * pos=<x= 1, y= 1, z= 5>, vel=<x= 0, y= 0, z= 2>
     * <p>
     * After 10 steps:
     * pos=<x= 2, y= 1, z=-3>, vel=<x=-3, y=-2, z= 1>
     * pos=<x= 1, y=-8, z= 0>, vel=<x=-1, y= 1, z= 3>
     * pos=<x= 3, y=-6, z= 1>, vel=<x= 3, y= 2, z=-3>
     * pos=<x= 2, y= 0, z= 4>, vel=<x= 1, y=-1, z=-1>
     * Then, it might help to calculate the total energy in the system. The total energy for a single moon is its potential energy multiplied by its kinetic energy. A moon's potential energy is the sum of the absolute values of its x, y, and z position coordinates. A moon's kinetic energy is the sum of the absolute values of its velocity coordinates. Below, each line shows the calculations for a moon's potential energy (pot), kinetic energy (kin), and total energy:
     * <p>
     * Energy after 10 steps:
     * pot: 2 + 1 + 3 =  6;   kin: 3 + 2 + 1 = 6;   total:  6 * 6 = 36
     * pot: 1 + 8 + 0 =  9;   kin: 1 + 1 + 3 = 5;   total:  9 * 5 = 45
     * pot: 3 + 6 + 1 = 10;   kin: 3 + 2 + 3 = 8;   total: 10 * 8 = 80
     * pot: 2 + 0 + 4 =  6;   kin: 1 + 1 + 1 = 3;   total:  6 * 3 = 18
     * Sum of total energy: 36 + 45 + 80 + 18 = 179
     * In the above example, adding together the total energy for all moons after 10 steps produces the total energy in the system, 179.
     * <p>
     * Here's a second example:
     * <p>
     * <x=-8, y=-10, z=0>
     * <x=5, y=5, z=10>
     * <x=2, y=-7, z=3>
     * <x=9, y=-8, z=-3>
     * Every ten steps of simulation for 100 steps produces:
     * <p>
     * After 0 steps:
     * pos=<x= -8, y=-10, z=  0>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  5, y=  5, z= 10>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  2, y= -7, z=  3>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  9, y= -8, z= -3>, vel=<x=  0, y=  0, z=  0>
     * <p>
     * After 10 steps:
     * pos=<x= -9, y=-10, z=  1>, vel=<x= -2, y= -2, z= -1>
     * pos=<x=  4, y= 10, z=  9>, vel=<x= -3, y=  7, z= -2>
     * pos=<x=  8, y=-10, z= -3>, vel=<x=  5, y= -1, z= -2>
     * pos=<x=  5, y=-10, z=  3>, vel=<x=  0, y= -4, z=  5>
     * <p>
     * After 20 steps:
     * pos=<x=-10, y=  3, z= -4>, vel=<x= -5, y=  2, z=  0>
     * pos=<x=  5, y=-25, z=  6>, vel=<x=  1, y=  1, z= -4>
     * pos=<x= 13, y=  1, z=  1>, vel=<x=  5, y= -2, z=  2>
     * pos=<x=  0, y=  1, z=  7>, vel=<x= -1, y= -1, z=  2>
     * <p>
     * After 30 steps:
     * pos=<x= 15, y= -6, z= -9>, vel=<x= -5, y=  4, z=  0>
     * pos=<x= -4, y=-11, z=  3>, vel=<x= -3, y=-10, z=  0>
     * pos=<x=  0, y= -1, z= 11>, vel=<x=  7, y=  4, z=  3>
     * pos=<x= -3, y= -2, z=  5>, vel=<x=  1, y=  2, z= -3>
     * <p>
     * After 40 steps:
     * pos=<x= 14, y=-12, z= -4>, vel=<x= 11, y=  3, z=  0>
     * pos=<x= -1, y= 18, z=  8>, vel=<x= -5, y=  2, z=  3>
     * pos=<x= -5, y=-14, z=  8>, vel=<x=  1, y= -2, z=  0>
     * pos=<x=  0, y=-12, z= -2>, vel=<x= -7, y= -3, z= -3>
     * <p>
     * After 50 steps:
     * pos=<x=-23, y=  4, z=  1>, vel=<x= -7, y= -1, z=  2>
     * pos=<x= 20, y=-31, z= 13>, vel=<x=  5, y=  3, z=  4>
     * pos=<x= -4, y=  6, z=  1>, vel=<x= -1, y=  1, z= -3>
     * pos=<x= 15, y=  1, z= -5>, vel=<x=  3, y= -3, z= -3>
     * <p>
     * After 60 steps:
     * pos=<x= 36, y=-10, z=  6>, vel=<x=  5, y=  0, z=  3>
     * pos=<x=-18, y= 10, z=  9>, vel=<x= -3, y= -7, z=  5>
     * pos=<x=  8, y=-12, z= -3>, vel=<x= -2, y=  1, z= -7>
     * pos=<x=-18, y= -8, z= -2>, vel=<x=  0, y=  6, z= -1>
     * <p>
     * After 70 steps:
     * pos=<x=-33, y= -6, z=  5>, vel=<x= -5, y= -4, z=  7>
     * pos=<x= 13, y= -9, z=  2>, vel=<x= -2, y= 11, z=  3>
     * pos=<x= 11, y= -8, z=  2>, vel=<x=  8, y= -6, z= -7>
     * pos=<x= 17, y=  3, z=  1>, vel=<x= -1, y= -1, z= -3>
     * <p>
     * After 80 steps:
     * pos=<x= 30, y= -8, z=  3>, vel=<x=  3, y=  3, z=  0>
     * pos=<x= -2, y= -4, z=  0>, vel=<x=  4, y=-13, z=  2>
     * pos=<x=-18, y= -7, z= 15>, vel=<x= -8, y=  2, z= -2>
     * pos=<x= -2, y= -1, z= -8>, vel=<x=  1, y=  8, z=  0>
     * <p>
     * After 90 steps:
     * pos=<x=-25, y= -1, z=  4>, vel=<x=  1, y= -3, z=  4>
     * pos=<x=  2, y= -9, z=  0>, vel=<x= -3, y= 13, z= -1>
     * pos=<x= 32, y= -8, z= 14>, vel=<x=  5, y= -4, z=  6>
     * pos=<x= -1, y= -2, z= -8>, vel=<x= -3, y= -6, z= -9>
     * <p>
     * After 100 steps:
     * pos=<x=  8, y=-12, z= -9>, vel=<x= -7, y=  3, z=  0>
     * pos=<x= 13, y= 16, z= -3>, vel=<x=  3, y=-11, z= -5>
     * pos=<x=-29, y=-11, z= -1>, vel=<x= -3, y=  7, z=  4>
     * pos=<x= 16, y=-13, z= 23>, vel=<x=  7, y=  1, z=  1>
     * <p>
     * Energy after 100 steps:
     * pot:  8 + 12 +  9 = 29;   kin: 7 +  3 + 0 = 10;   total: 29 * 10 = 290
     * pot: 13 + 16 +  3 = 32;   kin: 3 + 11 + 5 = 19;   total: 32 * 19 = 608
     * pot: 29 + 11 +  1 = 41;   kin: 3 +  7 + 4 = 14;   total: 41 * 14 = 574
     * pot: 16 + 13 + 23 = 52;   kin: 7 +  1 + 1 =  9;   total: 52 *  9 = 468
     * Sum of total energy: 290 + 608 + 574 + 468 = 1940
     * What is the total energy in the system after simulating the moons given in your scan for 1000 steps?
     */
    @Test
    void testFirstExample() {
        List<Moon> moons = Stream.of(
                "<x=-1, y=0, z=2>",
                "<x=2, y=-10, z=-7>",
                "<x=4, y=-8, z=8>",
                "<x=3, y=5, z=-1>").map(Moon::parse).collect(Collectors.toList());

        moons.forEach(System.out::println);
        System.out.println();
        for (long i = 1; i < 11; i++) {
            Moon.step(moons);
            System.out.println("After " + i + " step(s):");
            moons.forEach(System.out::println);
            System.out.println();
        }

        long totalEnergy = moons.stream().mapToLong(Moon::energy).sum();
        assertThat(totalEnergy).isEqualTo(179);
    }

    @Test
    void testSecondExample() {
        List<Moon> moons = Stream.of(
                "<x=-8, y=-10, z=0>",
                "<x=5, y=5, z=10>",
                "<x=2, y=-7, z=3>",
                "<x=9, y=-8, z=-3>").map(Moon::parse).collect(Collectors.toList());

        moons.forEach(System.out::println);
        System.out.println();
        for (long i = 1; i < 101; i++) {
            Moon.step(moons);
            if (i % 10 == 0) {
                System.out.println("After " + i + " step(s):");
                moons.forEach(System.out::println);
                System.out.println();
            }
        }

        long totalEnergy = moons.stream().mapToLong(Moon::energy).sum();
        assertThat(totalEnergy).isEqualTo(1940);
    }

    @Test
    void testInputPartOne() throws IOException {
        List<Moon> moons = FileUtils.readLines("/day/12/input").stream().map(Moon::parse).collect(Collectors.toList());

        IntStream.range(0, 1000).mapToObj(i -> moons).forEach(Moon::step);
        long totalEnergy = moons.stream().mapToLong(Moon::energy).sum();
        assertThat(totalEnergy).isEqualTo(12053);
    }

    /**
     * --- Part Two ---
     * All this drifting around in space makes you wonder about the nature of the universe. Does history really repeat
     * itself? You're curious whether the moons will ever return to a previous state.
     * <p>
     * Determine the number of steps that must occur before all of the moons' positions and velocities exactly match a
     * previous point in time.
     * <p>
     * For example, the first example above takes 2772 steps before they exactly match a previous point in time; it
     * eventually returns to the initial state:
     * <p>
     * After 0 steps:
     * pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>
     * <p>
     * After 2770 steps:
     * pos=<x=  2, y= -1, z=  1>, vel=<x= -3, y=  2, z=  2>
     * pos=<x=  3, y= -7, z= -4>, vel=<x=  2, y= -5, z= -6>
     * pos=<x=  1, y= -7, z=  5>, vel=<x=  0, y= -3, z=  6>
     * pos=<x=  2, y=  2, z=  0>, vel=<x=  1, y=  6, z= -2>
     * <p>
     * After 2771 steps:
     * pos=<x= -1, y=  0, z=  2>, vel=<x= -3, y=  1, z=  1>
     * pos=<x=  2, y=-10, z= -7>, vel=<x= -1, y= -3, z= -3>
     * pos=<x=  4, y= -8, z=  8>, vel=<x=  3, y= -1, z=  3>
     * pos=<x=  3, y=  5, z= -1>, vel=<x=  1, y=  3, z= -1>
     * <p>
     * After 2772 steps:
     * pos=<x= -1, y=  0, z=  2>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  2, y=-10, z= -7>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  4, y= -8, z=  8>, vel=<x=  0, y=  0, z=  0>
     * pos=<x=  3, y=  5, z= -1>, vel=<x=  0, y=  0, z=  0>
     * Of course, the universe might last for a very long time before repeating. Here's a copy of the second example
     * from above:
     * <p>
     * <x=-8, y=-10, z=0>
     * <x=5, y=5, z=10>
     * <x=2, y=-7, z=3>
     * <x=9, y=-8, z=-3>
     * This set of initial positions takes 4686774924 steps before it repeats a previous state! Clearly, you might need
     * to find a more efficient way to simulate the universe.
     * <p>
     * How many steps does it take to reach the first state that exactly matches a previous state?
     */
    @Test
    void testDriftingFirstExample() {
        List<Moon> moons = Stream.of(
                "<x=-1, y=0, z=2>",
                "<x=2, y=-10, z=-7>",
                "<x=4, y=-8, z=8>",
                "<x=3, y=5, z=-1>").map(Moon::parse).collect(Collectors.toList());

        List<Moon> moonCopyList = moons.stream().map(Moon::new).collect(Collectors.toList());

        long step = 0;
        do {
            step++;
            Moon.step(moons);
        } while (!moons.equals(moonCopyList));

        assertThat(step).isEqualTo(2772);

        long loopX = Moon.findLoop(moonCopyList, Moon::getPositionX, Moon::getVelocityX);
        long loopY = Moon.findLoop(moonCopyList, Moon::getPositionY, Moon::getVelocityY);
        long loopZ = Moon.findLoop(moonCopyList, Moon::getPositionZ, Moon::getVelocityZ);

        assertThat(loopX).isEqualTo(18);
        assertThat(loopY).isEqualTo(28);
        assertThat(loopZ).isEqualTo(44);

        long result = Arithmetic.lcm(loopX, loopY, loopZ);
        assertThat(result).isEqualTo(2772);
    }

    @Test
    void testDriftingSecondExample() {
        List<Moon> moons = Stream.of(
                "<x=-8, y=-10, z=0>",
                "<x=5, y=5, z=10>",
                "<x=2, y=-7, z=3>",
                "<x=9, y=-8, z=-3>").map(Moon::parse).collect(Collectors.toList());


        long loopX = Moon.findLoop(moons, Moon::getPositionX, Moon::getVelocityX);
        long loopY = Moon.findLoop(moons, Moon::getPositionY, Moon::getVelocityY);
        long loopZ = Moon.findLoop(moons, Moon::getPositionZ, Moon::getVelocityZ);

        assertThat(loopX).isEqualTo(2028);
        assertThat(loopY).isEqualTo(5898);
        assertThat(loopZ).isEqualTo(4702);

        long result = Arithmetic.lcm(loopX, loopY, loopZ);
        assertThat(result).isEqualTo(4686774924L);
    }

    @Test
    void testInputPartTwo() throws IOException {
        List<Moon> moons = FileUtils.readLines("/day/12/input").stream().map(Moon::parse).collect(Collectors.toList());

        long loopX = Moon.findLoop(moons, Moon::getPositionX, Moon::getVelocityX);
        long loopY = Moon.findLoop(moons, Moon::getPositionY, Moon::getVelocityY);
        long loopZ = Moon.findLoop(moons, Moon::getPositionZ, Moon::getVelocityZ);

        assertThat(loopX).isEqualTo(186028L);
        assertThat(loopY).isEqualTo(286332L);
        assertThat(loopZ).isEqualTo(96236L);

        long result = Arithmetic.lcm(loopX, loopY, loopZ);
        assertThat(result).isEqualTo(320380285873116L);
    }

    static class Moon {
        private static final String FORMAT = "<x=%3d, y=%3d, z=%3d>";
        private long positionX;
        private long positionY;
        private long positionZ;
        private long velocityX = 0;
        private long velocityY = 0;
        private long velocityZ = 0;

        public Moon(long positionX, long positionY, long positionZ) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.positionZ = positionZ;
        }

        public Moon(Moon moon) {
            this.positionX = moon.positionX;
            this.positionY = moon.positionY;
            this.positionZ = moon.positionZ;
            this.velocityX = moon.velocityX;
            this.velocityY = moon.velocityY;
            this.velocityZ = moon.velocityZ;
        }

        public static Moon parse(String line) {
            int i = line.indexOf('<');
            int j = line.indexOf('>');
            String substring = line.substring(i + 1, j);
            String[] split = substring.split(", ");
            return new Moon(Integer.parseInt(split[0].split("=")[1]), Integer.parseInt(split[1].split("=")[1]), Integer.parseInt(split[2].split("=")[1]));
        }

        public static void step(List<Moon> moons) {
            for (int i = 0; i < moons.size(); i++) {
                for (int j = i + 1; j < moons.size(); j++) {
                    moons.get(i).applyGravity(moons.get(j));
                }
            }
            moons.forEach(Moon::move);
        }

        public static long findLoop(List<Moon> moons, Function<Moon, Long> positionFunction, Function<Moon, Long> velocityFunction) {
            List<Moon> moonCopyList = moons.stream().map(Moon::new).collect(Collectors.toList());

            long step = 0;
            do {
                step++;
                step(moonCopyList);
            } while (match(moons, moonCopyList, positionFunction) || match(moons, moonCopyList, velocityFunction));
            return step;
        }

        public static boolean match(List<Moon> moons1, List<Moon> moons2, Function<Moon, Long> function) {
            return !moons1.stream().map(function).collect(Collectors.toList()).equals(moons2.stream().map(function).collect(Collectors.toList()));
        }

        public void applyGravity(Moon moon) {
            int compareX = Long.compare(positionX, moon.positionX);
            int compareY = Long.compare(positionY, moon.positionY);
            int compareZ = Long.compare(positionZ, moon.positionZ);

            velocityX -= compareX;
            velocityY -= compareY;
            velocityZ -= compareZ;

            moon.velocityX += compareX;
            moon.velocityY += compareY;
            moon.velocityZ += compareZ;
        }


        public long getPositionX() {
            return positionX;
        }

        public long getPositionY() {
            return positionY;
        }

        public long getPositionZ() {
            return positionZ;
        }

        public long getVelocityX() {
            return velocityX;
        }

        public long getVelocityY() {
            return velocityY;
        }

        public long getVelocityZ() {
            return velocityZ;
        }

        public void move() {
            positionX += velocityX;
            positionY += velocityY;
            positionZ += velocityZ;
        }

        public long energy() {
            long potential = Math.abs(positionX) + Math.abs(positionY) + Math.abs(positionZ);
            long kinetic = Math.abs(velocityX) + Math.abs(velocityY) + Math.abs(velocityZ);
            return potential * kinetic;
        }

        @Override
        public String toString() {
            return "pos=" + String.format(FORMAT, positionX, positionY, positionZ)
                    + ", vel=" + String.format(FORMAT, velocityX, velocityY, velocityZ);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Moon moon = (Moon) o;
            return positionX == moon.positionX &&
                    positionY == moon.positionY &&
                    positionZ == moon.positionZ &&
                    velocityX == moon.velocityX &&
                    velocityY == moon.velocityY &&
                    velocityZ == moon.velocityZ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(positionX, positionY, positionZ, velocityX, velocityY, velocityZ);
        }
    }
}
