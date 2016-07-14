package com.qunar.corp.cactus.service.graph;

import com.google.common.base.Function;

import java.util.Comparator;
import java.util.List;

/**
 * @author zhenyu.nie created on 2014 14-1-21 下午4:14
 */
public class BinarySearch {

    public static <T> int searchEqualOrUpperIndex(List<T> values, T input, Comparator<T> comparator) {
        return searchEqualOrUpperIndex(values, input, comparator, BinarySearch.<T>self());
    }

    // return the first value's key equal to input,
    // or the first value's key Greater than input,
    // or values.size()
    public static <T, F> int searchEqualOrUpperIndex(List<F> values, T input, Comparator<T> comparator, Function<F, T> keyGetter) {
        int begin = -1;
        int end = values.size();
        while (end - begin > 1) {
            int mid = begin + (end - begin) / 2;
            T midValue = keyGetter.apply(values.get(mid));
            if (comparator.compare(midValue, input) < 0) {
                begin = mid;
            } else {
                end = mid;
            }
        }
        return end;
    }

    @SuppressWarnings("unchecked")
    static <T> Function<T, T> self() {
        return (Function<T, T>) SELF_FUNC;
    }

    private static Function<Object, Object> SELF_FUNC = new Function<Object, Object>() {
        @Override
        public Object apply(Object input) {
            return input;
        }
    };
}
