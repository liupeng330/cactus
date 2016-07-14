package com.qunar.corp.cactus.checker.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.UrlUtils;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.cluster.router.condition.ConditionRouter;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.checker.RouterChecker;
import com.qunar.corp.cactus.checker.ValueChecker;
import com.qunar.corp.cactus.exception.*;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.governance.router.CactusMatchPair;
import com.qunar.corp.cactus.service.governance.router.RouterHelper;
import com.qunar.corp.cactus.service.governance.router.RouterParam;
import com.qunar.corp.cactus.service.providerlevel.ConsumerService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.DelayedString;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.alibaba.dubbo.common.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.service.governance.router.RouterConstants.*;
import static com.qunar.corp.cactus.service.governance.router.RouterHelper.*;
import static com.qunar.corp.cactus.util.ConstantHelper.NEW_LINE;

/**
 * @author zhenyu.nie created on 2013 13-12-6 下午5:56
 */
@Service
public class RouterCheckerImpl implements RouterChecker {

    @Resource
    private ProviderService providerService;

    @Resource
    private ConsumerService consumerService;

    @Resource
    private IQueryGovernanceService queryService;

    @Resource(name="routerFactory")
    private GovernanceFactory routerFactory;

    @SuppressWarnings("all")
    public void checkValidRouteParams(Map<String, String> params) {
        Preconditions.checkArgument(params != null, "params can not be null");
        Map<String, String> illegalParams = Maps.newHashMapWithExpectedSize(params.size());
        for (Map.Entry<String, Predicate<String>> keyAndChecker : RouterParam.KEY_CHECKER_MAP.entrySet()) {
            String key = keyAndChecker.getKey();
            String value = params.get(key);
            Predicate<String> checker = Predicates.and(keyAndChecker.getValue(), ValueChecker.HAS_NO_ILLEGAL_CHAR);
            if (!checker.apply(value)) {
                illegalParams.put(key, nullToEmpty(value));
            }
        }
        if (!illegalParams.isEmpty()) {
            throw new IllegalParamException(illegalParams, "illegal params are (" + illegalParams + ")");
        }
    }

    @SuppressWarnings("all")
    public void checkIllegalCondition(ServiceSign sign, Map<String, String> params) {
        Preconditions.checkArgument(sign != null, "service sign can not be null");
        Preconditions.checkArgument(params != null, "params can not be null");
        checkNotEmptyCondition(params);
        checkMatchExist(sign, params);
        checkNotForceNoMatch(sign, params);
    }

    public void checkIllegalCondition(GovernanceData data, List<GovernanceData> relativeDatas) {
        ServiceSign sign = data.getServiceSign();
        Map<String, String> params = getRouteParams(data.getUrl());
        checkIllegalCondition(sign, params, relativeDatas);
    }

    @SuppressWarnings("all")
    private void checkIllegalCondition(ServiceSign sign, Map<String, String> params, List<GovernanceData> relativeDatas) {
        Preconditions.checkArgument(sign != null, "service sign can not be null");
        Preconditions.checkArgument(params != null, "params can not be null");
        checkNotEmptyCondition(params);
        checkMatchExist(sign, params);
        checkNotForceNoMatch(sign, params, FluentIterable.from(relativeDatas));
    }

    // 用来判断条件中的每一个参数都有match的对象，以及某一方面条件组合后能有match的对象
    private void checkMatchExist(ServiceSign sign, Map<String, String> params) {
        checkMethodMatch(sign, params);
        checkProviderMatch(sign, params);
        checkConsumerMatch(sign, params);
    }

