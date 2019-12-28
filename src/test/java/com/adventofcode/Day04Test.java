package com.adventofcode;

import com.adventofcode.maths.Digits;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day04Test {
    /**
     * --- Day 4: Secure Container ---
     * You arrive at the Venus fuel depot only to discover it's protected by a password. The Elves had written the
     * password on a sticky note, but someone threw it out.
     * <p>
     * However, they do remember a few key facts about the password:
     * <p>
     * It is a six-digit number.
     * The value is within the range given in your puzzle input.
     * Two adjacent digits are the same (like 22 in 122345).
     * Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123 or
     * 135679).
     * Other than the range rule, the following are true:
     * <p>
     * 111111 meets these criteria (double 11, never decreases).
     * 223450 does not meet these criteria (decreasing pair of digits 50).
     * 123789 does not meet these criteria (no double).
     * How many different passwords within the range given in your puzzle input meet these criteria?
     * <p>
     * Your puzzle input is 153517-630395.
     */
    public static boolean match(int n) {
        List<Integer> digits = Digits.digits(n);
        boolean sorted = Ordering.natural().isOrdered(digits);
        if (!sorted) {
            return false;
        }

        for (int i = 1, digitsSize = digits.size(); i < digitsSize; i++) {
            if (digits.get(i).equals(digits.get(i - 1))) {
                return true;
            }
        }

        return false;
    }

    /**
     * --- Part Two ---
     * An Elf just remembered one more important detail: the two adjacent matching digits are not part of a larger group
     * of matching digits.
     * <p>
     * Given this additional criterion, but still ignoring the range rule, the following are now true:
     * <p>
     * 112233 meets these criteria because the digits never decrease and all repeated digits are exactly two digits long.
     * 123444 no longer meets the criteria (the repeated 44 is part of a larger group of 444).
     * 111122 meets the criteria (even though 1 is repeated more than twice, it still contains a double 22).
     * How many different passwords within the range given in your puzzle input meet all of the criteria?
     * <p>
     * Your puzzle input is still 153517-630395.
     */
    public static boolean match2(int n) {
        List<Integer> digits = Digits.digits(n);
        boolean sorted = Ordering.natural().isOrdered(digits);
        if (!sorted) {
            return false;
        }

        List<Pair<Integer, Integer>> counter = new ArrayList<>();
        Pair<Integer, Integer> current = null;
        for (int value : digits) {
            if (current == null) {
                current = Pair.of(value, 1);
            } else if (current.getLeft() == value) {
                current = Pair.of(value, current.getRight() + 1);
            } else {
                counter.add(current);
                current = Pair.of(value, 1);
            }
        }
        counter.add(current);

        return counter.stream().anyMatch(p -> p.getRight() == 2);
    }

    @Test
    void testInput() {
        long total = IntStream.range(153517, 630395).filter(Day04Test::match).count();
        assertThat(total).isEqualTo(1729);
    }

    @Test
    void test111111() {
        assertThat(match(111111)).isTrue();
    }

    @Test
    void test223450() {
        assertThat(match(223450)).isFalse();
    }

    @Test
    void test123789() {
        assertThat(match(123789)).isFalse();
    }

    @Test
    void testInput2() {
        long total = IntStream.range(153517, 630395).filter(Day04Test::match2).count();
        assertThat(total).isEqualTo(1172);
    }


    @Test
    void test112233() {
        assertThat(match2(112233)).isTrue();
    }

    @Test
    void test123444() {
        assertThat(match2(123444)).isFalse();
    }

    @Test
    void test111122() {
        assertThat(match2(111122)).isTrue();
    }
}
