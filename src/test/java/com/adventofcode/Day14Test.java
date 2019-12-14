package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class Day14Test {
    private static Map<String, Long> solveOrePerFuel(List<Reaction> reactions, long fuelQuantity) {
        Map<String, Long> quantities = new HashMap<>();
        quantities.put("FUEL", fuelQuantity);

        Map<String, Reaction> reactionMap = reactions.stream().collect(Collectors.toMap(k -> k.getChemicalOutput().getLeft(), Function.identity()));

        do {
            Optional<Map.Entry<String, Long>> firstChemical = quantities.entrySet().stream()
                    .filter(e -> !"ORE".equals(e.getKey()))
                    .filter(e -> e.getValue() > 0).findFirst();
            if (!firstChemical.isPresent()) {
                break;
            }

            Reaction reaction = reactionMap.get(firstChemical.get().getKey());
            long needed = firstChemical.get().getValue();
            long value = reaction.getChemicalOutput().getValue();
            reaction.apply(quantities, -Arithmetic.ceil(needed, value));
        }
        while (quantities.entrySet().stream().anyMatch(e -> !"ORE".equals(e.getKey()) && e.getValue() > 0));

        return quantities;
    }

    private static long solveFuelPerOre(List<Reaction> reactions, long oreQuantity) {
        long maxFuel = 1;
        while (true) {
            if (checkSolution(reactions, maxFuel, oreQuantity)) {
                maxFuel *= 2;
            } else {
                break;
            }
        }

        long minFuel = maxFuel / 2;
        while (minFuel != maxFuel) {
            long midFuel = (maxFuel + minFuel) / 2;
            if (checkSolution(reactions, midFuel, oreQuantity)) {
                minFuel = midFuel;
            } else {
                maxFuel = midFuel;
            }
            if (minFuel + 1 == maxFuel) {
                break;
            }
        }
        return minFuel;
    }

    private static boolean checkSolution(List<Reaction> reactions, long fuelQuantity, long oreQuantity) {
        Map<String, Long> quantity = solveOrePerFuel(reactions, fuelQuantity);
        return quantity.getOrDefault("ORE", 0L) < oreQuantity;
    }

    /**
     * --- Day 14: Space Stoichiometry ---
     * As you approach the rings of Saturn, your ship's low fuel indicator turns on. There isn't any fuel here, but the
     * rings have plenty of raw material. Perhaps your ship's Inter-Stellar Refinery Union brand nanofactory can turn
     * these raw materials into fuel.
     * <p>
     * You ask the nanofactory to produce a list of the reactions it can perform that are relevant to this process (your
     * puzzle input). Every reaction turns some quantities of specific input chemicals into some quantity of an output
     * chemical. Almost every chemical is produced by exactly one reaction; the only exception, ORE, is the raw material
     * input to the entire process and is not produced by a reaction.
     * <p>
     * You just need to know how much ORE you'll need to collect before you can produce one unit of FUEL.
     * <p>
     * Each reaction gives specific quantities for its inputs and output; reactions cannot be partially run, so only
     * whole integer multiples of these quantities can be used. (It's okay to have leftover chemicals when you're done,
     * though.) For example, the reaction 1 A, 2 B, 3 C => 2 D means that exactly 2 units of chemical D can be produced
     * by consuming exactly 1 A, 2 B and 3 C. You can run the full reaction as many times as necessary; for example, you
     * could produce 10 D by consuming 5 A, 10 B, and 15 C.
     * <p>
     * Suppose your nanofactory produces the following list of reactions:
     * <p>
     * 10 ORE => 10 A
     * 1 ORE => 1 B
     * 7 A, 1 B => 1 C
     * 7 A, 1 C => 1 D
     * 7 A, 1 D => 1 E
     * 7 A, 1 E => 1 FUEL
     * The first two reactions use only ORE as inputs; they indicate that you can produce as much of chemical A as you
     * want (in increments of 10 units, each 10 costing 10 ORE) and as much of chemical B as you want (each costing 1
     * ORE). To produce 1 FUEL, a total of 31 ORE is required: 1 ORE to produce 1 B, then 30 more ORE to produce the
     * 7 + 7 + 7 + 7 = 28 A (with 2 extra A wasted) required in the reactions to convert the B into C, C into D, D into E,
     * and finally E into FUEL. (30 A is produced because its reaction requires that it is created in increments of 10.)
     * <p>
     * Or, suppose you have the following list of reactions:
     * <p>
     * 9 ORE => 2 A
     * 8 ORE => 3 B
     * 7 ORE => 5 C
     * 3 A, 4 B => 1 AB
     * 5 B, 7 C => 1 BC
     * 4 C, 1 A => 1 CA
     * 2 AB, 3 BC, 4 CA => 1 FUEL
     * The above list of reactions requires 165 ORE to produce 1 FUEL:
     * <p>
     * Consume 45 ORE to produce 10 A.
     * Consume 64 ORE to produce 24 B.
     * Consume 56 ORE to produce 40 C.
     * Consume 6 A, 8 B to produce 2 AB.
     * Consume 15 B, 21 C to produce 3 BC.
     * Consume 16 C, 4 A to produce 4 CA.
     * Consume 2 AB, 3 BC, 4 CA to produce 1 FUEL.
     * Here are some larger examples:
     * <p>
     * 13312 ORE for 1 FUEL:
     * <p>
     * 157 ORE => 5 NZVS
     * 165 ORE => 6 DCFZ
     * 44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL
     * 12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ
     * 179 ORE => 7 PSHF
     * 177 ORE => 5 HKGWZ
     * 7 DCFZ, 7 PSHF => 2 XJWVT
     * 165 ORE => 2 GPVTF
     * 3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT
     * 180697 ORE for 1 FUEL:
     * <p>
     * 2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG
     * 17 NVRVD, 3 JNWZP => 8 VPVL
     * 53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL
     * 22 VJHF, 37 MNCFX => 5 FWMGM
     * 139 ORE => 4 NVRVD
     * 144 ORE => 7 JNWZP
     * 5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC
     * 5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV
     * 145 ORE => 6 MNCFX
     * 1 NVRVD => 8 CXFTF
     * 1 VJHF, 6 MNCFX => 4 RFSQX
     * 176 ORE => 6 VJHF
     * 2210736 ORE for 1 FUEL:
     * <p>
     * 171 ORE => 8 CNZTR
     * 7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL
     * 114 ORE => 4 BHXH
     * 14 VRPVC => 6 BMBT
     * 6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL
     * 6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT
     * 15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW
     * 13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW
     * 5 BMBT => 4 WPTQ
     * 189 ORE => 9 KTJDG
     * 1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP
     * 12 VRPVC, 27 CNZTR => 2 XDBXC
     * 15 KTJDG, 12 BHXH => 5 XCVML
     * 3 BHXH, 2 VRPVC => 7 MZWV
     * 121 ORE => 7 VRPVC
     * 7 XCVML => 6 RJRHP
     * 5 BHXH, 4 VRPVC => 5 LTCX
     * Given the list of reactions in your puzzle input, what is the minimum amount of ORE required to produce exactly 1 FUEL?
     */
    @Test
    void testSimpleNanofactory1() {
        List<Reaction> reactions = Stream.of(
                "10 ORE => 10 A",
                "1 ORE => 1 B",
                "7 A, 1 B => 1 C",
                "7 A, 1 C => 1 D",
                "7 A, 1 D => 1 E",
                "7 A, 1 E => 1 FUEL").map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 31L));
    }

    @Test
    void testSimpleNanofactory2() {
        List<Reaction> reactions = Stream.of(
                "9 ORE => 2 A",
                "8 ORE => 3 B",
                "7 ORE => 5 C",
                "3 A, 4 B => 1 AB",
                "5 B, 7 C => 1 BC",
                "4 C, 1 A => 1 CA",
                "2 AB, 3 BC, 4 CA => 1 FUEL").map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 165L));
    }

    @Test
    void testLargerNanofactory1() {
        List<Reaction> reactions = Stream.of(
                "157 ORE => 5 NZVS",
                "165 ORE => 6 DCFZ",
                "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
                "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
                "179 ORE => 7 PSHF",
                "177 ORE => 5 HKGWZ",
                "7 DCFZ, 7 PSHF => 2 XJWVT",
                "165 ORE => 2 GPVTF",
                "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 13312L));
    }

    @Test
    void testLargerNanofactory2() {
        List<Reaction> reactions = Stream.of(
                "2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
                "17 NVRVD, 3 JNWZP => 8 VPVL",
                "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
                "22 VJHF, 37 MNCFX => 5 FWMGM",
                "139 ORE => 4 NVRVD",
                "144 ORE => 7 JNWZP",
                "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
                "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
                "145 ORE => 6 MNCFX",
                "1 NVRVD => 8 CXFTF",
                "1 VJHF, 6 MNCFX => 4 RFSQX",
                "176 ORE => 6 VJHF"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 180697L));
    }

    @Test
    void testLargerNanofactory3() {
        List<Reaction> reactions = Stream.of(
                "171 ORE => 8 CNZTR",
                "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
                "114 ORE => 4 BHXH",
                "14 VRPVC => 6 BMBT",
                "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
                "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
                "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
                "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
                "5 BMBT => 4 WPTQ",
                "189 ORE => 9 KTJDG",
                "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
                "12 VRPVC, 27 CNZTR => 2 XDBXC",
                "15 KTJDG, 12 BHXH => 5 XCVML",
                "3 BHXH, 2 VRPVC => 7 MZWV",
                "121 ORE => 7 VRPVC",
                "7 XCVML => 6 RJRHP",
                "5 BHXH, 4 VRPVC => 5 LTCX"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 2210736L));
    }

    @Test
    void testInputPartOne() throws IOException {
        List<Reaction> reactions = FileUtils.readLines("/day/14/input").stream().map(Reaction::parseReaction).collect(Collectors.toList());

        Map<String, Long> fuelReaction = solveOrePerFuel(reactions, 1);
        assertThat(fuelReaction).contains(entry("ORE", 337075L));
    }

    /**
     * --- Part Two ---
     * After collecting ORE for a while, you check your cargo hold: 1 trillion (1000000000000) units of ORE.
     * <p>
     * With that much ore, given the examples above:
     * <p>
     * The 13312 ORE-per-FUEL example could produce 82892753 FUEL.
     * The 180697 ORE-per-FUEL example could produce 5586022 FUEL.
     * The 2210736 ORE-per-FUEL example could produce 460664 FUEL.
     * Given 1 trillion ORE, what is the maximum amount of FUEL you can produce?
     */
    @Test
    void testLargerTrillion1() {
        List<Reaction> reactions = Stream.of(
                "157 ORE => 5 NZVS",
                "165 ORE => 6 DCFZ",
                "44 XJWVT, 5 KHKGT, 1 QDVJ, 29 NZVS, 9 GPVTF, 48 HKGWZ => 1 FUEL",
                "12 HKGWZ, 1 GPVTF, 8 PSHF => 9 QDVJ",
                "179 ORE => 7 PSHF",
                "177 ORE => 5 HKGWZ",
                "7 DCFZ, 7 PSHF => 2 XJWVT",
                "165 ORE => 2 GPVTF",
                "3 DCFZ, 7 NZVS, 5 HKGWZ, 10 PSHF => 8 KHKGT"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        assertThat(solveFuelPerOre(reactions, 1_000_000_000_000L)).isEqualTo(82892753L);
    }

    @Test
    void testLargerTrillion2() {
        List<Reaction> reactions = Stream.of(
                "2 VPVL, 7 FWMGM, 2 CXFTF, 11 MNCFX => 1 STKFG",
                "17 NVRVD, 3 JNWZP => 8 VPVL",
                "53 STKFG, 6 MNCFX, 46 VJHF, 81 HVMC, 68 CXFTF, 25 GNMV => 1 FUEL",
                "22 VJHF, 37 MNCFX => 5 FWMGM",
                "139 ORE => 4 NVRVD",
                "144 ORE => 7 JNWZP",
                "5 MNCFX, 7 RFSQX, 2 FWMGM, 2 VPVL, 19 CXFTF => 3 HVMC",
                "5 VJHF, 7 MNCFX, 9 VPVL, 37 CXFTF => 6 GNMV",
                "145 ORE => 6 MNCFX",
                "1 NVRVD => 8 CXFTF",
                "1 VJHF, 6 MNCFX => 4 RFSQX",
                "176 ORE => 6 VJHF"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        assertThat(solveFuelPerOre(reactions, 1_000_000_000_000L)).isEqualTo(5586022L);
    }

    @Test
    void testLargerTrillion3() {
        List<Reaction> reactions = Stream.of(
                "171 ORE => 8 CNZTR",
                "7 ZLQW, 3 BMBT, 9 XCVML, 26 XMNCP, 1 WPTQ, 2 MZWV, 1 RJRHP => 4 PLWSL",
                "114 ORE => 4 BHXH",
                "14 VRPVC => 6 BMBT",
                "6 BHXH, 18 KTJDG, 12 WPTQ, 7 PLWSL, 31 FHTLT, 37 ZDVW => 1 FUEL",
                "6 WPTQ, 2 BMBT, 8 ZLQW, 18 KTJDG, 1 XMNCP, 6 MZWV, 1 RJRHP => 6 FHTLT",
                "15 XDBXC, 2 LTCX, 1 VRPVC => 6 ZLQW",
                "13 WPTQ, 10 LTCX, 3 RJRHP, 14 XMNCP, 2 MZWV, 1 ZLQW => 1 ZDVW",
                "5 BMBT => 4 WPTQ",
                "189 ORE => 9 KTJDG",
                "1 MZWV, 17 XDBXC, 3 XCVML => 2 XMNCP",
                "12 VRPVC, 27 CNZTR => 2 XDBXC",
                "15 KTJDG, 12 BHXH => 5 XCVML",
                "3 BHXH, 2 VRPVC => 7 MZWV",
                "121 ORE => 7 VRPVC",
                "7 XCVML => 6 RJRHP",
                "5 BHXH, 4 VRPVC => 5 LTCX"
        ).map(Reaction::parseReaction).collect(Collectors.toList());

        assertThat(solveFuelPerOre(reactions, 1_000_000_000_000L)).isEqualTo(460664L);
    }

    @Test
    void testInputPartTwo() throws IOException {
        List<Reaction> reactions = FileUtils.readLines("/day/14/input").stream().map(Reaction::parseReaction).collect(Collectors.toList());
        assertThat(solveFuelPerOre(reactions, 1_000_000_000_000L)).isEqualTo(5194174L);
    }

    private static class Reaction {
        private final List<Pair<String, Long>> chemicalInputs;
        private final Pair<String, Long> chemicalOutput;

        public Reaction(List<Pair<String, Long>> chemicalInputs, Pair<String, Long> chemicalOutput) {
            this.chemicalInputs = chemicalInputs;
            this.chemicalOutput = chemicalOutput;
        }

        static Reaction parseReaction(String line) {
            String[] split = line.split(" => ");
            List<Pair<String, Long>> inputs = Stream.of(split[0].split(", ")).map(Reaction::parseChemical).collect(Collectors.toList());
            Pair<String, Long> output = parseChemical(split[1]);
            return new Reaction(inputs, output);
        }

        static Pair<String, Long> parseChemical(String a) {
            String[] split = a.trim().split(" ");
            return Pair.of(split[1], Long.valueOf(split[0]));
        }

        static String writeChemical(Pair<String, Long> chemical) {
            return chemical.getRight() + " " + chemical.getLeft();
        }

        private void apply(Map<String, Long> quantities, long factor) {
            applyChemicalChange(quantities, chemicalOutput, factor);

            for (Pair<String, Long> input : chemicalInputs) {
                applyChemicalChange(quantities, input, -factor);
            }
        }

        private void applyChemicalChange(Map<String, Long> quantities, Pair<String, Long> chemical, long factor) {
            quantities.compute(chemical.getKey(), (k, v) -> {
                if (v == null) {
                    return factor * chemical.getRight();
                } else {
                    return v + factor * chemical.getRight();
                }
            });
        }

        public List<Pair<String, Long>> getChemicalInputs() {
            return chemicalInputs;
        }

        public Pair<String, Long> getChemicalOutput() {
            return chemicalOutput;
        }

        @Override
        public String toString() {
            return "Reaction[" + chemicalInputs.stream().map(Reaction::writeChemical).collect(Collectors.joining(", "))
                    + " => " + writeChemical(chemicalOutput) + "]";
        }
    }
}
