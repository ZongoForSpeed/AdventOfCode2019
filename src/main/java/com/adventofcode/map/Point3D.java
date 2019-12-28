package com.adventofcode.map;

import java.util.Objects;

public class Point3D {
    private final Point2D p;
    private final int z;

    public Point3D(int x, int y, int z) {
        p = new Point2D(x, y);
        this.z = z;
    }

    public Point3D(Point2D p, int z) {
        this.p = p;
        this.z = z;
    }

    public static long ManhattanDistance(Point3D a, Point3D b) {
        return Point2D.ManhattanDistance(a.p, b.p) + Math.abs(a.z - b.z);
    }

    public int getX() {
        return p.getX();
    }

    public int getY() {
        return p.getY();
    }

    public int getZ() {
        return z;
    }

    public Point2D project() {
        return p;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + p.getX() +
                ", y=" + p.getY() +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return z == point3D.z &&
                p.equals(point3D.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, z);
    }
}
