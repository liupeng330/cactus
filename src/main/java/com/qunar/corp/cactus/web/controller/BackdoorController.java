package com.qunar.corp.cactus.web.controller;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import com.qunar.corp.cactus.service.SpecialService;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.service.governance.IQueryGovernanceService;
import com.qunar.corp.cactus.service.governance.config.ProviderOnlineService;
import com.qunar.corp.cactus.service.graph.GraphService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.service.providerlevel.impl.ConfiguratorService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import com.qunar.corp.cactus.util.UrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alibaba.dubbo.common.Constants.DISABLED_KEY;
import static com.alibaba.dubbo.common.Constants.DUBBO_VERSION_KEY;

/**
 * @author zhenyu.nie created on 2014 2014/8/12 21:00
 */
@Controller
@RequestMapping("/backdoor")
public class BackdoorController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(BackdoorController.class);

    @Resource
    private GovernanceDataDao governanceDataDao;

    @Resource
    private ConfiguratorService configuratorService;

    @Resource
    private ProviderService providerService;

    @Resource
    private ProviderOnlineService onlineService;

    @Resource
    private ZKClusterService zkClusterService;

    @Resource
    private SpecialService specialService;

    @Resource
    private GraphService graphService;

    @Resource
    private IQueryGovernanceService queryService;

    interface DisableScanner {
        void scan(String group, String service, URL disableUrl);
        void close();
    }

    abstract class ScannerDecorator implements DisableScanner {

        DisableScanner delegate;

        ScannerDecorator(DisableScanner delegate) {
            this.delegate = delegate;
        }

        @Override
        public void scan(String group, String service, URL disableUrl) {
            delegate.scan(group, service, disableUrl);
        }

        @Override
        public void close() {
            delegate.close();
        }
    }

    class PredicatesScanner extends ScannerDecorator implements DisableScanner {

        Predicate<URL> predicate;

        PredicatesScanner(Predicate<URL> predicate, DisableScanner disableScanner) {
            super(disableScanner);
            this.predicate = predicate;
        }

        @Override
        public void scan(String group, String service, URL disableUrl) {
            if (predicate.apply(disableUrl)) {
                super.scan(group, service, disableUrl);
            }
        }
    }

    class PrintNormalScanner implements DisableScanner {

        PrintWriter pw;

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        public PrintNormalScanner(String name) throws FileNotFoundException {
            String date = dateFormat.format(new Date());
            String filePath = getCachePath() + name + "-" + date;
            logger.info("scan disable type: {}, file path: {}", name, filePath);
            pw = new PrintWriter(filePath);
        }

        @Override
        public void scan(String group, String service, URL url) {
            String address = url.getAddress();
            pw.print(url.getParameter(ConstantHelper.ZKID));
            pw.print("\t");
            pw.print(group);
            pw.print("\t");
            pw.print(service);
            pw.print("\t");
            pw.print(address);
            pw.print(":");
            pw.print(CommonCache.address2HostNameAndPort(address));
            pw.print("\t");
            pw.print(url);
            pw.println();
        }

        @Override
        public void close() {
            pw.close();
        }
    }

    class PrintScanner implements DisableScanner {

        PrintWriter pw;

        PrintScanner(String name) throws FileNotFoundException {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = dateFormat.format(new Date());
            String filePath = getCachePath() + name + "-" + date;
            logger.info("scan disable type: {}, file path: {}", name, filePath);
            pw = new PrintWriter(filePath);
        }

        @Override
        public void scan(String group, String service, URL disableUrl) {
            printDisableUrl(pw, group, service, disableUrl);
        }

        @Override
        public void close() {
            pw.close();
        }
    }

    class UnRegisterScanner extends ScannerDecorator implements DisableScanner {

        UnRegisterScanner(DisableScanner disableScanner) {
            super(disableScanner);
        }

        @Override
        public void scan(String group, String service, URL disableUrl) {
            super.scan(group, service, disableUrl);
            URL originUrl = getOriginUrl(disableUrl);
            GovernanceData data = new GovernanceData();
            data.setZkId(Long.valueOf(disableUrl.getParameter(ConstantHelper.ZKID)));
            data.setServiceName(service);
            data.setUrl(originUrl);
            RegisterAndUnRegisters registerAndUnRegisters = new RegisterAndUnRegisters();
            registerAndUnRegisters.addUnRegister(data);
            zkClusterService.writeDatas(registerAndUnRegisters);
        }
    }

    class UpdateGovernanceScanner extends ScannerDecorator implements DisableScanner {

        UpdateGovernanceScanner(DisableScanner disableScanner) {
            super(disableScanner);
        }

        @Override
        public void scan(String group, String service, URL disableUrl) {
            super.scan(group, service, disableUrl);
            disableUrl.addParameter(ConstantHelper.Q_REGISTRY_KEY, group)
                    .addParameter(ConstantHelper.CACTUS_USER_NAME, ConstantHelper.CACTUS_API)
                    .addParameter(ConstantHelper.CACTUS_USER_ID, UserContainer.getUserId());
            onlineService.providerOnline(disableUrl);
        }
    }

    @RequestMapping("/scanDisables/all")
    @JsonBody
    public synchronized Object scanDisablesAll() throws FileNotFoundException {
        DisableScanner scanner = new PrintScanner("scan-all-disable");
        return scan(scanner);
    }

    @RequestMapping("/scanContains")
    @JsonBody
    public synchronized Object scanContains(@RequestParam String contains) throws FileNotFoundException {
        logger.info("search contains [{}]", contains);
        DisableScanner scanner = new PrintNormalScanner("search-contains");
        return scan(scanner, new SearchContains(contains));
    }

    private interface Search {
        List<URL> search(String group, String service);
    }

    private class SearchContains implements Search {

        String contains;

        public SearchContains(String contains) {
            this.contains = contains;
        }

        @Override
        public List<URL> search(String group, String service) {
            ImmutableList<URL> providers = providerService.getItems(group, service).toList();
            List<URL> results = Lists.newArrayList();
            for (URL provider : providers) {
                if (provider.toString().contains(contains)) {
                    results.add(provider);
                }
            }
            return results;
        }
    }

    @RequestMapping("/scanDisables/new")
    @JsonBody
    public synchronized Object scanDisablesNew() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-new-disable");
        DisableScanner scanner = new PredicatesScanner(NEW_DISABLE_FUNC, printScanner);
        return scan(scanner);
    }

    @RequestMapping("/scanDisables/hasProvider/new")
    @JsonBody
    public synchronized Object scanDisablesHasProviderNew() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-has-provider-new-disable");
        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(HAS_PROVIDER_FUNC, NEW_DISABLE_FUNC), printScanner);
        return scan(scanner);
    }

    @RequestMapping("/scanDisables/noProvider/new")
    @JsonBody
    public synchronized Object scanDisablesNoProviderNew() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-no-provider-new-disable");
        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(Predicates.not(HAS_PROVIDER_FUNC), NEW_DISABLE_FUNC), printScanner);
        return scan(scanner);
    }

    @RequestMapping("/scanDisables/old")
    @JsonBody
    public synchronized Object scanDisablesOld() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-old-disable");
        DisableScanner scanner = new PredicatesScanner(Predicates.not(NEW_DISABLE_FUNC), printScanner);
        return scan(scanner);
    }

    @RequestMapping("/scanDisables/hasProvider/old")
    @JsonBody
    public synchronized Object scanDisablesHasProviderOld() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-has-provider-old-disable");
        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(HAS_PROVIDER_FUNC, Predicates.not(NEW_DISABLE_FUNC)), printScanner);
        return scan(scanner);
    }

    @RequestMapping("/scanDisables/noProvider/old")
    @JsonBody
    public synchronized Object scanDisablesNoProviderOld() throws FileNotFoundException {
        PrintScanner printScanner = new PrintScanner("scan-no-provider-old-disable");
        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(Predicates.not(HAS_PROVIDER_FUNC), Predicates.not(NEW_DISABLE_FUNC)), printScanner);
        return scan(scanner);
    }

