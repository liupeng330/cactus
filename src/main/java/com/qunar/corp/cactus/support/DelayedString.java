package com.qunar.corp.cactus.support;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * @author zhenyu.nie created on 2013 13-11-22 下午9:50
 */
public class DelayedString<T> {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Iterable<T> iterable;
    private final Function<T, String> toStringFunc;
    private final int nums;

    DelayedString(Iterable<T> iterable, Function<T, String> toStringFunc, int nums) {
        Preconditions.checkArgument(nums >= 0, "num %s should be greater than 0", nums);
        this.iterable = iterable;
        this.toStringFunc = toStringFunc;
        this.nums = nums;
    }

    @Override
    public String toString() {
        return makeIterableToString(toStringFunc, nums).apply(iterable);
    }

    static class IterableToString<T> implements Function<Iterable<T>, String> {
        private Function<T, String> toStringFunc;

        private int nums;

        public IterableToString(Function<T, String> toStringFunc, int nums) {
            Preconditions.checkArgument(nums >= 0, "nums %s should be greater or equal than 0", nums);
            this.toStringFunc = toStringFunc;
            this.nums = nums;
        }

        @Override
        public String apply(Iterable<T> input) {
            StringBuilder sb = new StringBuilder();
            int lineCount = 0;
            for (T t : input) {
                if (lineCount != 0) {
                    sb.append(NEW_LINE);
                }
                sb.append(toStringFunc.apply(t));
                lineCount++;
                if (nums != INFINITE_NUM && lineCount == nums) {
                    sb.append(NEW_LINE).append("and more......");
                    break;
                }
            }
            return sb.toString();
        }
    }

    public static final int DEFAULT_NUM = 3;

    public static final int INFINITE_NUM = 0;

    private static <T> Function<T, String> defaultToString() {
        return new Function<T, String>() {
            @Override
            public String apply(T input) {
                return input.toString();
            }
        };
    }

    private static <T> IterableToString<T> makeIterableToString(Function<T, String> function, int nums) {
        return new IterableToString<T>(function, nums);
    }

    public static <T> DelayedString<T> toString(Iterable<T> iterable, Function<T, String> toStringFunc, int nums) {
        return new DelayedString<T>(iterable, toStringFunc, nums);
    }

    public static <T> DelayedString<T> toString(Iterable<T> iterable, Function<T, String> toStringFunc) {
        return toString(iterable, toStringFunc, DEFAULT_NUM);
    }

    public static <T> DelayedString<T> toString(Iterable<T> iterable, int nums) {
        return toString(iterable, (Function<T,String>) defaultToString(), nums);
    }

    public static <T> DelayedString<T> toString(Iterable<T> iterable) {
        return toString(iterable, (Function<T, String>) defaultToString());
    }
}
