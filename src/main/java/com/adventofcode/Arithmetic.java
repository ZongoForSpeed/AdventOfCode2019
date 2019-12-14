package com.adventofcode;

public class Arithmetic {
    static int lcm(int a, int b, int c) {
        int lcm = lcm(a, b);
        return c * (lcm / gcd(lcm, c));
    }

    static long lcm(long a, long b, long c) {
        long lcm = lcm(a, b);
        return c * (lcm / gcd(lcm, c));
    }

    static int gcd(int a, int b, int c) {
        return gcd(a, gcd(b, c));
    }

    static long gcd(long a, long b, long c) {
        return gcd(a, gcd(b, c));
    }

    static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }

    static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    static int gcd(int a, int b) {
        if (a == 0)
            return b;
        if (b == 0)
            return a;

        int gcd;
        while (true) {
            gcd = a % b;
            if (gcd == 0) {
                gcd = b;
                break;
            }
            a = b;
            b = gcd;
        }
        return gcd;
    }

    static long gcd(long a, long b) {
        if (a == 0)
            return b;
        if (b == 0)
            return a;

        long gcd;
        while (true) {
            gcd = a % b;
            if (gcd == 0) {
                gcd = b;
                break;
            }
            a = b;
            b = gcd;
        }
        return gcd;
    }

    public static long ceil(long n, long d) {
        return (n + d - 1) / d;
    }

    public static int ceil(int n, int d) {
        return (n + d - 1) / d;
    }
}
