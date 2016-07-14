package com.qunar.corp.cactus.service.governance.router;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.Router;
import com.alibaba.dubbo.rpc.cluster.router.condition.ConditionRouter;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.util.Pair;
import com.qunar.corp.cactus.util.UrlHelper;

import java.util.*;

import static com.alibaba.dubbo.common.Constants.CATEGORY_KEY;
import static com.alibaba.dubbo.common.Constants.ROUTERS_CATEGORY;

/**
 * @author zhenyu.nie created on 2013 13-11-29 下午4:58
 */
public class RouterHelper {

    public static String toRouteRule(Map<String, String> params) {
        String consumerRouteRule = toConsumerRouteRule(params);
        String providerRouteRule = toProviderRouteRule(params);
        return consumerRouteRule + " => " + providerRouteRule;
    }

    public static Predicate<URL> isWhiteList() {
        return IS_WHITE_LIST_FUNC;
    }

    private static final Predicate<URL> IS_WHITE_LIST_FUNC = new Predicate<URL>() {
        @Override
        public boolean apply(URL input) {
            return isWhiteList(getRouteParams(input));
        }
    };

    // 这里的白名单指provider方面参数为空，consumer方面match参数为空的情形
    public static boolean isWhiteList(Map<String, String> params) {
        return Maps.filterKeys(params, Predicates.in(RouterConstants.PROVIDER_KEYS)).isEmpty()
                && !Maps.filterKeys(params, Predicates.in(RouterConstants.CONSUMER_KEYS)).isEmpty()
                && Maps.filterKeys(params, Predicates.in(RouterConstants.CONSUMER_MATCH_KEYS)).isEmpty();
    }

    public static Map<String, String> getRouteParams(URL url) {
        Map<String, String> paramsMap = Maps.newHashMap();
        if (url == null) {
            return paramsMap;
        }

        Pair<Map<String, CactusMatchPair>, Map<String, CactusMatchPair>> whenThenMatchPair = getWhenThenMatchPair(url);
        paramsMap.putAll(getParamsMap(whenThenMatchPair.getLeft(), true));
        paramsMap.putAll(getParamsMap(whenThenMatchPair.getRight(), false));
        paramsMap.putAll(Maps.filterKeys(url.getParameters(), Predicates.in(RouterConstants.OTHER_KEYS)));
        return Maps.newHashMap(paramsMap);
    }

    private static Pair<Map<String, CactusMatchPair>, Map<String, CactusMatchPair>> getWhenThenMatchPair(URL url) {
        String rule = url.getParameterAndDecoded(Constants.RULE_KEY);
        if (rule == null || rule.trim().length() == 0) {
            throw new IllegalArgumentException("Illegal route rule!");
        }
        rule = rule.replace("consumer.", "").replace("provider.", "");
        int i = rule.indexOf("=>");
        String whenRule = i < 0 ? null : rule.substring(0, i).trim();
        String thenRule = i < 0 ? rule.trim() : rule.substring(i + 2).trim();
        Map<String, CactusMatchPair> when = StringUtils.isBlank(whenRule) ||
                "true".equals(whenRule) ? new HashMap<String, CactusMatchPair>() : parseRule(whenRule);
        Map<String, CactusMatchPair> then = StringUtils.isBlank(thenRule) ||
                "false".equals(thenRule) ? new HashMap<String, CactusMatchPair>() : parseRule(thenRule);
        return Pair.makePair(when, then);
    }

    public static boolean isRouterMatch(URL router, String key, String value) {
        Pair<Map<String, CactusMatchPair>, Map<String, CactusMatchPair>> whenThenMatchPair = getWhenThenMatchPair(router);
        if (RouterConstants.PROVIDER_CONDITION_KEYS.contains(key)) {
            key = key.replace("provider.", "");
            CactusMatchPair matchPair = whenThenMatchPair.getRight().get(key);
            return isParamMatch(matchPair, value);
        } else if (RouterConstants.CONSUMER_CONDITION_KEYS.contains(key)) {
            key = key.replace("consumer.", "");
            CactusMatchPair matchPair = whenThenMatchPair.getLeft().get(key);
            return isParamMatch(matchPair, value);
        } else {
            throw new IllegalArgumentException("illegal key: " + key);
        }
    }

    private static boolean isParamMatch(CactusMatchPair matchPair, String value) {
        if (matchPair == null) {
            return false;
        }

        for (String match : matchPair.matches) {
            if (UrlUtils.isMatchGlobPattern(match, value)) {
                return true;
            }
        }

        for (String mismatch : matchPair.mismatches) {
            if (UrlUtils.isMatchGlobPattern(mismatch, value)) {
                return true;
            }
        }

        return false;
    }

