package com.qunar.corp.cactus.checker;

import com.google.common.base.*;
import com.google.common.collect.Iterables;
import com.qunar.corp.cactus.bean.MockEnum;

import java.util.regex.Pattern;

/**
 * @author zhenyu.nie created on 2013 13-11-19 下午9:51
 */
public class ValueChecker {

    public static final Predicate<String> IS_POSITIVE_NUM = new Predicate<String>() {
        public boolean apply(String input) {
            int num;
            try {
                num = Integer.valueOf(input);
            } catch (Throwable e) {
                return false;
            }
            return num > 0;
        }
    };

    public final static Predicate<String> IS_VALID_MOCK = new Predicate<String>() {
        public boolean apply(String input) {
            return Objects.equal(MockEnum.FAILOVER.getText(), input)
                    || Objects.equal(MockEnum.SHIELD.getText(), input)
                    || Objects.equal(MockEnum.NONEMOCK.getText(), input);
        }
    };

    public static Predicate<String> IS_VALID_BOOLEAN = new Predicate<String>() {
        public boolean apply(String input) {
            return Objects.equal("true", input) ||
                    Objects.equal("false", input);
        }
    };

    public static final Pattern ILLEGAL_CHAR_PATTERN = Pattern.compile("[&!=>\\s]");

    public static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    public static final Predicate<String> HAS_ILLEGAL_CHAR = new Predicate<String>() {
        public boolean apply(String input) {
            return !Strings.isNullOrEmpty(input)
                    && Iterables.any(COMMA_SPLITTER.split(input),
                    new Predicate<String>() {
                        public boolean apply(String input) {
                            return ILLEGAL_CHAR_PATTERN.matcher(input).find();
                        }
                    });
        }
    };

    public static final Predicate<String> HAS_NO_ILLEGAL_CHAR = Predicates.not(HAS_ILLEGAL_CHAR);
}
