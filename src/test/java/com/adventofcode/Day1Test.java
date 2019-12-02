package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Day1Test {
    @Test
    void massOf12() {
        assertThat(Day1.fuelRequirements(12)).isEqualTo(2);
    }

    @Test
    void massOf14() {
        assertThat(Day1.fuelRequirements(14)).isEqualTo(2);
        assertThat(Day1.sumFuelRequirements(14)).isEqualTo(2);
    }

    @Test
    void massOf1969() {
        assertThat(Day1.fuelRequirements(1969)).isEqualTo(654);
        assertThat(Day1.sumFuelRequirements(1969)).isEqualTo(966);
    }

    @Test
    void massOf100756() {
        assertThat(Day1.fuelRequirements(100756)).isEqualTo(33583);
        assertThat(Day1.sumFuelRequirements(100756)).isEqualTo(50346);
    }

    @Test
    void massOfInput() throws IOException {
        List<String> lines = FileUtils.readLines("/day/1/input");
        assertThat(lines.stream().mapToLong(Long::valueOf).map(Day1::fuelRequirements).sum()).isEqualTo(3369286);
        assertThat(lines.stream().mapToLong(Long::valueOf).map(Day1::sumFuelRequirements).sum()).isEqualTo(5051054);
    }
}
