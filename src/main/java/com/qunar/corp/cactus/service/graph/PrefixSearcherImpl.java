package com.qunar.corp.cactus.service.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.*;

/**
 * @author zhenyu.nie created on 2014 2014/8/14 13:12
 *
 * 该类实现了前缀字符串搜索，支持各种对象，各种比较方法，并且实现了List接口（不可变）
 * keyGetter表示如何从对象中获取一个String类型的key，这个key将被用来做前缀搜索
 * comparator和greater是配套的，comparator表示key的比较策略，
 * greater表示如何根据一个key找到一个刚好比key大但是又不会在搜索时被key搜出来的字符
 * 举个例子，假如对象为String，keyGetter返回它自己，那么当comparator为字符串排序的时候，ab的greater将返回ac，
 * 另外一个例子，计入对象为String，keyGetter返回它自己，那么当comparator为字符串忽略大小写排序的时候，ab的greater返回(ac, aC, Ac, AC)中一种即可
 */
public class PrefixSearcherImpl<T> extends AbstractList<T> implements PrefixSearcher<T> {

    private final ImmutableList<T> values;

    private final Function<T, String> keyGetter;

    private final Ordering<String> comparator;

    private final Function<String, String> greater;

    public PrefixSearcherImpl(ImmutableList<T> values, Function<T, String> keyGetter, Ordering<String> comparator, Function<String, String> greater) {
        this.values = values;
        this.keyGetter = keyGetter;
        this.comparator = comparator;
        this.greater = greater;
    }

    @Override
    public T get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public List<T> prefixSearch(String word) {
        int startIndex = prefixSearchStart(word);
        if (startIndex < 0) {
            return ImmutableList.of();
        } else {
            return values.subList(startIndex, prefixSearchEnd(word));
        }
    }

    // 这个binarySearch不是一般意义上的binarySearch，返回的将是集合中大于等于key的最小index，找不到时返回size()
    // 所以没有作为公共接口
    int searchEqualOrUpperIndex(String word) {
        return BinarySearch.searchEqualOrUpperIndex(values, word, comparator, keyGetter);
    }

    private int prefixSearchStart(String word) {
        int equalOrUpperIndex = searchEqualOrUpperIndex(word);

        if (prefixWordExist(word, equalOrUpperIndex)) {
            return equalOrUpperIndex;
        } else {
            return -1;
        }
    }

    private boolean prefixWordExist(String word, int equalOrUpperIndex) {
        return equalOrUpperIndex != values.size()
                && comparator.compare(keyGetter.apply(values.get(equalOrUpperIndex)), greater.apply(word)) < 0;
    }

    private int prefixSearchEnd(String word) {
        return searchEqualOrUpperIndex(greater.apply(word));
    }

    public static class Builder<T> {

        private List<T> nodes = Lists.newArrayList();

        private Function<T, String> keyGetter;

        private SearchAssist searchAssist;

        public static <F> Builder<F> from(Collection<F> nodes) {
            return new Builder<F>(nodes);
        }

        private Builder(Collection<T> nodes) {
            this.nodes.addAll(nodes);
        }

        public Builder<T> searchAssist(SearchAssist searchAssist) {
            this.searchAssist = searchAssist;
            return this;
        }

        public Builder<T> keyGetter(Function<T, String> greater) {
            this.keyGetter = greater;
            return this;
        }

        @SuppressWarnings("unchecked")
        public PrefixSearcherImpl<T> build() {
            if (searchAssist == null) {
                searchAssist = defaultSearchAssist();
            }

            if (keyGetter == null) {
                keyGetter = defaultKeyGetter();
            }

            if (nodes != null) {
                ImmutableList<T> result = searchAssist.comparator.onResultOf(keyGetter).immutableSortedCopy(nodes);
                return new PrefixSearcherImpl<T>(result, keyGetter, searchAssist.comparator, searchAssist.greater);
            } else {
                return new PrefixSearcherImpl<T>(ImmutableList.<T>of(), keyGetter, searchAssist.comparator, searchAssist.greater);
            }
        }
    }

    public static class SearchAssist {

        final Ordering<String> comparator;

        final Function<String, String> greater;

        public SearchAssist(Ordering<String> comparator, Function<String, String> greater) {
            Preconditions.checkNotNull(comparator);
            Preconditions.checkNotNull(greater);
            this.comparator = comparator;
            this.greater = greater;
        }
    }

    public static SearchAssist defaultSearchAssist() {
        return DEFAULT_SEARCH_ASSIST;
    }

    private static final SearchAssist DEFAULT_SEARCH_ASSIST = new SearchAssist(Ordering.<String>natural(),
            new Function<String, String>() {
                @Override
                public String apply(String input) {
                    Preconditions.checkArgument(!Strings.isNullOrEmpty(input));
                    char[] chars = input.toCharArray();
                    char lastChar = chars[chars.length - 1];
                    if (lastChar > lastChar + 1) {
                        throw new IllegalArgumentException("illegal word, last char is max char");
                    } else {
                        chars[chars.length - 1] += 1;
                    }
                    return new String(chars);
                }
            });

    @SuppressWarnings("unchecked")
    public static <T> Function<T, String> defaultKeyGetter() {
        return (Function<T, String>) DEFAULT_KEY_GETTER;
    }

    private static final Function<Object, String> DEFAULT_KEY_GETTER = new Function<Object, String>() {
        @Override
        public String apply(Object input) {
            return input.toString();
        }
    };
}
