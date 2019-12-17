package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day16Test {
    private static final short[] PATTERN = new short[]{0, 1, 0, -1};

    private static int pattern(int base, int n) {
        return PATTERN[((n + 1) / base) % 4];
    }

    private static List<Integer> pattern(int i) {
        return IntStream.range(0, 100).map(n1 -> pattern(i, n1)).boxed().collect(Collectors.toList());
    }

    private static String flawedFrequencyTransmission(String input, int steps) {
        int[] digits = input.chars().map(c -> c - '0').toArray();
        for (int step = 0; step < steps; step++) {
            for (int n = 0; n < digits.length; n++) {

                int sum = 0;
                for (int i = n; i < digits.length; i++) {
                    sum += digits[i] * PATTERN[((i + 1) / (n + 1)) % 4];
                }
                sum = Math.abs(sum) % 10;
                digits[n] = sum;
            }

            // System.out.println(Arrays.toString(digits));
        }
        return IntStream.of(digits).mapToObj(String::valueOf).collect(Collectors.joining(""));
    }

    private static String decode(String input) {
        int offset = Integer.parseInt(input.substring(0, 7));
        String repeat = input.repeat(10000);
        int[] digits = repeat.chars().map(c -> c - '0').toArray();
        int[] next = new int[digits.length];
        for (int step = 0; step < 100; step++) {
            int sum = Arrays.stream(digits, offset, digits.length).sum();

            for (int i = offset; i < digits.length; i++) {
                next[i] = sum % 10;
                sum -= digits[i];
            }

            System.arraycopy(next, offset, digits, offset, next.length - offset);
            // System.out.println(Arrays.toString(digits));
        }

        return Arrays.stream(digits, offset, offset + 8).mapToObj(String::valueOf).collect(Collectors.joining(""));
    }


    /**
     * --- Day 16: Flawed Frequency Transmission ---
     * You're 3/4ths of the way through the gas giants. Not only do roundtrip signals to Earth take five hours, but the
     * signal quality is quite bad as well. You can clean up the signal with the Flawed Frequency Transmission algorithm,
     * or FFT.
     * <p>
     * As input, FFT takes a list of numbers. In the signal you received (your puzzle input), each number is a single
     * digit: data like 15243 represents the sequence 1, 5, 2, 4, 3.
     * <p>
     * FFT operates in repeated phases. In each phase, a new list is constructed with the same length as the input list.
     * This new list is also used as the input for the next phase.
     * <p>
     * Each element in the new list is built by multiplying every value in the input list by a value in a repeating
     * pattern and then adding up the results. So, if the input list were 9, 8, 7, 6, 5 and the pattern for a given
     * element were 1, 2, 3, the result would be 9*1 + 8*2 + 7*3 + 6*1 + 5*2 (with each input element on the left and
     * each value in the repeating pattern on the right of each multiplication). Then, only the ones digit is kept: 38
     * becomes 8, -17 becomes 7, and so on.
     * <p>
     * While each element in the output array uses all of the same input array elements, the actual repeating pattern to
     * use depends on which output element is being calculated. The base pattern is 0, 1, 0, -1. Then, repeat each value
     * in the pattern a number of times equal to the position in the output list being considered. Repeat once for the
     * first element, twice for the second element, three times for the third element, and so on. So, if the third
     * element of the output list is being calculated, repeating the values would produce: 0, 0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1.
     * <p>
     * When applying the pattern, skip the very first value exactly once. (In other words, offset the whole pattern
     * left by one.) So, for the second element of the output list, the actual pattern used would be: 0, 1, 1, 0, 0, -1,
     * -1, 0, 0, 1, 1, 0, 0, -1, -1, ....
     * <p>
     * After using this process to calculate each element of the output list, the phase is complete, and the output list
     * of this phase is used as the new input list for the next phase, if any.
     * <p>
     * Given the input signal 12345678, below are four phases of FFT. Within each phase, each output digit is calculated
     * on a single line with the result at the far right; each multiplication operation shows the input digit on the left
     * and the pattern value on the right:
     * <p>
     * Input signal: 12345678
     * <p>
     * 1*1  + 2*0  + 3*-1 + 4*0  + 5*1  + 6*0  + 7*-1 + 8*0  = 4
     * 1*0  + 2*1  + 3*1  + 4*0  + 5*0  + 6*-1 + 7*-1 + 8*0  = 8
     * 1*0  + 2*0  + 3*1  + 4*1  + 5*1  + 6*0  + 7*0  + 8*0  = 2
     * 1*0  + 2*0  + 3*0  + 4*1  + 5*1  + 6*1  + 7*1  + 8*0  = 2
     * 1*0  + 2*0  + 3*0  + 4*0  + 5*1  + 6*1  + 7*1  + 8*1  = 6
     * 1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*1  + 7*1  + 8*1  = 1
     * 1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*0  + 7*1  + 8*1  = 5
     * 1*0  + 2*0  + 3*0  + 4*0  + 5*0  + 6*0  + 7*0  + 8*1  = 8
     * <p>
     * After 1 phase: 48226158
     * <p>
     * 4*1  + 8*0  + 2*-1 + 2*0  + 6*1  + 1*0  + 5*-1 + 8*0  = 3
     * 4*0  + 8*1  + 2*1  + 2*0  + 6*0  + 1*-1 + 5*-1 + 8*0  = 4
     * 4*0  + 8*0  + 2*1  + 2*1  + 6*1  + 1*0  + 5*0  + 8*0  = 0
     * 4*0  + 8*0  + 2*0  + 2*1  + 6*1  + 1*1  + 5*1  + 8*0  = 4
     * 4*0  + 8*0  + 2*0  + 2*0  + 6*1  + 1*1  + 5*1  + 8*1  = 0
     * 4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*1  + 5*1  + 8*1  = 4
     * 4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*0  + 5*1  + 8*1  = 3
     * 4*0  + 8*0  + 2*0  + 2*0  + 6*0  + 1*0  + 5*0  + 8*1  = 8
     * <p>
     * After 2 phases: 34040438
     * <p>
     * 3*1  + 4*0  + 0*-1 + 4*0  + 0*1  + 4*0  + 3*-1 + 8*0  = 0
     * 3*0  + 4*1  + 0*1  + 4*0  + 0*0  + 4*-1 + 3*-1 + 8*0  = 3
     * 3*0  + 4*0  + 0*1  + 4*1  + 0*1  + 4*0  + 3*0  + 8*0  = 4
     * 3*0  + 4*0  + 0*0  + 4*1  + 0*1  + 4*1  + 3*1  + 8*0  = 1
     * 3*0  + 4*0  + 0*0  + 4*0  + 0*1  + 4*1  + 3*1  + 8*1  = 5
     * 3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*1  + 3*1  + 8*1  = 5
     * 3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*0  + 3*1  + 8*1  = 1
     * 3*0  + 4*0  + 0*0  + 4*0  + 0*0  + 4*0  + 3*0  + 8*1  = 8
     * <p>
     * After 3 phases: 03415518
     * <p>
     * 0*1  + 3*0  + 4*-1 + 1*0  + 5*1  + 5*0  + 1*-1 + 8*0  = 0
     * 0*0  + 3*1  + 4*1  + 1*0  + 5*0  + 5*-1 + 1*-1 + 8*0  = 1
     * 0*0  + 3*0  + 4*1  + 1*1  + 5*1  + 5*0  + 1*0  + 8*0  = 0
     * 0*0  + 3*0  + 4*0  + 1*1  + 5*1  + 5*1  + 1*1  + 8*0  = 2
     * 0*0  + 3*0  + 4*0  + 1*0  + 5*1  + 5*1  + 1*1  + 8*1  = 9
     * 0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*1  + 1*1  + 8*1  = 4
     * 0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*0  + 1*1  + 8*1  = 9
     * 0*0  + 3*0  + 4*0  + 1*0  + 5*0  + 5*0  + 1*0  + 8*1  = 8
     * <p>
     * After 4 phases: 01029498
     * Here are the first eight digits of the final output list after 100 phases for some larger inputs:
     * <p>
     * 80871224585914546619083218645595 becomes 24176176.
     * 19617804207202209144916044189917 becomes 73745418.
     * 69317163492948606335995924319873 becomes 52432133.
     * After 100 phases of FFT, what are the first eight digits in the final output list?
     */
    @Test
    void testSimple() {
        assertThat(flawedFrequencyTransmission("12345678", 1)).isEqualTo("48226158");
        assertThat(flawedFrequencyTransmission("12345678", 2)).isEqualTo("34040438");
        assertThat(flawedFrequencyTransmission("12345678", 3)).isEqualTo("03415518");
        assertThat(flawedFrequencyTransmission("12345678", 4)).isEqualTo("01029498");
    }

    @Test
    void testLargerExample() {
        assertThat(flawedFrequencyTransmission("80871224585914546619083218645595", 100)).startsWith("24176176");
        assertThat(flawedFrequencyTransmission("19617804207202209144916044189917", 100)).startsWith("73745418");
        assertThat(flawedFrequencyTransmission("69317163492948606335995924319873", 100)).startsWith("52432133");
    }

    @Test
    void testInputPartOne() throws IOException {
        String line = FileUtils.readLine("/day/16/input");
        assertThat(flawedFrequencyTransmission(line, 100)).startsWith("32002835");
    }

    @Test
    void testPattern() {
        assertThat(pattern(1)).startsWith(1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1, 0, -1, 0, 1);
        assertThat(pattern(2)).startsWith(0, 1, 1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0);
        assertThat(pattern(3)).startsWith(0, 0, 1, 1, 1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 1, 1, 1, 0, 0, 0, -1);
        assertThat(pattern(4)).startsWith(0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 1, 1);
    }

    /**
     * --- Part Two ---
     * Now that your FFT is working, you can decode the real signal.
     * <p>
     * The real signal is your puzzle input repeated 10000 times. Treat this new signal as a single input list. Patterns
     * are still calculated as before, and 100 phases of FFT are still applied.
     * <p>
     * The first seven digits of your initial input signal also represent the message offset. The message offset is the
     * location of the eight-digit message in the final output list. Specifically, the message offset indicates the
     * number of digits to skip before reading the eight-digit message. For example, if the first seven digits of your initial input signal were 1234567, the eight-digit message would be the eight digits after skipping 1,234,567 digits of the final output list. Or, if the message offset were 7 and your final output list were 98765432109876543210, the eight-digit message would be 21098765. (Of course, your real message offset will be a seven-digit number, not a one-digit number like 7.)
     * <p>
     * Here is the eight-digit message in the final output list after 100 phases. The message offset given in each input
     * has been highlighted. (Note that the inputs given below are repeated 10000 times to find the actual starting
     * input lists.)
     * <p>
     * 03036732577212944063491565474664 becomes 84462026.
     * 02935109699940807407585447034323 becomes 78725270.
     * 03081770884921959731165446850517 becomes 53553731.
     * After repeating your input signal 10000 times and running 100 phases of FFT, what is the eight-digit message
     * embedded in the final output list?
     */
    @Test
    void testDecode() {
        assertThat(decode("03036732577212944063491565474664")).isEqualTo("84462026");
        assertThat(decode("02935109699940807407585447034323")).isEqualTo("78725270");
        assertThat(decode("03081770884921959731165446850517")).isEqualTo("53553731");
    }

    @Test
    void testInputPartTwo() throws IOException {
        String line = FileUtils.readLine("/day/16/input");
        assertThat(decode(line)).isEqualTo("69732268");
    }

}
