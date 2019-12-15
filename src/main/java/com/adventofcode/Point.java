package com.adventofcode;

import java.util.Objects;

public class Point {
    private final long x;
    private final long y;

    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public Point move(Direction direction) {
        switch (direction) {
            case NORTH:
                return new Point(x, y - 1);
            case SOUTH:
                return new Point(x, y + 1);
            case WEST:
                return new Point(x - 1, y);
            case EAST:
                return new Point(x + 1, y);
            default:
                return new Point(Long.MAX_VALUE, Long.MAX_VALUE);
        }
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
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

    enum Direction {
        NORTH() {
            Direction reverse() {
                return SOUTH;
            }
        },
        SOUTH() {
            Direction reverse() {
                return NORTH;
            }
        },
        WEST() {
            Direction reverse() {
                return EAST;
            }
        },
        EAST() {
            Direction reverse() {
                return WEST;
            }
        };

        abstract Direction reverse();
    }
}
