package com.qunar.corp.cactus.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.dao.GroupClusterDao;
import com.qunar.corp.cactus.dao.SpecialDao;
import com.qunar.corp.cactus.exception.GroupClusterExistException;
import com.qunar.corp.cactus.service.SpecialService;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.corp.cactus.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2014 14-3-19 下午5:52
 */
@Service
public class SpecialServiceImpl implements SpecialService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialServiceImpl.class);

    private volatile List<String> groupClusters;

    private volatile List<String> specialGroups;

    @Resource
    private SpecialDao specialDao;

    @Resource
    private GroupClusterDao groupClusterDao;

    @Resource
    private ZKClusterService zkClusterService;

    @Override
    public void freshSpecials() {
        logger.info("start fresh specials");
        fresh();
        reset();
    }

    public void fresh() {
        logger.info("start fresh group cluster");
        Set<String> clusters = Sets.newHashSet(groupClusterDao.selectAll());
        Set<String> specialGroups = Sets.newHashSet(specialDao.selectAll());

        List<String> groupsToDelete = Lists.newArrayList();
        for (String specialGroup : specialGroups) {
            String cluster = PathUtil.getCluster(specialGroup);
            if (!clusters.contains(cluster)) {
                groupsToDelete.add(specialGroup);
            }
        }

        if (!groupsToDelete.isEmpty()) {
            specialGroups.removeAll(groupsToDelete);
            specialDao.clear(groupsToDelete);
        }

        freshSpecialGroups(clusters, specialGroups);
        logger.info("end fresh group cluster");
    }

    private void freshSpecialGroups(Set<String> clusters, Set<String> specialGroups) {
        Set<String> newSpecialGroups = Sets.newHashSet();
        for (String cluster : clusters) {
            newSpecialGroups.addAll(findSpecialGroups(cluster));
        }

        List<String> lackGroups = Sets.difference(newSpecialGroups, specialGroups).immutableCopy().asList();
        if (!lackGroups.isEmpty()) {
            specialDao.insert(lackGroups);
        }
    }

    // cluster自身是不应该被查找的
    private Set<String> findSpecialGroups(String cluster) {
        Set<String> specialGroups = Sets.newHashSet();
        Set<String> children = zkClusterService.getChildren(cluster);
        for (String child : children) {
            findSpecialGroups(cluster, child, specialGroups);
        }
        return specialGroups;
    }

    private void findSpecialGroups(String parent, String node, Set<String> specialGroups) {
        if (ConstantHelper.DUBBO_NODES.contains(node)) {
            return;
        }

        String path = PathUtil.makePath(parent, node);
        Set<String> children = zkClusterService.getChildren(path);
        if (children.isEmpty()) {
            return;
        }

        if (zkClusterService.isPreServicePath(path)) {
            specialGroups.add(path);
        }

        for (String child : children) {
            findSpecialGroups(path, child, specialGroups);
        }
    }

    private void reset() {
        logger.info("start reset group cluster");
        List<String> clusters = groupClusterDao.selectAll();
        List<String> specialGroups = specialDao.selectAll();
        this.groupClusters = Ordering.natural().immutableSortedCopy(clusters);
        this.specialGroups = Ordering.natural().immutableSortedCopy(specialGroups);
    }

    @Override
    public void add(String cluster) throws GroupClusterExistException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cluster), "group cluster [%s] can not be null or empty", cluster);

        logger.info("add special service, cluster={}", cluster);

        if (cluster.startsWith("/")) {
            Preconditions.checkArgument(cluster.length() >= 2, "invalid cluster [%s]", cluster);
            cluster = cluster.substring(1);
        }

        try {
            groupClusterDao.insert(cluster);
        } catch (DuplicateKeyException e) {
            logger.warn("group cluster is existed, cluster={}", cluster);
            throw new GroupClusterExistException();
        }
    }

    @Override
    public List<String> getGroups() {
        return specialGroups;
    }

    @Override
    public List<String> getGroupClusters() {
        return groupClusters;
    }

    @Override
    public int delete(String cluster) {
        return groupClusterDao.delete(cluster);
    }
}
