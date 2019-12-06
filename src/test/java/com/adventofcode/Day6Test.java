package com.adventofcode;

import com.adventofcode.utils.FileUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Day6Test {
    /**
     * --- Day 6: Universal Orbit Map ---
     * You've landed at the Universal Orbit Map facility on Mercury. Because navigation in space often involves
     * transferring between orbits, the orbit maps here are useful for finding efficient routes between, for example,
     * you and Santa. You download a map of the local orbits (your puzzle input).
     * <p>
     * Except for the universal Center of Mass (COM), every object in space is in orbit around exactly one other object.
     * An orbit looks roughly like this:
     * <p>
     * \
     * \
     * |
     * |
     * AAA--> o            o <--BBB
     * |
     * |
     * /
     * /
     * <p>
     * In this diagram, the object BBB is in orbit around AAA. The path that BBB takes around AAA (drawn with lines) is
     * only partly shown. In the map data, this orbital relationship is written AAA)BBB, which means "BBB is in orbit
     * around AAA".
     * <p>
     * Before you use your map data to plot a course, you need to make sure it wasn't corrupted during the download.
     * To verify maps, the Universal Orbit Map facility uses orbit count checksums - the total number of direct orbits
     * (like the one shown above) and indirect orbits.
     * <p>
     * Whenever A orbits B and B orbits C, then A indirectly orbits C. This chain can be any number of objects long: if
     * A orbits B, B orbits C, and C orbits D, then A indirectly orbits D.
     * <p>
     * For example, suppose you have the following map:
     * <p>
     * COM)B
     * B)C
     * C)D
     * D)E
     * E)F
     * B)G
     * G)H
     * D)I
     * E)J
     * J)K
     * K)L
     * Visually, the above map of orbits looks like this:
     * <p>
     * G - H       J - K - L
     * /           /
     * COM - B - C - D - E - F
     * \
     * I
     * In this visual representation, when two objects are connected by a line, the one on the right directly orbits
     * the one on the left.
     * <p>
     * Here, we can count the total number of orbits as follows:
     * <p>
     * D directly orbits C and indirectly orbits B and COM, a total of 3 orbits.
     * L directly orbits K and indirectly orbits J, E, D, C, B, and COM, a total of 7 orbits.
     * COM orbits nothing.
     * The total number of direct and indirect orbits in this example is 42.
     * <p>
     * What is the total number of direct and indirect orbits in your map data?
     */
    private static long internalCountOrbits(Multimap<String, String> graph, Map<String, Long> cache, String object) {
        Long value = cache.get(object);
        if (value != null) {
            return value;
        }

        long result = 0;
        Collection<String> orbits = graph.get(object);
        if (orbits != null) {
            result += orbits.size();
            for (String orbit : orbits) {
                result += internalCountOrbits(graph, cache, orbit);
            }
        }

        cache.put(object, result);
        return result;
    }

    public static long countOrbits(Multimap<String, String> graph) {
        long result = 0;

        Map<String, Long> cache = new HashMap<>();
        for (String object : graph.keySet()) {
            result += internalCountOrbits(graph, cache, object);
        }

        return result;
    }

    private static Multimap<String, String> readGraph(String filename) throws IOException {
        Multimap<String, String> graph = HashMultimap.create();
        List<String> lines = FileUtils.readLines(filename);
        for (String line : lines) {
            String[] orbit = line.split("\\)");
            graph.put(orbit[1], orbit[0]);
        }

        return graph;

    }

    @Test
    void testExample() throws IOException {
        Multimap<String, String> graph = readGraph("/day/6/example");
        assertThat(internalCountOrbits(graph, new HashMap<>(), "D")).isEqualTo(3);
        assertThat(internalCountOrbits(graph, new HashMap<>(), "L")).isEqualTo(7);
        assertThat(countOrbits(graph)).isEqualTo(42);
    }

    @Test
    void testInputPartOne() throws IOException {
        Multimap<String, String> graph = readGraph("/day/6/input");
        assertThat(countOrbits(graph)).isEqualTo(144909);
    }
}
