package com.adventofcode;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Intcode {

    public static String intcode(String stringCodes) {
        int[] codes = intcode(stringCodes, -1, -1);
        return IntStream.of(codes).boxed().map(Objects::toString).collect(Collectors.joining(","));
    }

    public static int[] intcode(String stringCodes, int noun, int verb) {
        int[] codes = Stream.of(stringCodes.split(",")).mapToInt(Integer::valueOf).toArray();
        if (noun > 0 && verb > 0) {
            codes[1] = noun;
            codes[2] = verb;
        }
        return internalIntcode(codes, () -> 0, n -> {
        });
    }

    public static int ioIntcode(String stringCodes, int input) {
        int[] codes = Stream.of(stringCodes.split(",")).mapToInt(Integer::valueOf).toArray();
        AtomicInteger output = new AtomicInteger();
        internalIntcode(codes, () -> input, output::set);
        return output.get();
    }

    public static int thrusterSignal(String code, List<Integer> settings) {
        try {
            return internalThrusterSignal(code, settings);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static int internalThrusterSignal(String stringCodes, List<Integer> settings) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(settings.size());
        BlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>(settings.subList(0, 1));
        BlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>(settings.subList(1, 2));
        BlockingQueue<Integer> queue3 = new LinkedBlockingQueue<>(settings.subList(2, 3));
        BlockingQueue<Integer> queue4 = new LinkedBlockingQueue<>(settings.subList(3, 4));
        BlockingQueue<Integer> queue5 = new LinkedBlockingQueue<>(settings.subList(4, 5));

        executorService.submit(() -> {
            internalIntcode(stringCodes, take(queue1), queue2::offer);
        });

        executorService.submit(() -> {
            internalIntcode(stringCodes, take(queue2), queue3::offer);
        });

        executorService.submit(() -> {
            internalIntcode(stringCodes, take(queue3), queue4::offer);
        });

        executorService.submit(() -> {
            internalIntcode(stringCodes, take(queue4), queue5::offer);
        });

        Future<Integer> future = executorService.submit(() -> {
            AtomicInteger result = new AtomicInteger(0);
            internalIntcode(stringCodes, take(queue5), (n) -> {
                queue1.offer(n);
                result.set(n);
            });
            return result.get();
        });

        queue1.offer(0);
        return future.get();
    }

    private static IntSupplier take(BlockingQueue<Integer> queue) {
        return () -> {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static int[] internalIntcode(String stringCodes, IntSupplier input, IntConsumer output) {
        int[] codes = Stream.of(stringCodes.split(",")).mapToInt(Integer::valueOf).toArray();
        return internalIntcode(codes, input, output);
    }

    private static int[] internalIntcode(int[] codes, IntSupplier input, IntConsumer output) {
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
                    int stackInput = input.getAsInt();
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
                    output.accept(value1);
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

    public static Pair<List<Integer>, Integer> maxThrusterSignal(String program, Integer... items) {
        Optional<Pair<List<Integer>, Integer>> max = Permutations.of(items)
                .map(settings -> Pair.of(settings, thrusterSignal(program, settings)))
                .max((o1, o2) -> Comparator.comparingInt((ToIntFunction<Pair<List<Integer>, Integer>>) Pair::getRight).compare(o1, o2));
        return max.orElseGet(() -> Pair.of(Collections.emptyList(), -1));
    }
}
