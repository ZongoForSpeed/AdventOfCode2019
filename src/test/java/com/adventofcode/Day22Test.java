package com.adventofcode;

import com.adventofcode.maths.Arithmetic;
import com.adventofcode.matrix.Matrix2D;
import com.adventofcode.utils.FileUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Day22Test {
    public static final String DEAL_WITH_INCREMENT = "deal with increment ";
    public static final String DEAL_INTO_NEW_STACK = "deal into new stack";
    public static final String CUT_N_CARDS = "cut ";

    private static List<Integer> dealIntoNewStack(List<Integer> cards) {
        List<Integer> result = new ArrayList<>(cards);
        Collections.reverse(result);
        return result;
    }

    private static List<Integer> cutCards(List<Integer> cards, int n) {
        List<Integer> result = new ArrayList<>();
        if (n > 0) {
            result.addAll(cards.subList(n, cards.size()));
            result.addAll(cards.subList(0, n));
        } else {
            result.addAll(cards.subList(cards.size() + n, cards.size()));
            result.addAll(cards.subList(0, cards.size() + n));
        }

        return result;
    }

    private static List<Integer> dealWithIncrement(List<Integer> cards, int n) {
        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[(i * n) % cards.size()] = cards.get(i);
        }

        return IntStream.of(result).boxed().collect(Collectors.toList());
    }

    private static List<Integer> slamShuffle(List<Integer> cards, List<String> commands) {
        for (String command : commands) {
            if (command.startsWith(DEAL_WITH_INCREMENT)) {
                int increment = Integer.parseInt(command.substring(DEAL_WITH_INCREMENT.length()));
                cards = dealWithIncrement(cards, increment);
            } else if (command.startsWith(DEAL_INTO_NEW_STACK)) {
                cards = dealIntoNewStack(cards);
            } else if (command.startsWith(CUT_N_CARDS)) {
                int argument = Integer.parseInt(command.substring(CUT_N_CARDS.length()));
                cards = cutCards(cards, argument);
            } else {
                throw new IllegalStateException("Unknown command '" + command + "'");
            }
        }

        return cards;
    }

    private static long slamShuffle(long position, long modulus, List<String> commands) {
        for (String command : commands) {
            if (command.startsWith(DEAL_WITH_INCREMENT)) {
                int increment = Integer.parseInt(command.substring(DEAL_WITH_INCREMENT.length()));
                position = multiplyMod(position, increment, modulus);
                // position = (position + modulus - increment) % modulus;
            } else if (command.startsWith(DEAL_INTO_NEW_STACK)) {
                position = modulus - 1 - position;
            } else if (command.startsWith(CUT_N_CARDS)) {
                int argument = Integer.parseInt(command.substring(CUT_N_CARDS.length()));
                position = (position + modulus - argument) % modulus;
            } else {
                throw new IllegalStateException("Unknown command '" + command + "'");
            }
        }

        return position;
    }

    private static long multiplyMod(long a, long b, long modulus) {
        return multiplyMod(BigInteger.valueOf(a), BigInteger.valueOf(b), BigInteger.valueOf(modulus));
    }

    private static long multiplyMod(BigInteger a, BigInteger b, BigInteger modulus) {
        return a.multiply(b).mod(modulus).longValue();
    }

    private static long inverseSlamShuffle(List<String> commands, long position, long modulus) {
        Collections.reverse(commands);
        for (String command : commands) {
            if (command.startsWith(DEAL_WITH_INCREMENT)) {
                int increment = Integer.parseInt(command.substring(DEAL_WITH_INCREMENT.length()));
                position = multiplyMod(position, Arithmetic.inverseModulaire(increment, modulus), modulus);
                // position = (position + modulus - increment) % modulus;
            } else if (command.startsWith(DEAL_INTO_NEW_STACK)) {
                position = modulus - 1 - position;
            } else if (command.startsWith(CUT_N_CARDS)) {
                int argument = Integer.parseInt(command.substring(CUT_N_CARDS.length()));
                position = (position + modulus + argument) % modulus;
            } else {
                throw new IllegalStateException("Unknown command '" + command + "'");
            }
        }

        return position;
    }

    private static long inverseSlamShuffle(List<String> commands, long position, long modulus, long exponent) {
        Matrix2D m = new Matrix2D(1, 0, 0, 1);

        for (String command : commands) {
            if (command.startsWith(DEAL_WITH_INCREMENT)) {
                long argument = Integer.parseInt(command.substring(DEAL_WITH_INCREMENT.length()));
                argument = Arithmetic.inverseModulaire(argument, modulus);
                m = m.multiply(new Matrix2D(argument, 0, 0, 1), modulus);
            } else if (command.startsWith(DEAL_INTO_NEW_STACK)) {
                m = m.multiply(new Matrix2D(-1, modulus - 1, 0, 1), modulus);
            } else if (command.startsWith(CUT_N_CARDS)) {
                int argument = Integer.parseInt(command.substring(CUT_N_CARDS.length()));
                m = m.multiply(new Matrix2D(1, argument, 0, 1), modulus);
            } else {
                throw new IllegalStateException("Unknown command '" + command + "'");
            }
        }

        m = Matrix2D.power(m, exponent, modulus);

        return (m.a11 * position + m.a12) % modulus;
    }

    /**
     * --- Day 22: Slam Shuffle ---
     * There isn't much to do while you wait for the droids to repair your ship. At least you're drifting in the right
     * direction. You decide to practice a new card shuffle you've been working on.
     * <p>
     * Digging through the ship's storage, you find a deck of space cards! Just like any deck of space cards, there are
     * 10007 cards in the deck numbered 0 through 10006. The deck must be new - they're still in factory order, with 0
     * on the top, then 1, then 2, and so on, all the way through to 10006 on the bottom.
     * <p>
     * You've been practicing three different techniques that you use while shuffling. Suppose you have a deck of only
     * 10 cards (numbered 0 through 9):
     * <p>
     * To deal into new stack, create a new stack of cards by dealing the top card of the deck onto the top of the new
     * stack repeatedly until you run out of cards:
     * <p>
     * Top          Bottom
     * 0 1 2 3 4 5 6 7 8 9   Your deck
     * New stack
     * <p>
     * 1 2 3 4 5 6 7 8 9   Your deck
     * 0   New stack
     * <p>
     * 2 3 4 5 6 7 8 9   Your deck
     * 1 0   New stack
     * <p>
     * 3 4 5 6 7 8 9   Your deck
     * 2 1 0   New stack
     * <p>
     * Several steps later...
     * <p>
     * 9   Your deck
     * 8 7 6 5 4 3 2 1 0   New stack
     * <p>
     * Your deck
     * 9 8 7 6 5 4 3 2 1 0   New stack
     * Finally, pick up the new stack you've just created and use it as the deck for the next technique.
     * <p>
     * To cut N cards, take the top N cards off the top of the deck and move them as a single unit to the bottom of the
     * deck, retaining their order. For example, to cut 3:
     * <p>
     * Top          Bottom
     * 0 1 2 3 4 5 6 7 8 9   Your deck
     * <p>
     * 3 4 5 6 7 8 9   Your deck
     * 0 1 2                 Cut cards
     * <p>
     * 3 4 5 6 7 8 9         Your deck
     * 0 1 2   Cut cards
     * <p>
     * 3 4 5 6 7 8 9 0 1 2   Your deck
     * You've also been getting pretty good at a version of this technique where N is negative! In that case, cut (the
     * absolute value of) N cards from the bottom of the deck onto the top. For example, to cut -4:
     * <p>
     * Top          Bottom
     * 0 1 2 3 4 5 6 7 8 9   Your deck
     * <p>
     * 0 1 2 3 4 5           Your deck
     * 6 7 8 9   Cut cards
     * <p>
     * 0 1 2 3 4 5   Your deck
     * 6 7 8 9               Cut cards
     * <p>
     * 6 7 8 9 0 1 2 3 4 5   Your deck
     * To deal with increment N, start by clearing enough space on your table to lay out all of the cards individually
     * in a long line. Deal the top card into the leftmost position. Then, move N positions to the right and deal the
     * next card there. If you would move into a position past the end of the space on your table, wrap around and keep
     * counting from the leftmost card again. Continue this process until you run out of cards.
     * <p>
     * For example, to deal with increment 3:
     * <p>
     * <p>
     * 0 1 2 3 4 5 6 7 8 9   Your deck
     * . . . . . . . . . .   Space on table
     * ^                     Current position
     * <p>
     * Deal the top card to the current position:
     * <p>
     * 1 2 3 4 5 6 7 8 9   Your deck
     * 0 . . . . . . . . .   Space on table
     * ^                     Current position
     * <p>
     * Move the current position right 3:
     * <p>
     * 1 2 3 4 5 6 7 8 9   Your deck
     * 0 . . . . . . . . .   Space on table
     * ^               Current position
     * <p>
     * Deal the top card:
     * <p>
     * 2 3 4 5 6 7 8 9   Your deck
     * 0 . . 1 . . . . . .   Space on table
     * ^               Current position
     * <p>
     * Move right 3 and deal:
     * <p>
     * 3 4 5 6 7 8 9   Your deck
     * 0 . . 1 . . 2 . . .   Space on table
     * ^         Current position
     * <p>
     * Move right 3 and deal:
     * <p>
     * 4 5 6 7 8 9   Your deck
     * 0 . . 1 . . 2 . . 3   Space on table
     * ^   Current position
     * <p>
     * Move right 3, wrapping around, and deal:
     * <p>
     * 5 6 7 8 9   Your deck
     * 0 . 4 1 . . 2 . . 3   Space on table
     * ^                 Current position
     * <p>
     * And so on:
     * <p>
     * 0 7 4 1 8 5 2 9 6 3   Space on table
     * Positions on the table which already contain cards are still counted; they're not skipped. Of course, this
     * technique is carefully designed so it will never put two cards in the same position or leave a position empty.
     * <p>
     * Finally, collect the cards on the table so that the leftmost card ends up at the top of your deck, the card to
     * its right ends up just below the top card, and so on, until the rightmost card ends up at the bottom of the deck.
     * <p>
     * The complete shuffle process (your puzzle input) consists of applying many of these techniques. Here are some
     * examples that combine techniques; they all start with a factory order deck of 10 cards:
     * <p>
     * deal with increment 7
     * deal into new stack
     * deal into new stack
     * Result: 0 3 6 9 2 5 8 1 4 7
     * cut 6
     * deal with increment 7
     * deal into new stack
     * Result: 3 0 7 4 1 8 5 2 9 6
     * deal with increment 7
     * deal with increment 9
     * cut -2
     * Result: 6 3 0 7 4 1 8 5 2 9
     * deal into new stack
     * cut -2
     * deal with increment 7
     * cut 8
     * cut -4
     * deal with increment 7
     * cut 3
     * deal with increment 9
     * deal with increment 3
     * cut -1
     * Result: 9 2 5 8 1 4 7 0 3 6
     * Positions within the deck count from 0 at the top, then 1 for the card immediately below the top card, and so on to the bottom. (That is, cards start in the position matching their number.)
     * <p>
     * After shuffling your factory order deck of 10007 cards, what is the position of card 2019?
     */
    @Test
    void testDealIntoNewStack() {
        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(dealIntoNewStack(cards)).containsExactly(9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
    }

    @Test
    void testCutCards() {
        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(cutCards(cards, 3)).containsExactly(3, 4, 5, 6, 7, 8, 9, 0, 1, 2);
        assertThat(cutCards(cards, -4)).containsExactly(6, 7, 8, 9, 0, 1, 2, 3, 4, 5);
    }

    @Test
    void testDealWithIncrement() {
        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(dealWithIncrement(cards, 3)).containsExactly(0, 7, 4, 1, 8, 5, 2, 9, 6, 3);
    }

    @Test
    void testSimpleExample1() {
        List<String> commands = Arrays.asList("deal with increment 7",
                DEAL_INTO_NEW_STACK,
                DEAL_INTO_NEW_STACK);

        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        List<Integer> shuffle = slamShuffle(cards, commands);
        assertThat(shuffle).containsExactly(0, 3, 6, 9, 2, 5, 8, 1, 4, 7);
    }

    @Test
    void testSimpleExample2() {
        List<String> commands = Arrays.asList("cut 6",
                "deal with increment 7",
                DEAL_INTO_NEW_STACK);

        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(slamShuffle(cards, commands)).containsExactly(3, 0, 7, 4, 1, 8, 5, 2, 9, 6);
    }

    @Test
    void testSimpleExample3() {
        List<String> commands = Arrays.asList("deal with increment 7",
                "deal with increment 9",
                "cut -2");

        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(slamShuffle(cards, commands)).containsExactly(6, 3, 0, 7, 4, 1, 8, 5, 2, 9);
    }

    @Test
    void testSimpleExample4() {
        List<String> commands = Arrays.asList(DEAL_INTO_NEW_STACK,
                "cut -2",
                "deal with increment 7",
                "cut 8",
                "cut -4",
                "deal with increment 7",
                "cut 3",
                "deal with increment 9",
                "deal with increment 3",
                "cut -1");

        List<Integer> cards = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        assertThat(slamShuffle(cards, commands)).containsExactly(9, 2, 5, 8, 1, 4, 7, 0, 3, 6);
    }

    @Test
    void testInputPartOne() throws IOException {
        List<String> commands = FileUtils.readLines("/day/22/input");
        List<Integer> cards = IntStream.range(0, 10007).boxed().collect(Collectors.toList());
        List<Integer> shuffle = slamShuffle(cards, commands);
        assertThat(shuffle.indexOf(2019)).isEqualTo(4775);
        assertThat(slamShuffle(2019, 10007, commands)).isEqualTo(4775);
        assertThat(inverseSlamShuffle(commands, 4775, 10007)).isEqualTo(2019);
    }

    /**
     * --- Part Two ---
     * After a while, you realize your shuffling skill won't improve much more with merely a single deck of cards. You
     * ask every 3D printer on the ship to make you some more cards while you check on the ship repairs. While reviewing
     * the work the droids have finished so far, you think you see Halley's Comet fly past!
     * <p>
     * When you get back, you discover that the 3D printers have combined their power to create for you a single, giant,
     * brand new, factory order deck of 119315717514047 space cards.
     * <p>
     * Finally, a deck of cards worthy of shuffling!
     * <p>
     * You decide to apply your complete shuffle process (your puzzle input) to the deck 101741582076661 times in a row.
     * <p>
     * You'll need to be careful, though - one wrong move with this many cards and you might overflow your entire ship!
     * <p>
     * After shuffling your new, giant, factory order deck that many times, what number is on the card that ends up in
     * position 2020?
     */
    @Test
    void testSolvePartTwo() throws IOException {
        List<String> commands = FileUtils.readLines("/day/22/input");
        long result = inverseSlamShuffle(commands, 2020, 119315717514047L, 101741582076661L);
        assertThat(result).isEqualTo(37889219674304L);
    }
}
