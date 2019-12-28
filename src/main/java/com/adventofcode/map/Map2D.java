package com.adventofcode.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Map2D implements Map<Point2D, Long> {
    private final Map<Point2D, Long> map;

    public Map2D() {
        this.map = new HashMap<>();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Long get(Object key) {
        return map.get(key);
    }

    public Long put(Point2D key, Long value) {
        return map.put(key, value);
    }

    public Long remove(Object key) {
        return map.remove(key);
    }

    public void putAll(Map<? extends Point2D, ? extends Long> m) {
        map.putAll(m);
    }

    public void clear() {
        map.clear();
    }

    public Set<Point2D> keySet() {
        return map.keySet();
    }

    public Collection<Long> values() {
        return map.values();
    }

    public Set<Map.Entry<Point2D, Long>> entrySet() {
        return map.entrySet();
    }

    public List<String> print(char[][] view, Function<Long, Character> supplier) {
        for (Map.Entry<Point2D, Long> entry : map.entrySet()) {
            view[entry.getKey().getY()][entry.getKey().getX()] = supplier.apply(entry.getValue());
        }

        for (char[] chars : view) {
            System.out.println(String.valueOf(chars));
        }
        return Arrays.stream(view).map(String::valueOf).collect(Collectors.toList());
    }

    public List<String> print(Function<Long, Character> supplier) {
        int maxX = map.keySet().stream().mapToInt(Point2D::getX).max().orElse(0);
        int minX = map.keySet().stream().mapToInt(Point2D::getX).min().orElse(0);
        int maxY = map.keySet().stream().mapToInt(Point2D::getY).max().orElse(0);
        int minY = map.keySet().stream().mapToInt(Point2D::getY).min().orElse(0);

        char[][] view = new char[maxY - minY + 1][maxX - minX + 1];
        for (char[] chars : view) {
            Arrays.fill(chars, ' ');
        }

        for (Map.Entry<Point2D, Long> entry : map.entrySet()) {
            view[entry.getKey().getY() - minY][entry.getKey().getX() - minX] = supplier.apply(entry.getValue());
        }

        for (char[] chars : view) {
            System.out.println(String.valueOf(chars));
        }
        return Arrays.stream(view).map(String::valueOf).collect(Collectors.toList());
    }
}
