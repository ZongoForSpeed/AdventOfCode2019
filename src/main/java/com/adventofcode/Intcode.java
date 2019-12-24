package com.adventofcode;

import org.apache.commons.lang3.tuple.Pair;

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Intcode {

    public static String intcode(String stringCodes) {
        long[] codes = intcode(stringCodes, -1, -1);
        return LongStream.of(codes).boxed().map(Objects::toString).collect(Collectors.joining(","));
    }

    public static long[] intcode(String stringCodes, long noun, long verb) {
        List<Long> memory = Stream.of(stringCodes.split(",")).map(Long::valueOf).collect(Collectors.toList());
        if (noun > 0 && verb > 0) {
            memory.set(1, noun);
            memory.set(2, verb);
        }
        return internalIntcode(memory, () -> 0, n -> {
        });
    }

    public static long ioIntcode(String stringCodes, long input) {
        List<Long> memory = Stream.of(stringCodes.split(",")).map(Long::valueOf).collect(Collectors.toList());
        AtomicLong output = new AtomicLong();
        internalIntcode(memory, () -> input, output::set);
        return output.get();
    }

    public static long thrusterSignal(String code, List<Long> settings) {
        try {
            return internalThrusterSignal(code, settings);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static long internalThrusterSignal(String stringCodes, List<Long> settings) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(settings.size());
        BlockingQueue<Long> queue1 = new LinkedBlockingQueue<>(settings.subList(0, 1));
        BlockingQueue<Long> queue2 = new LinkedBlockingQueue<>(settings.subList(1, 2));
        BlockingQueue<Long> queue3 = new LinkedBlockingQueue<>(settings.subList(2, 3));
        BlockingQueue<Long> queue4 = new LinkedBlockingQueue<>(settings.subList(3, 4));
        BlockingQueue<Long> queue5 = new LinkedBlockingQueue<>(settings.subList(4, 5));

        executorService.submit(() -> {
            intcode(stringCodes, take(queue1), queue2::offer);
        });

        executorService.submit(() -> {
            intcode(stringCodes, take(queue2), queue3::offer);
        });

        executorService.submit(() -> {
            intcode(stringCodes, take(queue3), queue4::offer);
        });

        executorService.submit(() -> {
            intcode(stringCodes, take(queue4), queue5::offer);
        });

        Future<Long> future = executorService.submit(() -> {
            AtomicLong result = new AtomicLong(0);
            intcode(stringCodes, take(queue5), (n) -> {
                queue1.offer(n);
                result.set(n);
            });
            return result.get();
        });

        queue1.offer(0L);
        return future.get();
    }

    public static LongSupplier take(BlockingQueue<Long> queue) {
        return () -> {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        };
    }

    public static long[] intcode(String stringCodes, LongSupplier input, LongConsumer output) {
        List<Long> memory = Stream.of(stringCodes.split(",")).map(Long::valueOf).collect(Collectors.toList());
        return internalIntcode(memory, input, output);
    }

    private static long[] internalIntcode(List<Long> memory, LongSupplier input, LongConsumer output) {
        int relativeBase = 0;
        for (int position = 0; position < memory.size(); ) {
            int[] mode = parseCode(memory.get(position));

            switch (mode[0]) {
                case 1: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    setValue(memory, mode, position, relativeBase, 3, value1 + value2);
                    position += 4;
                    break;
                }
                case 2: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    setValue(memory, mode, position, relativeBase, 3, value1 * value2);
                    position += 4;
                    break;
                }
                case 3: {
                    long stackInput = input.getAsLong();
                    setValue(memory, mode, position, relativeBase, 1, stackInput);
                    position += 2;
                    break;
                }
                case 4: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    output.accept(value1);
                    position += 2;
                    break;
                }
                // Opcode 5 is jump-if-true: if the first parameter is non-zero, it sets the instruction pointer to the
                // value from the second parameter. Otherwise, it does nothing.
                case 5: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    if (value1 != 0) {
                        position = (int) value2;
                    } else {
                        position += 3;
                    }
                    break;
                }
                // Opcode 6 is jump-if-false: if the first parameter is zero, it sets the instruction pointer to the
                // value from the second parameter. Otherwise, it does nothing.
                case 6: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    if (value1 == 0) {
                        position = (int) value2;
                    } else {
                        position += 3;
                    }
                    break;
                }
                // Opcode 7 is less than: if the first parameter is less than the second parameter, it stores 1 in the
                // position given by the third parameter. Otherwise, it stores 0.
                case 7: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    setValue(memory, mode, position, relativeBase, 3, value1 < value2 ? 1 : 0);
                    position += 4;
                    break;
                }
                // Opcode 8 is equals: if the first parameter is equal to the second parameter, it stores 1 in the
                // position given by the third parameter. Otherwise, it stores 0.
                case 8: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    long value2 = readParameter(memory, mode, relativeBase, position, 2);
                    setValue(memory, mode, position, relativeBase, 3, value1 == value2 ? 1 : 0);
                    position += 4;
                    break;
                }
                // Opcode 9 adjusts the relative base by the value of its only parameter. The relative base increases
                // (or decreases, if the value is negative) by the value of the parameter.
                case 9: {
                    long value1 = readParameter(memory, mode, relativeBase, position, 1);
                    relativeBase += value1;
                    position += 2;
                    break;
                }
                case 99:
                    return memory.stream().mapToLong(Long::longValue).toArray();
                default:
                    throw new IllegalStateException("unknown code (" + mode[0] + ")");
            }
        }

        // Arrays.fill(memory, -1);
        return memory.stream().mapToLong(Long::longValue).toArray();
    }

    private static long readMemory(List<Long> memory, int position) {
        if (position < memory.size()) {
            return memory.get(position);
        } else {
            return 0L;
        }
    }

    private static void setMemory(List<Long> memory, int position, long value) {
        if (position >= memory.size()) {
            memory.addAll(Collections.nCopies(position - memory.size() + 1, 0L));
        }
        memory.set(position, value);
    }

    private static void setValue(List<Long> memory, int[] mode, int position, int relativeBase, int offset, long value) {
        switch (mode[offset]) {
            case 0:
                setMemory(memory, (int) readMemory(memory, position + offset), value);
                break;
            case 1:
                setMemory(memory, position + offset, value);
                break;
            case 2:
                setMemory(memory, relativeBase + (int) readMemory(memory, position + offset), value);
                break;
            default:
                throw new IllegalStateException("setValue(" + mode[offset] + ")");
        }
    }

    private static long readParameter(List<Long> memory, int[] mode, int relativeBase, int position, int offset) {
        switch (mode[offset]) {
            case 0:
                return readMemory(memory, (int) readMemory(memory, position + offset));
            case 1:
                return readMemory(memory, position + offset);
            case 2:
                return readMemory(memory, relativeBase + (int) readMemory(memory, position + offset));
            default:
                throw new IllegalStateException("readParameter(" + mode[offset] + ")");
        }
    }

    private static int[] parseCode(long code) {
        int[] mode = new int[4];
        int intCode = (int) code;
        mode[0] = intCode % 100;
        mode[1] = (intCode / 100) % 10;
        mode[2] = (intCode / 1000) % 10;
        mode[3] = (intCode / 10000) % 10;
        return mode;
    }

    public static Pair<List<Long>, Long> maxThrusterSignal(String program, Long... items) {
        Optional<Pair<List<Long>, Long>> max = Permutations.of(items)
                .map(settings -> Pair.of(settings, thrusterSignal(program, settings)))
                .max((o1, o2) -> Comparator.comparingLong((ToLongFunction<Pair<List<Long>, Long>>) Pair::getRight).compare(o1, o2));
        return max.orElseGet(() -> Pair.of(Collections.emptyList(), -1L));
    }

    public static class Robot implements AutoCloseable {
        private final ExecutorService executorService;
        private final BlockingQueue<Long> inputQueue = new LinkedBlockingQueue<>();
        private final BlockingQueue<Long> outputQueue = new LinkedBlockingQueue<>();

        public Robot(String program) {
            executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                intcode(program, take(inputQueue), outputQueue::offer);
            });
        }

        public long action(long input) {
            inputQueue.offer(input);
            return take(outputQueue).getAsLong();
        }

        @Override
        public void close() {
            executorService.shutdown();
        }
    }
}
