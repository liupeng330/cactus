package com.qunar.corp.cactus.service.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import java.util.AbstractList;
import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2014 2014/8/14 15:22
 */
public class BiDirectionPrefixSearcher<T> extends AbstractList<T> implements PrefixSearcher<T>, BiDirectionCollection<T> {

    private final PrefixSearcherImpl<T> searcher;

    private final Function<T, String> keyGetter;

    private final PrefixSearcherImpl.SearchAssist searchAssist;

    public BiDirectionPrefixSearcher(Set<T> inputs) {
        this(inputs, PrefixSearcherImpl.defaultSearchAssist());
    }

    public BiDirectionPrefixSearcher(Set<T> inputs, PrefixSearcherImpl.SearchAssist searchAssist) {
        keyGetter = PrefixSearcherImpl.defaultKeyGetter();
        searcher = PrefixSearcherImpl.Builder.from(inputs).searchAssist(searchAssist).keyGetter(keyGetter).build();
        this.searchAssist = searchAssist;
    }

    @Override
    @SuppressWarnings("all")
    public int get(T object) {
        Preconditions.checkNotNull(object);
        String key = keyGetter.apply(object);
        Preconditions.checkNotNull(key);

        // 这个binarySearch不是一般意义上的binarySearch，返回的将是集合中大于等于key的最小index，找不到时返回size()
        // 这里对集合中与object的key相等的那一部分部分进行搜索
        int index = searcher.searchEqualOrUpperIndex(key);
        while (index != searcher.size()) {
            T atIndex = searcher.get(index);
            if (object.equals(atIndex)) {
                return index;
            } else if (keyEqual(key, keyGetter.apply(atIndex))) {
                ++index;
            } else {
                return -1;
            }
        }
        return -1;
    }

    private boolean keyEqual(String lhs, String rhs) {
        return searchAssist.comparator.compare(lhs, rhs) == 0;
    }

    @Override
    public T get(int index) {
        return searcher.get(index);
    }

    @Override
    public int size() {
        return searcher.size();
    }

    @Override
    public List<T> prefixSearch(String word) {
        return searcher.prefixSearch(word);
    }
}