    private static Map<String, String> getParamsMap(Map<String, CactusMatchPair> when, boolean isWhen) {
        Map<String, String> whenParamsMap = Maps.newHashMap();
        String prefix;
        if (isWhen) {
            prefix = "consumer.";
        } else {
            prefix = "provider.";
        }
        for (Map.Entry<String, CactusMatchPair> entry : when.entrySet()) {
            if (entry.getValue() != null) {
                CactusMatchPair matchPair = entry.getValue();
                String key = prefix + entry.getKey();
                if (!matchPair.matches.isEmpty()) {
                    String matchers = Joiner.on(",").join(matchPair.matches);
                    whenParamsMap.put(RouterParam.MATCH_URL_KEY_TO_KEY_MAP.get(key), matchers);
                }
                if (!matchPair.mismatches.isEmpty()) {
                    String mismatchers = Joiner.on(",").join(matchPair.mismatches);
                    whenParamsMap.put(RouterParam.MISMATCH_URL_KEY_TO_KEY_MAP.get(key), mismatchers);
                }
            }
        }
        return whenParamsMap;
    }

    private static final Predicate<Map.Entry<String, String>> IS_PROVIDER_ROUTE_ENTRY =
            new Predicate<Map.Entry<String, String>>() {
                @Override
                public boolean apply(Map.Entry<String, String> input) {
                    return input.getKey().contains("provider");
                }
            };

    public static String toConsumerRouteRule(Map<String, String> params) {
        return toPartialRouterRule(Maps.filterEntries(params, Predicates.not(IS_PROVIDER_ROUTE_ENTRY)));
    }

    public static String toProviderRouteRule(Map<String, String> params) {
        return toPartialRouterRule(Maps.filterEntries(params, IS_PROVIDER_ROUTE_ENTRY));
    }

