package com.adventofcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Digits {
    static List<Integer> digits(int n) {
        List<Integer> d = new ArrayList<>();
        while (n > 0) {
            d.add(n % 10);
            n /= 10;
        }

        Collections.reverse(d);
        return d;
    }
}
