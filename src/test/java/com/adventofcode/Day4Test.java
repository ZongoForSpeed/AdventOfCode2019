package com.adventofcode;

import org.testng.annotations.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day4Test {
    @Test
    void testInput() {;
        long total = IntStream.range(153517, 630395).filter(Day4::match).count();
        assertThat(total).isEqualTo(1729);
    }

    @Test
    void test111111() {
        assertThat(Day4.match(111111)).isTrue();
    }

    @Test
    void test223450() {
        assertThat(Day4.match(223450)).isFalse();
    }

    @Test
    void test123789() {
        assertThat(Day4.match(123789)).isFalse();
    }

    @Test
    void testInput2() {
        long total = IntStream.range(153517, 630395).filter(Day4::match2).count();
        assertThat(total).isEqualTo(1172);
    }


    @Test
    void test112233() {
        assertThat(Day4.match2(112233)).isTrue();
    }

    @Test
    void test123444() {
        assertThat(Day4.match2(123444)).isFalse();
    }

    @Test
    void test111122() {
        assertThat(Day4.match2(111122)).isTrue();
    }
}
