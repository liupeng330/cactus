package com.qunar.corp.cactus.service.graph;

/**
 * @author zhenyu.nie created on 2014 14-2-14 下午3:48
 *
 * 设置这个接口的目的在于，构造一个NFGraph需要集合能够支持通过index获取对象和通过对象获取index的双向get
 * 而OrdinalMap虽然支持这两种操作，但是并不满足我们的需求，我们还需要进行搜索
 * 在不需要搜索的地方我们使用OrdinalMap来存储，在需要搜索的地方使用其它的数据结构来存储
 * 在和图相关的操作中，让它们都实现BiDirectionCollection接口，就可以进行统一处理了
 */
public interface BiDirectionCollection<T> {

    int get(T object);

    T get(int index);
}
