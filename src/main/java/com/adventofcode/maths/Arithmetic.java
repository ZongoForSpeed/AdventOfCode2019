package com.adventofcode.maths;

public class Arithmetic {
    public static int lcm(int a, int b, int c) {
        int lcm = lcm(a, b);
        return c * (lcm / gcd(lcm, c));
    }

    public static long lcm(long a, long b, long c) {
        long lcm = lcm(a, b);
        return c * (lcm / gcd(lcm, c));
    }

    public static int gcd(int a, int b, int c) {
        return gcd(a, gcd(b, c));
    }

    public static long gcd(long a, long b, long c) {
        return gcd(a, gcd(b, c));
    }

    public static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }

    public static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    public static int gcd(int a, int b) {
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

    public static long gcd(long a, long b) {
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
