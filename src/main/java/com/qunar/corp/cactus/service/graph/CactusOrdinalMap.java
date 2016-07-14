package com.qunar.corp.cactus.service.graph;

import com.netflix.nfgraph.util.OrdinalMap;

/**
 * @author zhenyu.nie created on 2014 14-2-14 下午3:50
 * 这个类只是为了继承BiDirectionCollection接口，给OrdinalMap包装一下
 */
public class CactusOrdinalMap<T> extends OrdinalMap<T> implements BiDirectionCollection<T> {
}
