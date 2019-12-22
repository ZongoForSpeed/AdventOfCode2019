package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

public class Day21Test {
    private static long runSpringscript(String line, String command) {
        BlockingQueue<Long> queue = new LinkedBlockingQueue<>();
        command.chars().mapToLong(t -> t).forEach(queue::add);
        AtomicLong result = new AtomicLong();
        Intcode.intcode(line, Intcode.take(queue), c -> {
            if (c < 256) {
                System.out.print((char) c);
                // sb.append((char) c);
            } else {
                System.out.println(c);
                result.set(c);
            }
        });

        return result.get();
    }

    /**
     * --- Day 21: Springdroid Adventure ---
     * You lift off from Pluto and start flying in the direction of Santa.
     * <p>
     * While experimenting further with the tractor beam, you accidentally pull an asteroid directly into your ship! It
     * deals significant damage to your hull and causes your ship to begin tumbling violently.
     * <p>
     * You can send a droid out to investigate, but the tumbling is causing enough artificial gravity that one wrong
     * step could send the droid through a hole in the hull and flying out into space.
     * <p>
     * The clear choice for this mission is a droid that can jump over the holes in the hull - a springdroid.
     * <p>
     * You can use an Intcode program (your puzzle input) running on an ASCII-capable computer to program the
     * springdroid. However, springdroids don't run Intcode; instead, they run a simplified assembly language called
     * springscript.
     * <p>
     * While a springdroid is certainly capable of navigating the artificial gravity and giant holes, it has one
     * downside: it can only remember at most 15 springscript instructions.
     * <p>
     * The springdroid will move forward automatically, constantly thinking about whether to jump. The springscript
     * program defines the logic for this decision.
     * <p>
     * Springscript programs only use Boolean values, not numbers or strings. Two registers are available: T, the
     * temporary value register, and J, the jump register. If the jump register is true at the end of the springscript
     * program, the springdroid will try to jump. Both of these registers start with the value false.
     * <p>
     * Springdroids have a sensor that can detect whether there is ground at various distances in the direction it is
     * facing; these values are provided in read-only registers. Your springdroid can detect ground at four distances:
     * one tile away (A), two tiles away (B), three tiles away (C), and four tiles away (D). If there is ground at the
     * given distance, the register will be true; if there is a hole, the register will be false.
     * <p>
     * There are only three instructions available in springscript:
     * <p>
     * AND X Y sets Y to true if both X and Y are true; otherwise, it sets Y to false.
     * OR X Y sets Y to true if at least one of X or Y is true; otherwise, it sets Y to false.
     * NOT X Y sets Y to true if X is false; otherwise, it sets Y to false.
     * In all three instructions, the second argument (Y) needs to be a writable register (either T or J). The first
     * argument (X) can be any register (including A, B, C, or D).
     * <p>
     * For example, the one-instruction program NOT A J means
     *                                                     "if the tile immediately in front of me is not ground, jump".
     * <p>
     * Or, here is a program that jumps if a three-tile-wide hole (with ground on the other side of the hole) is detected:
     * <p>
     * NOT A J
     * NOT B T
     * AND T J
     * NOT C T
     * AND T J
     * AND D J
     * The Intcode program expects ASCII inputs and outputs. It will begin by displaying a prompt; then, input the
     * desired instructions one per line. End each line with a newline (ASCII code 10). When you have finished entering
     * your program, provide the command WALK followed by a newline to instruct the springdroid to begin surveying the
     * hull.
     * <p>
     * If the springdroid falls into space, an ASCII rendering of the last moments of its life will be produced. In
     * these, @ is the springdroid, # is hull, and . is empty space. For example, suppose you program the springdroid
     * like this:
     * <p>
     * NOT D J
     * WALK
     * This one-instruction program sets J to true if and only if there is no ground four tiles away. In other words,
     * it attempts to jump into any hole it finds:
     * <p>
     * .................
     * .................
     * @................
     * #####.###########
     * <p>
     * .................
     * .................
     * .@...............
     * #####.###########
     * <p>
     * .................
     * ..@..............
     * .................
     * #####.###########
     * <p>
     * ...@.............
     * .................
     * .................
     * #####.###########
     * <p>
     * .................
     * ....@............
     * .................
     * #####.###########
     * <p>
     * .................
     * .................
     * .....@...........
     * #####.###########
     * <p>
     * .................
     * .................
     * .................
     * #####@###########
     * However, if the springdroid successfully makes it across, it will use an output instruction to indicate the
     * amount of damage to the hull as a single giant integer outside the normal ASCII range.
     * <p>
     * Program the springdroid with logic that allows it to survey the hull without falling into space. What amount of
     * hull damage does it report?
     */
    @Test
    void testPartOne() throws IOException {
        String line = FileUtils.readLine("/day/21/input");
        String command = "NOT A T\n" +
                "NOT B J\n" +
                "OR T J\n" +
                "NOT C T\n" +
                "OR T J\n" +
                "AND D J\n" +
                "WALK\n";
        assertThat(runSpringscript(line, command)).isEqualTo(19361023);
    }

    /**
     * --- Part Two ---
     * There are many areas the springdroid can't reach. You flip through the manual and discover a way to increase its
     * sensor range.
     * <p>
     * Instead of ending your springcode program with WALK, use RUN. Doing this will enable extended sensor mode, capable
     * of sensing ground up to nine tiles away. This data is available in five new read-only registers:
     * <p>
     * Register E indicates whether there is ground five tiles away.
     * Register F indicates whether there is ground six tiles away.
     * Register G indicates whether there is ground seven tiles away.
     * Register H indicates whether there is ground eight tiles away.
     * Register I indicates whether there is ground nine tiles away.
     * All other functions remain the same.
     * <p>
     * Successfully survey the rest of the hull by ending your program with RUN. What amount of hull damage does the
     * springdroid now report?
     */
    @Test
    void testPartTwo() throws IOException {
        String line = FileUtils.readLine("/day/21/input");
        String command = "NOT F J\n"
                + "OR E J\n"
                + "OR H J\n"
                + "AND D J\n"
                + "NOT C T\n"
                + "AND T J\n"
                + "NOT D T\n"
                + "OR B T\n"
                + "OR E T\n"
                + "NOT T T\n"
                + "OR T J\n"
                + "NOT A T\n"
                + "OR T J\n"
                + "RUN\n";
        assertThat(runSpringscript(line, command)).isEqualTo(1141457530);
    }
}