    @SuppressWarnings("unchecked")
    private void checkMethodMatch(ServiceSign sign, Map<String, String> params) {
        Map<String, String> methodParams = Maps.newHashMap(Maps.filterKeys(params, Predicates.in(METHOD_KEYS)));
        if (!methodParams.isEmpty()) {
            List<String> methods = providerService.getMethods(sign.getGroup(), sign.serviceInterface,
                    ProviderPredicates.serviceKeyEqual(sign), UrlHelper.zkIdEqual(sign.zkId));
            checkNotEmptyForNoMatch(sign, methods, METHOD_KEY);
            matchCheck(methods, methodParams, KEY_PARAM_GETTER_MAP_FOR_STRING, false);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkProviderMatch(ServiceSign sign, Map<String, String> params) {
        Map<String, String> providerParams = Maps.filterKeys(params, Predicates.in(PROVIDER_KEYS));
        if (!providerParams.isEmpty()) {
            List<URL> providers = providerService.getItems(sign.getGroup(), sign.getServiceInterface(),
                    ProviderPredicates.serviceKeyEqual(sign), UrlHelper.zkIdEqual(sign.zkId)).toList();
            checkNotEmptyForNoMatch(sign, providers, PROVIDER);
            matchCheck(providers, providerParams, KEY_PARAM_GETTER_MAP_FOR_URL, false);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkConsumerMatch(ServiceSign sign, Map<String, String> params) {
        Map<String, String> consumerParamsExcludeMethod = Maps.filterKeys(params, Predicates.in(CONSUMER_KEYS));
        if (!consumerParamsExcludeMethod.isEmpty()) {
            List<URL> consumers = consumerService.getItems(sign.getGroup(), sign.serviceInterface,
                    UrlHelper.matchServiceKey(sign), UrlHelper.zkIdEqual(sign.zkId)).toList();
            checkNotEmptyForNoMatch(sign, consumers, CONSUMER);
            matchCheck(consumers, consumerParamsExcludeMethod, KEY_PARAM_GETTER_MAP_FOR_URL, isWhiteList(params));
        }
    }

    private <T> void checkNotEmptyForNoMatch(ServiceSign sign, List<T> items, String itemName) {
        if (items.isEmpty()) {
            throw new NoMatchedException("no " + itemName + " for group " + sign.getGroup() + " and service key ("
                    + sign.getServiceKey() + ")");
        }
    }

    private void checkNotEmptyCondition(Map<String, String> params) {
        if (params == null
                || FluentIterable.from(params.keySet()).filter(Predicates.in(MATCH_AND_MISMATCH_KEYS)).isEmpty()) {
            throw new EmptyParamException("empty route condition");
        }
    }

    private <T> void matchCheck(List<T> items, Map<String, String> params,
            Map<String, Function<T, String>> keyParamGetter, boolean whiteList) {
        if (params.isEmpty()) {
            return;
        }

        String rule = toPartialRouterRule(params).replace("consumer.", "").replace("provider.", "");
        Map<String, CactusMatchPair> keyAndMatchPairs = isNullOrEmpty(rule) ? new HashMap<String, CactusMatchPair>()
                : parseRule(rule);
        Map<String, Set<String>> keyAndMatchers = Maps.newHashMap(Maps.transformValues(keyAndMatchPairs, toMatchers()));

        // 对provider的host为$host的特殊处理
        boolean selfHost = dealSelfHost(params, keyAndMatchers);

        boolean itemMatch = false;
        for (T item : items) {
            boolean thisItemMatch = true;
            boolean eachMatch = true;
            for (Map.Entry<String, CactusMatchPair> matchPairEntry : keyAndMatchPairs.entrySet()) {
                String key = matchPairEntry.getKey();
                String param = keyParamGetter.get(key).apply(item);
                if (!whiteList && !itemMatch && !(selfHost && key.equals("host"))
                        && !matchPairEntry.getValue().isMatch(param, null)) {
                    thisItemMatch = false;
                }
                Set<String> matchers = keyAndMatchers.get(key);
                matchers.removeAll(FluentIterable.from(matchers).filter(matchTo(param)).toList());
                if (!matchers.isEmpty()) {
                    eachMatch = false;
                }
            }
            if (thisItemMatch) {
                itemMatch = true;
            }
            if (itemMatch && eachMatch) {
                return;
            }
        }
        ImmutableList<Set<String>> wrongParamSets = FluentIterable.from(keyAndMatchers.values()).filter(notEmptySet())
                .toList();
        List<String> wrongParams = Lists.newArrayList();
        for (Set<String> wrongParamSet : wrongParamSets) {
            wrongParams.addAll(wrongParamSet);
        }
        if (!wrongParams.isEmpty()) {
            throw new WrongMatcherException(wrongParams, "has wrong matchers (" + wrongParams + ")");
        }
        if (!itemMatch) {
            throw new NoMatchedException("can not match with params (" + params + ")");
        }
    }

    private boolean dealSelfHost(Map<String, String> params, Map<String, Set<String>> keyAndMatchers) {
        if (params.containsKey(PROVIDER_MATCH_HOST) || params.containsKey(PROVIDER_MISMATCH_HOST)) {
            Set<String> hostMatchers = keyAndMatchers.get("host");
            if (hostMatchers != null && hostMatchers.contains("$host")) {
                hostMatchers.remove("$host");
                return true;
            }
        }
        return false;
    }

    private void checkNotForceNoMatch(ServiceSign sign, Map<String, String> params) {
        FluentIterable<GovernanceData> relativeDatas = queryService.getGovernanceDatas(GovernanceQuery.createBuilder()
                .status(Status.ENABLE).group(sign.group).serviceName(sign.serviceInterface)
                .pathType(PathType.ROUTER).zkId(sign.zkId).dataType(DataType.ZKDATA).build());
        checkNotForceNoMatch(sign, params, relativeDatas);
    }

    @SuppressWarnings("unchecked")
    private void checkNotForceNoMatch(ServiceSign sign, Map<String, String> params,
            FluentIterable<GovernanceData> relativeDatas) {
        if (params.get(ENABLED_KEY) != null && !Boolean.parseBoolean(params.get(ENABLED_KEY))) {
            return;
        }
        checkNotMultiWhiteList(sign, params, relativeDatas.transform(UrlHelper.toFullUrl()));
        if (isWhiteOrBlackList(params)) {
            return;
        }

        List<URL> consumers = consumerService.getItems(sign.getGroup(), sign.serviceInterface,
                UrlHelper.zkIdEqual(sign.zkId), UrlHelper.matchServiceKey(sign)).toList();
        List<URL> providers = providerService.getZkIdAndServiceKeyMatchedEnabledProviders(sign).toList();

        Map<URL, Map<Invocation, List<Invoker<Object>>>> consumerAndMethodAndInvokers = RouterHelper.initRouteDatas(
                consumers, providers);
        if (consumerAndMethodAndInvokers.isEmpty()) {
            return;
        }

        List<ConditionRouter> routers = relativeDatas.transform(UrlHelper.toFullUrl()).transform(toRouter()).toList();
        // 避免filter多次
        Map<URL, Map<Invocation, List<Invoker<Object>>>> consumerPassedOriginRouters = Maps.newHashMap(Maps
                .filterEntries(route(consumerAndMethodAndInvokers, routers), CAN_PASS_ROUTERS));

        URL newRouter = UrlHelper.toFullUrl().apply(routerFactory.create(sign, params));
        Map<URL, Map<Invocation, List<Invoker<Object>>>> resultConsumers = Maps.filterEntries(
                route(consumerPassedOriginRouters, Arrays.asList(new ConditionRouter(newRouter))), CAN_PASS_ROUTERS);

        Set<URL> difference = Sets.difference(consumerPassedOriginRouters.keySet(), resultConsumers.keySet());
        if (!difference.isEmpty()) {
            throw new ForceToUnMatchException(difference, "some url force to unmatched:" + NEW_LINE
                    + DelayedString.toString(difference));
        }
    }

    private static final Predicate<Map.Entry<URL, Map<Invocation, List<Invoker<Object>>>>> CAN_PASS_ROUTERS = new Predicate<Map.Entry<URL, Map<Invocation, List<Invoker<Object>>>>>() {
        public boolean apply(Map.Entry<URL, Map<Invocation, List<Invoker<Object>>>> input) {
            for (Map.Entry<Invocation, List<Invoker<Object>>> methodAndProviders : input.getValue().entrySet()) {
                if (methodAndProviders.getValue().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    };

    private void checkNotMultiWhiteList(ServiceSign sign, Map<String, String> params,
            FluentIterable<URL> enabledRouterUrls) {
        if (isWhiteList(params)) {
            List<URL> existWhiteList = enabledRouterUrls.filter(isWhiteList())
                    .filter(ProviderPredicates.serviceKeyEqual(sign)).toList();
            if (!existWhiteList.isEmpty()) {
                throw new MultiWhiteListException("exist white list: " + DelayedString.toString(existWhiteList)
                        + NEW_LINE + "when meet params " + params);
            }
        }
    }

    private boolean isWhiteOrBlackList(Map<String, String> params) {
        return FluentIterable.from(params.keySet()).filter(Predicates.in(PROVIDER_KEYS)).isEmpty();
    }

    private Predicate<Set<String>> notEmptySet() {
        return new Predicate<Set<String>>() {
            public boolean apply(Set<String> input) {
                return input.size() > 0;
            }
        };
    }

    private Predicate<String> matchTo(final String param) {
        return new Predicate<String>() {
            public boolean apply(String matcher) {
                return UrlUtils.isMatchGlobPattern(matcher, param);
            }
        };
    }

    private Function<CactusMatchPair, Set<String>> toMatchers() {
        return new Function<CactusMatchPair, Set<String>>() {
            public Set<String> apply(CactusMatchPair input) {
                return Sets.newHashSet(Sets.union(input.matches, input.mismatches));
            }
        };
    }

    private static final Map<String, Function<URL, String>> KEY_PARAM_GETTER_MAP_FOR_URL = Maps.newHashMap();

    private static final Function<URL, String> IP_GETTER = new Function<URL, String>() {
        public String apply(URL input) {
            return input.getIp();
        }
    };

    private static final Function<URL, String> PORT_GETTER = new Function<URL, String>() {
        public String apply(URL input) {
            if (input.getPort() > 0) {
                return String.valueOf(input.getPort());
            } else {
                return "";
            }
        }
    };

    private static final Function<URL, String> APP_GETTER = new Function<URL, String>() {
        public String apply(URL input) {
            return input.getParameter(APPLICATION_KEY);
        }
    };

    private static final Map<String, Function<String, String>> KEY_PARAM_GETTER_MAP_FOR_STRING = Maps.newHashMap();

    private static final Function<String, String> METHOD_GETTER = new Function<String, String>() {
        public String apply(String input) {
            return input;
        }
    };

    static {
        KEY_PARAM_GETTER_MAP_FOR_STRING.put(METHOD_KEY, METHOD_GETTER);
        KEY_PARAM_GETTER_MAP_FOR_URL.put(HOST, IP_GETTER);
        KEY_PARAM_GETTER_MAP_FOR_URL.put(APPLICATION_KEY, APP_GETTER);
        KEY_PARAM_GETTER_MAP_FOR_URL.put(PORT, PORT_GETTER);
    }
}
