package com.adventofcode.maths;

import org.apache.commons.lang3.tuple.Triple;

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

    public static long inverseModulaire(long a, long n) {
        long inverse;
        Triple<Long, Long, Long> result = Bezout(a, n);
        inverse = result.getMiddle();
        if (inverse < 0)
            return inverse + n;
        else
            return inverse;
    }

    public static Triple<Long, Long, Long> Bezout(long a, long b) {
        // https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm#Pseudocode
        long s = 0, old_s = 1;
        long t = 1, old_t = 0;
        long r = b, old_r = a;
        while (r != 0) {
            long quotient = old_r / r;
            long new_r = old_r - quotient * r;
            long new_s = old_s - quotient * s;
            long new_t = old_t - quotient * t;

            old_r = r;
            old_s = s;
            old_t = t;

            r = new_r;
            s = new_s;
            t = new_t;
        }

        // std::cout << "Bézout coefficients:" << std::make_pair(old_s, old_t) << std::endl;
        // std::cout << "greatest common divisor:" << old_r << std::endl;
        // std::cout << "quotients by the gcd:" << std::make_pair(t, s) << std::endl;

        return Triple.of(old_r, old_s, old_t);
    }

    public static long power(long base, long exposant) {
        long resultat = 1;
        while (exposant > 0) {
            if (exposant % 2 != 0)
                resultat *= base;
            exposant /= 2;
            base *= base;
        }
        return resultat;
    }

    public static long powerMod(long base, long exposant, long modulo) {
        long resultat = 1;
        while (exposant > 0) {
            if (exposant % 2 != 0)
                resultat = (base * resultat) % modulo;
            exposant /= 2;
            base = (base * base) % modulo;
        }
        return resultat;
    }
}
