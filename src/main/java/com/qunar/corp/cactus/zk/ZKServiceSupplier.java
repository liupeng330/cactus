package com.qunar.corp.cactus.zk;

import com.google.common.base.Supplier;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午7:02
 */
public class ZKServiceSupplier implements Supplier<ZKService> {

    private final String address;

    public ZKServiceSupplier(String address) {
        this.address = address;
    }

    @Override
    public ZKService get() {
        return new ZKServiceImpl(address);
    }
}
