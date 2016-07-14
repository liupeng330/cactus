package com.qunar.corp.cactus.service.providerlevel.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.cluster.Configurator;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.service.providerlevel.AbstractProviderLevelService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.util.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.alibaba.dubbo.common.Constants.*;

/**
 * Date: 13-10-30 Time: 上午11:06
 *
 * @author: xiao.liang
 * @description:
 */
@Service
public class ProviderServiceImpl extends AbstractProviderLevelService implements ProviderService {

    @Resource
    private IQueryGovernanceService queryGovernanceService;

    public ProviderServiceImpl() {
        super(DEFAULT_PROTOCOL, PROVIDERS_CATEGORY);
    }

    @Override
    public FluentIterable<URL> getConfiguredProviders(String group, String serviceInterface,
            Predicate<URL>... predicates) {
        return FluentIterable.from(getConfiguredProviders(group, serviceInterface,
                getItems(group, serviceInterface, predicates)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FluentIterable<URL> getZkIdAndServiceKeyMatchedEnabledProviders(ServiceSign sign) {
        GovernanceQuery governanceQuery = GovernanceQuery.createBuilder().zkId(sign.zkId).group(sign.group)
                .serviceName(sign.getServiceInterface()).pathType(PathType.CONFIG)
                .notDataType(DataType.USERDATA).serviceGroup(sign.serviceGroup).version(sign.version)
                .status(Status.ENABLE).build();
        FluentIterable<URL> configuratorUrls = queryGovernanceService
                .getServiceKeyMatchedGovernanceDatas(governanceQuery).transform(UrlHelper.toFullUrl())
                .filter(UrlHelper.hasKeyOf(DISABLED_KEY));
        return getItems(sign.getGroup(), sign.getServiceInterface(), UrlHelper.zkIdEqual(sign.zkId),
                ProviderPredicates.serviceKeyEqual(sign)).transform(configuredBy(configuratorUrls)).filter(
                IS_ENABLED_CONFIGURED_PROVIDER_FUNC);
    }

    private Function<URL, URL> configuredBy(final Iterable<URL> configuratorUrls) {
        return new Function<URL, URL>() {
            @Override
            public URL apply(URL input) {
                List<Configurator> configurators = toConfigurators(configuratorUrls);
                for (Configurator configurator : configurators) {
                    input = configurator.configure(input);
                }
                return input;
            }
        };
    }

    private boolean isEnabledConfiguredProvider(URL configuredProvider) {
        return configuredProvider.hasParameter(Constants.DISABLED_KEY) ? !configuredProvider.getParameter(
                Constants.DISABLED_KEY, false) : configuredProvider.getParameter(Constants.ENABLED_KEY, true);
    }

    private final Predicate<URL> IS_ENABLED_CONFIGURED_PROVIDER_FUNC = new Predicate<URL>() {
        @Override
        public boolean apply(URL provider) {
            return isEnabledConfiguredProvider(provider);
        }
    };

    @Override
    public List<String> getMethods(String group, String serviceInterface,
                                   Predicate<URL>... urlPredicates) {
        Optional<URL> provider = getItems(group, serviceInterface, urlPredicates).first();
        return provider.isPresent() ? UrlHelper.getMethods(provider.get()) : ImmutableList.<String> of();
    }

    @Override
    @SuppressWarnings("all")
    public URL getConfiguredProvider(URL url) {
        ServiceSign sign = ServiceSign.makeServiceSign(url);
        FluentIterable<URL> configurators = queryGovernanceService
                .getGovernanceDatas(
                        GovernanceQuery.createBuilder().zkId(sign.zkId).group(sign.group)
                                .serviceName(sign.serviceInterface).pathType(PathType.CONFIG).status(Status.ENABLE)
                                .notDataType(DataType.USERDATA).build())
                .transform(UrlHelper.toFullUrl())
                .filter(UrlHelper.matchSubscriber(url.addParameter(Constants.CATEGORY_KEY,
                        Constants.CONFIGURATORS_CATEGORY)));
        return getConfiguredProvider(url, configurators);
    }

    private URL getConfiguredProvider(URL url, Iterable<URL> configurators) {
        return configuredBy(configurators).apply(url);
    }

    private List<URL> getConfiguredProviders(String group, String serviceInterface, Iterable<URL> providers) {
        List<URL> results = Lists.newArrayList();
        List<URL> configurators = queryGovernanceService
                .getGovernanceDatas(
                        GovernanceQuery.createBuilder().group(group).serviceName(serviceInterface)
                                .pathType(PathType.CONFIG).status(Status.ENABLE).notDataType(DataType.USERDATA).build())
                .transform(UrlHelper.toFullUrl()).toList();
        for (URL provider : providers) {
            FluentIterable<URL> validConfigurators = FluentIterable.from(configurators)
                    .filter(UrlHelper.zkIdEqual(Long.parseLong(provider.getParameter(ConstantHelper.ZKID))))
                    .filter(UrlHelper.matchSubscriber(
                            provider.addParameter(Constants.CATEGORY_KEY, Constants.CONFIGURATORS_CATEGORY)));
            results.add(getConfiguredProvider(provider, validConfigurators));
        }
        return results;
    }

    private static final int SOME_OFFLINE = 0;
    private static final int ALL_OFFLINE = 1;
    private static final int ALL_ONLINE = 2;

    @Override
    public ProviderService distinctWith(Comparator<URL> comparator) {
        return new DistinctProviderService(this, comparator);
    }

    class DistinctProviderService implements ProviderService {

        private ProviderServiceImpl providerService;
        private final List<Comparator<URL>> comparators = Lists.newArrayList();

        DistinctProviderService(ProviderServiceImpl providerService, Comparator<URL> comparator) {
            this.providerService = providerService;
            this.comparators.add(comparator);
        }

        @Override
        public FluentIterable<URL> getItems(String group, Predicate<URL>... predicates) {
            List<URL> providers = providerService.getConfiguredProviders(group, null,
                    providerService.getItems(group, predicates));
            Ordering<URL> mergedComparator = Ordering.compound(comparators);
            Collections.sort(providers, mergedComparator);

            URL distinctProvider = null;
            boolean allOnline = true;
            boolean allOffline = true;
            List<URL> results = Lists.newArrayList();
            for (URL provider : providers) {
                if (mergedComparator.compare(distinctProvider, provider) == 0) {
                    if (provider.getParameter(DISABLED_KEY, false)) {
                        allOnline = false;
                    } else {
                        allOffline = false;
                    }
                } else {
                    if (distinctProvider != null) {
                        results.add(distinctProvider.addParameter(
                                ConstantHelper.CACTUS_ONLINE_STATUS, getStatus(allOnline, allOffline)));
                    }

                    distinctProvider = provider;
                    if (distinctProvider.getParameter(DISABLED_KEY, false)) {
                        allOnline = false;
                        allOffline = true;
                    } else {
                        allOnline = true;
                        allOffline = false;
                    }
                }
            }

            if (distinctProvider != null) {
                results.add(distinctProvider.addParameter(ConstantHelper.CACTUS_ONLINE_STATUS,
                        getStatus(allOnline, allOffline)));
            }
            return FluentIterable.from(results);
        }

        private String getStatus(boolean allOnline, boolean allOffline) {
            if (allOnline) {
                return String.valueOf(ALL_ONLINE);
            } else if (allOffline) {
                return String.valueOf(ALL_OFFLINE);
            } else {
                return String.valueOf(SOME_OFFLINE);
            }
        }

        @Override
        public ProviderService distinctWith(Comparator<URL> comparator) {
            return addComparator(comparator);
        }

        private ProviderService addComparator(Comparator<URL> comparator) {
            comparators.add(comparator);
            return this;
        }

        @Override
        public List<String> getMethods(String group, String serviceInterface, Predicate<URL>... urlPredicate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FluentIterable<URL> getZkIdAndServiceKeyMatchedEnabledProviders(ServiceSign serviceSign) {
            throw new UnsupportedOperationException();
        }

        @Override
        public URL getConfiguredProvider(URL url) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FluentIterable<URL> getItems(String group, String serviceInterface, Predicate<URL>... predicates) {
            throw new UnsupportedOperationException();
        }

        @Override
        public FluentIterable<URL> getConfiguredProviders(String group, String serviceInterface, Predicate<URL>... predicates) {
            throw new UnsupportedOperationException();
        }
    }
}