//    @RequestMapping("/removeDisables/hasProvider/notEqualDb")
//    public synchronized Object removeHasProviderNotEqualDb() throws FileNotFoundException {
//        PrintScanner printScanner = new PrintScanner("scan-has-provider-not-equal-db");
//        UnRegisterScanner unRegisterScanner = new UnRegisterScanner(printScanner);
//        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(HAS_PROVIDER_FUNC, Predicates.not(EQUAL_DB_FUNC)), unRegisterScanner);
//        return scan(scanner);
//    }
//
//    @RequestMapping("/removeDisables/noProvider/notEqualDb")
//    public synchronized Object removeNoProviderNotEqualDb() throws FileNotFoundException {
//        PrintScanner printScanner = new PrintScanner("scan-no-provider-not-equal-db");
//        UnRegisterScanner unRegisterScanner = new UnRegisterScanner(printScanner);
//        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(Predicates.not(HAS_PROVIDER_FUNC), Predicates.not(EQUAL_DB_FUNC)), unRegisterScanner);
//        return scan(scanner);
//    }
//
//    @RequestMapping("/removeDisables/hasProvider/equalDb")
//    public synchronized Object removeHasProviderEqualDb() throws FileNotFoundException {
//        PrintScanner printScanner = new PrintScanner("scan-has-provider-equal-db");
//        UnRegisterScanner unRegisterScanner = new UnRegisterScanner(printScanner);
//        UpdateGovernanceScanner updateGovernanceScanner = new UpdateGovernanceScanner(unRegisterScanner);
//        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(HAS_PROVIDER_FUNC, EQUAL_DB_FUNC), updateGovernanceScanner);
//        return scan(scanner);
//    }
//
//    @RequestMapping("/removeDisables/noProvider/equalDb")
//    public synchronized Object removeNoProviderEqualDb() throws FileNotFoundException {
//        PrintScanner printScanner = new PrintScanner("scan-no-provider-equal-db");
//        UnRegisterScanner unRegisterScanner = new UnRegisterScanner(printScanner);
//        UpdateGovernanceScanner updateGovernanceScanner = new UpdateGovernanceScanner(unRegisterScanner);
//        DisableScanner scanner = new PredicatesScanner(Predicates.<URL>and(Predicates.not(HAS_PROVIDER_FUNC), EQUAL_DB_FUNC), updateGovernanceScanner);
//        return scan(scanner);
//    }

    private Object scan(DisableScanner scanner, Search searchContains) {
        try {
            Table<String, String, List<URL>> doubtItems = getDoubtItems(searchContains);
            for (String group : doubtItems.rowKeySet()) {
                Map<String, List<URL>> row = doubtItems.row(group);
                for (Map.Entry<String, List<URL>> entry : row.entrySet()) {
                    for (URL disableUrl : entry.getValue()) {
                        String service = entry.getKey();
                        scanner.scan(group, service, disableUrl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("scan error", e);
            return false;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private Object scan(DisableScanner scanner) {
        try {
            Table<String, String, List<URL>> doubtItems = getDoubtItems();
            for (String group : doubtItems.rowKeySet()) {
                Map<String, List<URL>> row = doubtItems.row(group);
                for (Map.Entry<String, List<URL>> entry : row.entrySet()) {
                    for (URL disableUrl : entry.getValue()) {
                        String service = entry.getKey();
                        scanner.scan(group, service, disableUrl);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("scan error", e);
            return false;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void printDisableUrl(PrintWriter pw, String group, String service, URL disableUrl) {
        String address = disableUrl.getAddress();
        pw.print(disableUrl.getParameter(ConstantHelper.ZKID));
        pw.print("\t");
        pw.print(group);
        pw.print("\t");
        pw.print(service);
        pw.print("\t");
        pw.print(address);
        pw.print(":");
        pw.print(CommonCache.address2HostNameAndPort(address));
        pw.print("\t");
        pw.print(COMMA_JOINNER.join(graphService.getOwners(PathUtil.getGroup(group))));
        pw.print("\t");
        if (disableUrl.getParameter(HAS_PROVIDER, false)) {
            pw.print("hasProvider");
            pw.print("\t");
            String dubboVersion = disableUrl.getParameter(DUBBO_VERSION_KEY);
            if (!Strings.isNullOrEmpty(dubboVersion)) {
                pw.print(dubboVersion);
            } else {
                pw.print("noVersion");
            }
        } else {
            pw.print("noProvider");
            pw.print("\t");
            pw.print("noVersion");
        }
        pw.print("\t");
        if (disableUrl.getParameter(EQUAL_DB, false)) {
            pw.print("equalDb");
        } else {
            pw.print("notEqualDb");
        }
        pw.print("\t");
        if (disableUrl.hasParameter(ConstantHelper.Q_REGISTRY_KEY)) {
            pw.print("new");
        } else {
            pw.print("old");
        }
        pw.print("\t");
        URL originUrl = getOriginUrl(disableUrl);
        pw.print(originUrl);
        pw.println();
    }

    private URL getOriginUrl(URL disableUrl) {
        return disableUrl
                .removeParameter(HAS_PROVIDER)
                .removeParameter(DUBBO_VERSION_KEY)
                .removeParameter(EQUAL_DB)
                .removeParameter(ConstantHelper.FAKE_Q_REGISTRY_KEY)
                .removeParameter(ConstantHelper.ZKID);
    }

    private String getCachePath() {
        return System.getProperty("catalina.base") + File.separator + "cache" + File.separator;
    }

    private static Joiner COMMA_JOINNER = Joiner.on(',');

    private Table<String, String, List<URL>> getDoubtItems() {
        Set<String> allGroup = zkClusterService.getAllGroup();

        Table<String, String, List<URL>> doubtItems = HashBasedTable.create();
        for (String group : allGroup) {
            List<String> groups = Lists.newArrayList();
            groups.add(group);
            scanGroupDisables(doubtItems, groups);
        }
        return doubtItems;
    }

    private Table<String, String, List<URL>> getDoubtItems(Search search) {
        Set<String> allGroup = zkClusterService.getAllGroup();

        Table<String, String, List<URL>> doubtItems = HashBasedTable.create();
        for (String group : allGroup) {
            List<String> groups = Lists.newArrayList();
            groups.add(group);
            scanGroupDisables(doubtItems, groups, search);
        }
        return doubtItems;
    }

    private void scanGroupDisables(Table<String, String, List<URL>> doubtItems, List<String> groups) {
        for (String group : groups) {
            Set<String> serviceNames = zkClusterService.getServiceNamesByGroup(group);
            for (String service : serviceNames) {
                List<URL> disableUrls = getConfiguredDisableUrls(group, service);
                if (!disableUrls.isEmpty()) {
                    doubtItems.put(group, service, disableUrls);
                }
            }
        }
    }

    private void scanGroupDisables(Table<String, String, List<URL>> doubtItems, List<String> groups, Search search) {
        for (String group : groups) {
            Set<String> serviceNames = zkClusterService.getServiceNamesByGroup(group);
            for (String service : serviceNames) {
                List<URL> results = search.search(group, service);
                doubtItems.put(group, service, results);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<URL> getConfiguredDisableUrls(final String group, String service) {
        try {
            List<URL> disableUrls = configuratorService.getItems(group, service, UrlHelper.hasNoKeyOf(ConstantHelper.CACTUS), isDisableConfig).toList();
            if (disableUrls.isEmpty()) return ImmutableList.of();

            GovernanceQuery query = GovernanceQuery.createBuilder()
                    .group(PathUtil.getGroup(group))
                    .serviceName(service)
                    .pathType(PathType.CONFIG)
                    .dataType(DataType.APIDATA)
                    .status(Status.ENABLE)
                    .build();
            List<GovernanceData> dbConfigs = queryService.getGovernanceDatas(query).toList();

            Set<URL> providerSet = providerService.getItems(group, service).toSortedSet(distinguish);
            Map<URL, URL> providerMap = Maps.newTreeMap(distinguish);
            for (URL provider : providerSet) {
                providerMap.put(provider, provider);
            }

            List<URL> urls = Lists.newArrayListWithExpectedSize(disableUrls.size());
            for (URL disableUrl : disableUrls) {
                URL newUrl = disableUrl;

                for (GovernanceData dbConfig : dbConfigs) {
                    if (dbConfig.getZkId() == disableUrl.getParameter(ConstantHelper.ZKID, 0)
                            && dbConfig.getUrl().getAddress().equals(disableUrl.getAddress())
                            && dbConfig.getUrl().getServiceKey().equals(disableUrl.getServiceKey())) {
                        newUrl = newUrl.addParameter(EQUAL_DB, true);
                        break;
                    }
                }

                URL provider = providerMap.get(disableUrl);
                if (provider != null) {
                    newUrl = newUrl.addParameter(HAS_PROVIDER, true);
                    newUrl = newUrl.addParameter(DUBBO_VERSION_KEY, provider.getParameter(DUBBO_VERSION_KEY, ""));
                }

                urls.add(newUrl);
            }

            return urls;
        } catch (Exception e) {
            logger.warn("scan error with {}/{}", group, service, e);
            return ImmutableList.of();
        }
    }

    private static final String EQUAL_DB = "equalDb";
    private static final String HAS_PROVIDER = "hasProvider";

    private static final Predicate<URL> HAS_PROVIDER_FUNC = new Predicate<URL>() {
        @Override
        public boolean apply(URL input) {
            return input.getParameter(HAS_PROVIDER, false);
        }
    };

    private static final Predicate<URL> EQUAL_DB_FUNC = new Predicate<URL>() {
        @Override
        public boolean apply(URL input) {
            return input.getParameter(EQUAL_DB, false);
        }
    };

    private static final Predicate<URL> NEW_DISABLE_FUNC = new Predicate<URL>() {
        @Override
        public boolean apply(URL input) {
            return input.hasParameter(ConstantHelper.Q_REGISTRY_KEY);
        }
    };

    private Predicate<URL> isDisableConfig = new Predicate<URL>() {
        @Override
        public boolean apply(URL input) {
            return input.getParameter(DISABLED_KEY, false);
        }
    };

    private Comparator<URL> distinguish = new Comparator<URL>() {
        @Override
        public int compare(URL lhs, URL rhs) {
            return ComparisonChain.start()
                    .compare(lhs.getParameter(ConstantHelper.ZKID), rhs.getParameter(ConstantHelper.ZKID))
                    .compare(lhs.getAddress(), rhs.getAddress())
                    .compare(lhs.getServiceKey(), rhs.getServiceKey())
                    .result();
        }
    };
}
