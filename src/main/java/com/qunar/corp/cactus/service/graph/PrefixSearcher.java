package com.qunar.corp.cactus.service.graph;

import java.util.List;

/**
 * @author zhenyu.nie created on 2014 2014/8/14 15:10
 */
public interface PrefixSearcher<T> extends List<T> {

    List<T> prefixSearch(String word);
}
