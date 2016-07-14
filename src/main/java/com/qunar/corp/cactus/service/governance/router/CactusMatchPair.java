package com.qunar.corp.cactus.service.governance.router;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.UrlUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2013 13-12-6 下午6:08
 * equal to MatchPair in dubbo ConditionRouter
 */
public class CactusMatchPair {

    public final Set<String> matches = new HashSet<String>();

    public final Set<String> mismatches = new HashSet<String>();

    public boolean isMatch(String value, URL param) {
        for (String mismatch : mismatches) {
            if (UrlUtils.isMatchGlobPattern(mismatch, value, param)) {
                return false;
            }
        }
        for (String match : matches) {
            if (UrlUtils.isMatchGlobPattern(match, value, param)) {
                return true;
            }
        }
        return matches.isEmpty();
    }
}
