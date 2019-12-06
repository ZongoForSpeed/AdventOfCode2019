package com.adventofcode;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Intcode {

    public static String intcode(String input) {
        int[] codes = intcode(input, -1, -1);
        return IntStream.of(codes).boxed().map(Objects::toString).collect(Collectors.joining(","));
    }

    public static int[] intcode(String input, int noun, int verb) {
        return intcode(input, noun, verb, new int[2]);
    }

    public static int[] intcode(String input, int[] io) {
        return intcode(input, -1, -1, io);
    }

    public static int[] intcode(String input, int noun, int verb, int[] io) {
        String[] split = input.split(",");

        int[] codes = Stream.of(split).mapToInt(Integer::valueOf).toArray();
        if (noun > 0 && verb > 0) {
            codes[1] = noun;
            codes[2] = verb;
        }
        for (int position = 0; position < codes.length; ) {
            int[] mode = parseCode(codes[position]);

            switch (mode[0]) {
                case 1: {
                    int value1 = mode[1] == 0 ? codes[codes[position + 1]] : codes[position + 1];
                    int value2 = mode[2] == 0 ? codes[codes[position + 2]] : codes[position + 2];
                    if (mode[3] == 0) {
                        codes[codes[position + 3]] = value1 + value2;
                    } else {
                        codes[position + 3] = value1 + value2;
                    }

                    position += 4;
                    break;
                }
                case 2: {
                    int value1 = mode[1] == 0 ? codes[codes[position + 1]] : codes[position + 1];
                    int value2 = mode[2] == 0 ? codes[codes[position + 2]] : codes[position + 2];
                    if (mode[3] == 0) {
                        codes[codes[position + 3]] = value1 * value2;
                    } else {
                        codes[position + 3] = value1 * value2;
                    }

                    position += 4;
                    break;
                }
                case 3: {
                    int stackInput = io[0];
                    if (mode[1] == 0) {
                        codes[codes[position + 1]] = stackInput;
                    } else {
                        codes[position + 1] = stackInput;
                    }

                    position += 2;
                    break;
                }
                case 4: {
                    int value1 = mode[1] == 0 ? codes[codes[position + 1]] : codes[position + 1];
                    io[1] = value1;

                    position += 2;
                    break;
                }
                // Opcode 5 is jump-if-true: if the first parameter is non-zero, it sets the instruction pointer to the
                // value from the second parameter. Otherwise, it does nothing.
                case 5: {
                    int value1 = mode[1] == 1 ? codes[position + 1] : codes[codes[position + 1]];
                    int value2 = mode[2] == 1 ? codes[position + 2] : codes[codes[position + 2]];
                    if (value1 != 0) {
                        position = value2;
                    } else {
                        position += 3;
                    }
                    break;
                }
                // Opcode 6 is jump-if-false: if the first parameter is zero, it sets the instruction pointer to the
                // value from the second parameter. Otherwise, it does nothing.
                case 6: {
                    int value1 = mode[1] == 1 ? codes[position + 1] : codes[codes[position + 1]];
                    int value2 = mode[2] == 1 ? codes[position + 2] : codes[codes[position + 2]];
                    if (value1 == 0) {
                        position = value2;
                    } else {
                        position += 3;
                    }
                    break;
                }
                // Opcode 7 is less than: if the first parameter is less than the second parameter, it stores 1 in the
                // position given by the third parameter. Otherwise, it stores 0.
                case 7: {
                    int value1 = mode[1] == 1 ? codes[position + 1] : codes[codes[position + 1]];
                    int value2 = mode[2] == 1 ? codes[position + 2] : codes[codes[position + 2]];
                    if (mode[3] == 1) {
                        codes[position + 3] = value1 < value2 ? 1 : 0;
                    } else {
                        codes[codes[position + 3]] = value1 < value2 ? 1 : 0;
                    }

                    position += 4;
                    break;
                }
                // Opcode 8 is equals: if the first parameter is equal to the second parameter, it stores 1 in the
                // position given by the third parameter. Otherwise, it stores 0.
                case 8: {
                    int value1 = mode[1] == 1 ? codes[position + 1] : codes[codes[position + 1]];
                    int value2 = mode[2] == 1 ? codes[position + 2] : codes[codes[position + 2]];
                    if (mode[3] == 1) {
                        codes[position + 3] = value1 == value2 ? 1 : 0;
                    } else {
                        codes[codes[position + 3]] = value1 == value2 ? 1 : 0;
                    }

                    position += 4;
                    break;
                }
                case 99:
                    return codes;
                default:
                    Arrays.fill(codes, -1);
                    return codes;
            }
        }

        Arrays.fill(codes, -1);
        return codes;
    }

    private static int[] parseCode(int code) {
        int[] mode = new int[4];
        mode[0] = code % 100;
        mode[1] = (code / 100) % 10;
        mode[2] = (code / 1000) % 10;
        mode[3] = (code / 10000) % 10;
        return mode;
    }
}
