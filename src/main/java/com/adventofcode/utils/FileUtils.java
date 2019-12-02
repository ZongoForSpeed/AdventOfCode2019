package com.adventofcode.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> readLines(String file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(file)))) {
            br.lines().forEach(lines::add);
        }
        return lines;
    }

    public static String readLine(String file) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(file)))) {
            return br.readLine();
        }
    }
}