    public static String toPartialRouterRule(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        int count = 0;

        List<ParamItem> paramItems = Lists.newArrayListWithCapacity(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String cactusKey = entry.getKey();
            String dubboKey = RouterConstants.KEY_TO_URL_KEY_MAP.get(cactusKey);
            paramItems.add(new ParamItem(cactusKey, dubboKey, entry.getValue()));
        }

        Collections.sort(paramItems, new Comparator<ParamItem>() {
            @Override
            public int compare(ParamItem o1, ParamItem o2) {
                return o1.dubboKey.compareTo(o2.dubboKey);
            }
        });

        for (ParamItem paramItem : paramItems) {
            ++count;
            sb.append(paramItem.dubboKey);
            if (RouterConstants.MISMATCH_KEYS.contains(paramItem.cactusKey)) {
                sb.append("!=");
            } else {
                sb.append("=");
            }
            sb.append(paramItem.value);
            if (count != params.size()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }

    private static class StringUtils {

        public static boolean isBlank(String str) {
            return str == null || str.length() == 0;
        }

        public static String[] split(String str, char ch) {
            List<String> list = new ArrayList<String>();

            int startIndex = 0;
            for (int i = startIndex; i < str.length(); i++) {
                if (ch == str.charAt(i)) {
                    list.add(str.substring(startIndex, i));
                    startIndex = i + 1;
                }
            }
            list.add(str.substring(startIndex));
            return list.toArray(EMPTY_STRING_ARRAY);
        }

        public static final String[] EMPTY_STRING_ARRAY = new String[0];
    }

    static class ParamItem {
        final String cactusKey;
        final String dubboKey;
        final String value;

        ParamItem(String cactusKey, String dubboKey, String value) {
            this.cactusKey = cactusKey;
            this.dubboKey = dubboKey;
            this.value = value;
        }
    }

    // mainly equal to parseRule in ConditionRouter
    public static Map<String, CactusMatchPair> parseRule(String rule) {
        try {
            Map<String, CactusMatchPair> condition = new HashMap<String, CactusMatchPair>();
            if (StringUtils.isBlank(rule)) {
                return condition;
            }

            String[] params = StringUtils.split(rule, '&');
            for (String param : params) {
                parseAndAddParam(param, condition);
            }

            return condition;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("illegal rule: " + rule, e);
        }
    }

    private static final String EQUAL = "=";
    private static final String NOT_EQUAL = "!=";

    private static void parseAndAddParam(String param, Map<String, CactusMatchPair> condition) {
        if (StringUtils.isBlank(param)) {
            throw new IllegalArgumentException();
        }

        String separator = getSeparator(param);
        int index = param.indexOf(separator);
        String key = param.substring(0, index);
        String value = param.substring(index + separator.length());

        if (StringUtils.isBlank(key) || StringUtils.isBlank(value) || key.contains(EQUAL) || value.contains(EQUAL)) {
            throw new IllegalArgumentException();
        }

        getContentHolder(key, separator, condition).addAll(parseValue(value));
    }

    private static String getSeparator(String param) {
        int index = param.indexOf(NOT_EQUAL);
        if (index >= 0) {
            return NOT_EQUAL;
        }

        index = param.indexOf(EQUAL);
        if (index >= 0) {
            return EQUAL;
        }

        throw new IllegalArgumentException();
    }

    private static Set<String> getContentHolder(String key, String separator, Map<String, CactusMatchPair> condition) {
        CactusMatchPair pair = condition.get(key);
        if (pair == null) {
            pair = new CactusMatchPair();
            condition.put(key, pair);
        }

        if (EQUAL.equals(separator)) {
            return pair.matches;
        } else {
            return pair.mismatches;
        }
    }

    private static Set<String> parseValue(String value) {
        String[] strs = StringUtils.split(value, ',');

        Set<String> values = new HashSet<String>(strs.length);
        for (String str : strs) {
            if (StringUtils.isBlank(str)) {
                throw new IllegalArgumentException();
            }
            values.add(str);
        }
        return values;
    }

    public static Map<URL, Map<Invocation, List<Invoker<Object>>>> route(
            Map<URL, Map<Invocation, List<Invoker<Object>>>> origin, List<? extends Router> routers) {
        Map<URL, Map<Invocation, List<Invoker<Object>>>> result = Maps.newHashMapWithExpectedSize(origin.size());
        for (Map.Entry<URL, Map<Invocation, List<Invoker<Object>>>> entry : origin.entrySet()) {
            result.put(entry.getKey(), route(entry.getKey(), entry.getValue(), routers));
        }
        return result;
    }

    static Map<Invocation, List<Invoker<Object>>> route(
            URL consumer, Map<Invocation, List<Invoker<Object>>> methodProviderMapping, List<? extends Router> routers) {
        if (consumer == null || methodProviderMapping == null || methodProviderMapping.isEmpty()) {
            return Maps.newHashMap();
        }
        if (routers == null) {
            routers = Lists.newArrayList();
        }
        URL tempForMatch = consumer.addParameter(CATEGORY_KEY, ROUTERS_CATEGORY);
        Map<Invocation, List<Invoker<Object>>> result = Maps.newHashMapWithExpectedSize(methodProviderMapping.size());
        for (Map.Entry<Invocation, List<Invoker<Object>>> methodAndProviders : methodProviderMapping.entrySet()) {
            Invocation method = methodAndProviders.getKey();
            List<Invoker<Object>> resultProviders = methodAndProviders.getValue();
            for (Router router : routers) {
                if (UrlHelper.matchSubscriber(tempForMatch).apply(router.getUrl())) {
                    resultProviders = router.route(resultProviders, consumer, method);
                }
            }
            result.put(method, resultProviders);
        }
        return result;
    }

    public static Map<URL, Map<Invocation, List<Invoker<Object>>>> route(List<URL> consumers,
            List<URL> providers, List<GovernanceData> routers) {
        List<ConditionRouter> conditionRouters = FluentIterable.from(routers).transform(UrlHelper.toFullUrl())
                .transform(toRouter()).toList();
        return route(initRouteDatas(consumers, providers), conditionRouters);
    }

    // consumer -> method -> provider
    public static Map<URL, Map<Invocation, List<Invoker<Object>>>> initRouteDatas(List<URL> consumers, List<URL> providers) {
        if (consumers.isEmpty() || providers.isEmpty()) {
            return Maps.newHashMap();
        }
        Set<Invocation> methods = FluentIterable
                .from(UrlHelper.getMethods(providers.get(0)))
                .transform(toInvocation())
                .toSet();
        if (methods.isEmpty()) {
            return Maps.newHashMap();
        }
        List<Invoker<Object>> invokers = FluentIterable.from(providers).transform(toInvoker()).toList();
        Map<URL, Map<Invocation, List<Invoker<Object>>>> consumerAndMethodAndInvokers = Maps.newHashMapWithExpectedSize(consumers.size());
        for (URL consumer : consumers) {
            Map<Invocation, List<Invoker<Object>>> methodAndInvokers = Maps.newHashMapWithExpectedSize(methods.size());
            consumerAndMethodAndInvokers.put(consumer, methodAndInvokers);
            for (Invocation method : methods) {
                methodAndInvokers.put(method, invokers);
            }
        }
        return consumerAndMethodAndInvokers;
    }

    public static Function<String, Invocation> toInvocation() {
        return new Function<String, Invocation>() {
            @Override
            public Invocation apply(String input) {
                return new CactusMockInvocation(input);
            }
        };
    }

    public static Function<URL, Invoker<Object>> toInvoker() {
        return new Function<URL, Invoker<Object>>() {
            @Override
            public Invoker<Object> apply(URL input) {
                return new CactusMockInvoker<Object>(input);
            }
        };
    }

    public static Function<URL, ConditionRouter> toRouter() {
        return new Function<URL, ConditionRouter>() {
            @Override
            public ConditionRouter apply(URL input) {
                return new ConditionRouter(input);
            }
        };
    }

}
