package com.adventofcode.map;

import java.util.Objects;

public class Point2D {
    private final int x;
    private final int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static long ManhattanDistance(Point2D a, Point2D b) {
        return 100 * (Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()));
    }

    public Point2D move(Direction direction) {
        switch (direction) {
            case NORTH:
                return new Point2D(x, y - 1);
            case SOUTH:
                return new Point2D(x, y + 1);
            case WEST:
                return new Point2D(x - 1, y);
            case EAST:
                return new Point2D(x + 1, y);
            default:
                return new Point2D(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }
    }

    public Point2D move(Point2D d) {
        return new Point2D(x + d.x, y + d.y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2D point = (Point2D) o;
        return x == point.x &&
                y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
