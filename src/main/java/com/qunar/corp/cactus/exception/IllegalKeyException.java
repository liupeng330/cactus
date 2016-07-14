/*
 * Copyright (c) 2013 Qunar.com. All Rights Reserved.
 */
package com.qunar.corp.cactus.exception;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2013 13-11-12 下午4:27
 */
public class IllegalKeyException extends IllegalArgumentException {

    private final Set<String> illegalKeys = Sets.newHashSet();

    public IllegalKeyException(Collection<String> illegalKeys) {
        super();
        this.illegalKeys.addAll(illegalKeys);
    }

    public IllegalKeyException(Collection<String> illegalKeys, String message) {
        super(message);
        this.illegalKeys.addAll(illegalKeys);
    }

    public IllegalKeyException(Collection<String> illegalKeys, String message, Throwable cause) {
        super(message, cause);
        this.illegalKeys.addAll(illegalKeys);
    }

    public IllegalKeyException(Collection<String> illegalKeys, Throwable cause) {
        super(cause);
        this.illegalKeys.addAll(illegalKeys);
    }

    public List<String> getIllegalKeys() {
        return ImmutableList.copyOf(illegalKeys);
    }
}
