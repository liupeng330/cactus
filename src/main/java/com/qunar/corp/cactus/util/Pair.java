/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.util;

/**
 * @author zhenyu.nie created on 2013 13-11-1 上午5:43
 */
public class Pair<L, R> {

    public final L left;

    public final R right;

    public static <U, V> Pair<U, V> makePair(U u, V v) {
        return new Pair<U, V>(u, v);
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    //为了在前端中能取到left和right，所以添加get方法
    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair other = (Pair) o;

        if (left != null ? !left.equals(other.left) : other.left != null) return false;
        if (right != null ? !right.equals(other.right) : other.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
