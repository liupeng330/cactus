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
 * @author zhenyu.nie created on 2013 13-12-2 下午12:24
 */
public class WrongMatcherException extends IllegalArgumentException {

    private final Set<String> wrongMatchers = Sets.newHashSet();

    public WrongMatcherException(Collection<String> wrongMatchers) {
        super();
        this.wrongMatchers.addAll(wrongMatchers);
    }

    public WrongMatcherException(Collection<String> wrongMatchers, String message) {
        super(message);
        this.wrongMatchers.addAll(wrongMatchers);
    }

    public WrongMatcherException(Collection<String> wrongMatchers, String message, Throwable cause) {
        super(message, cause);
        this.wrongMatchers.addAll(wrongMatchers);
    }

    public WrongMatcherException(Collection<String> wrongMatchers, Throwable cause) {
        super(cause);
        this.wrongMatchers.addAll(wrongMatchers);
    }

    public List<String> getWrongMatchers() {
        return ImmutableList.copyOf(wrongMatchers);
    }
}
