package com.adventofcode.maths;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArithmeticTest {
    @Test
    void testInverseModulaire() {
        assertThat(Arithmetic.inverseModulaire(3, 11)).isEqualTo(4);
        assertThat(Arithmetic.inverseModulaire(97643, 456753)).isEqualTo(368123);
        assertThat(Arithmetic.inverseModulaire(107113, 3246999210L)).isEqualTo(180730717L);
    }

    @Test
    void testPuissanceModulaire() {
        assertThat(Arithmetic.powerMod(2, 10, 100)).isEqualTo(24);
        assertThat(Arithmetic.powerMod(97643, 276799, 456753)).isEqualTo(368123);
    }

    @Test
    void testPuissance() {
        assertThat(Arithmetic.power(2, 10)).isEqualTo(1024);
    }
}
