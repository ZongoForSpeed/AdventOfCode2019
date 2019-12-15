package com.adventofcode.map;

public enum Direction {
    NORTH() {
        @Override
        public Direction reverse() {
            return SOUTH;
        }

        @Override
        public Direction left() {
            return WEST;
        }

        @Override
        public Direction right() {
            return EAST;
        }
    },
    SOUTH() {
        @Override
        public Direction reverse() {
            return NORTH;
        }

        @Override
        public Direction left() {
            return EAST;
        }

        @Override
        public Direction right() {
            return WEST;
        }
    },
    WEST() {
        @Override
        public Direction reverse() {
            return EAST;
        }

        @Override
        public Direction left() {
            return SOUTH;
        }

        @Override
        public Direction right() {
            return NORTH;
        }
    },
    EAST() {
        @Override
        public Direction reverse() {
            return WEST;
        }

        @Override
        public Direction left() {
            return NORTH;
        }

        @Override
        public Direction right() {
            return SOUTH;
        }
    };

    public abstract Direction reverse();

    public abstract Direction left();

    public abstract Direction right();
}
