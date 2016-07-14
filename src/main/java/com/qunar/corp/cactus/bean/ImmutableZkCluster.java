package com.qunar.corp.cactus.bean;

/**
 * @author zhenyu.nie created on 2014 2014/7/31 15:23
 *
 * 因为ZkCluster从mybatis查出来，导致ZkCluster类不能设置为不可变的，所以想要不可变只能新写一个不可变的ZKCluster类
 */
public class ImmutableZkCluster extends ZKCluster {

    public ImmutableZkCluster(ZKCluster zkCluster) {
        super(zkCluster);
    }

    @Override
    public void setId(long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAddress(String address) {
        throw new UnsupportedOperationException();
    }
}
