package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.qunar.corp.cactus.drainage.bean.DrainageGroup;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;
import com.qunar.corp.cactus.drainage.bean.DrainageParam;
import com.qunar.corp.cactus.drainage.bean.RunningStatus;
import com.qunar.corp.cactus.drainage.bean.TcpcopyParam;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import com.qunar.corp.cactus.drainage.tools.IpHostHelper;
import com.qunar.corp.cactus.util.CommonCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qunar.tc.dubbocopy.api.model.Group;
import qunar.tc.dubbocopy.api.model.Router;
import qunar.tc.dubbocopy.api.model.Target;
import qunar.tc.dubbocopy.api.service.RouterService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.qunar.corp.cactus.drainage.constant.Constants.INTERCEPT_ADDRESS_CONFIG_PREFIX;

/**
 * @author sen.chai
 * @date 2015-04-21 14:24
 */

@Service
public class TcpcopyDrainageHttpImpl implements DrainageService {

	private static final Logger logger = LoggerFactory.getLogger(TcpcopyDrainageHttpImpl.class);

	@Resource
	private DrainageLock drainageLock;

	@Resource
	private RouterService routerService;

	@Resource
	private ProxyLoadBalancer proxyLoadBalancer;

	@Resource
	private TcpcopyService tcpcopyService;

	public Map<RunningStatus, ? extends Function<TcpcopyParam, Boolean>> functionMap;

	@PostConstruct
	public void init() {
		functionMap = ImmutableMap.of(RunningStatus.start, new Function<TcpcopyParam, Boolean>() {
			@Override
			public Boolean apply(TcpcopyParam input) {
				tcpcopyService.start(input);
				return true;
			}
		}, RunningStatus.stop, new Function<TcpcopyParam, Boolean>() {
			@Override
			public Boolean apply(TcpcopyParam input) {
				tcpcopyService.stop(input);
				return true;
			}
		});
	}

	@Transactional
	@Override
	public void start(DrainageParam param) {
		logger.info("start: {}", param);
		drainageLock.lock(param);
		processDrainageRequest(param, RunningStatus.start);
	}

	@Transactional
	@Override
	public void stop(DrainageParam param) {
		logger.info("stop: {}", param);
		processDrainageRequest(param, RunningStatus.stop);
		drainageLock.unlock(param);
	}

	private void processDrainageRequest(DrainageParam param, RunningStatus status) {
//		try {
			invokeProxy(param, status);
			invokeTcpcopyAgent(param, status);
//		} catch (Exception e) {
//			logger.error("processDrainageRequest fail ", e);
//		}
	}

	private void invokeProxy(DrainageParam param, RunningStatus status) {
		logger.info("invoke proxy dubbo by broadcast: {}", param);
		List<String> methodNames = param.parseMethodNames();
		if(methodNames == null || methodNames.isEmpty()){
			Router router = new Router();
			router.setServiceName(param.getServiceName());
			router.setMethodName("");
			if (status == RunningStatus.start) {
				setGroup(param, router);
			}else{
				router.setGroups(Collections.EMPTY_SET);
			}
			routerService.setRouter(router);
		}else{
			for(String methodName:methodNames){
				Router router = new Router();
				router.setServiceName(param.getServiceName());
				router.setMethodName(methodName);
				if (status == RunningStatus.start) {
					setGroup(param, router);
				}else{
					router.setGroups(Collections.EMPTY_SET);
				}
				routerService.setRouter(router);
			}
		}
	}
	
	private void setGroup(DrainageParam param, Router router){
		Set<Group> groups = Sets.newHashSet(Iterables.transform(param.getTargetGroups(), new Function<DrainageGroup, Group>() {
			public Group apply(DrainageGroup drainageGroup) {
				Group group = new Group(drainageGroup.getGroupName(), drainageGroup.getN());
				group.addAll(Sets.newHashSet(Iterables.transform(drainageGroup.getDrainageIpAndPorts(), new Function<DrainageIpAndPort, Target>() {
					public Target apply(DrainageIpAndPort drainageIpAndPort) {
						return new Target(drainageIpAndPort.getIp(), drainageIpAndPort.getPort());
					}
				})));
				return group;
			}
		}));
		router.setGroups(groups);
	}

	private void invokeTcpcopyAgent(DrainageParam param, RunningStatus status) {
		try {
			List<TcpcopyParam> paramList = createTcpcopyParams(param, status);
			for (TcpcopyParam tcpcopyParam : paramList) {
				functionMap.get(status).apply(tcpcopyParam);
			}
		} catch (Exception e) {
			logger.error("", e);
			throw Throwables.propagate(e);
		}
	}

	private List<TcpcopyParam> createTcpcopyParams(DrainageParam param, RunningStatus status) {
		List<TcpcopyParam> paramList = Lists.newArrayList();
		for (DrainageIpAndPort serviceIpAndPort : param.getServiceIpAndPort()) {
			String roomId = resolveRoomId(serviceIpAndPort);
			DrainageIpAndPort proxyIpAndPort = chooseOneProxy(serviceIpAndPort, roomId);
			TcpcopyParam tcpcopyParam = new TcpcopyParam();
			tcpcopyParam.setServicePort(serviceIpAndPort.getPort());
			tcpcopyParam.setServiceIp(serviceIpAndPort.getIp());
			tcpcopyParam.setInterceptIp(chooseOneIntercept(serviceIpAndPort, roomId));
			tcpcopyParam.setRouteIp(chooseOneRoute(serviceIpAndPort, roomId));
			tcpcopyParam.setProxyIp(proxyIpAndPort.getIp());
			tcpcopyParam.setProxyPort(proxyIpAndPort.getPort());
			tcpcopyParam.setStatus(RunningStatus.valueOf(status.name()));
			paramList.add(tcpcopyParam);
		}
		return paramList;
	}

	private static String resolveRoomId(DrainageIpAndPort serviceIpAndPort) {
		String hostName = CommonCache.getHostNameByIp(serviceIpAndPort.getIp());
		logger.info("service host name is {}", hostName);
		final String webSitePrefix = ".qunar.com";
		if (hostName.endsWith(webSitePrefix)) {
			hostName = hostName.substring(0, hostName.length() - webSitePrefix.length());
		}
		int lastDotIndex = hostName.lastIndexOf(".");
		String roomId = hostName.substring(lastDotIndex + 1);
		if (!roomId.matches("cn\\d{1,}")) {
			throw new RuntimeException("未能找到合法的机房, host=" + hostName);
		}
		return roomId;
	}

	// 直接使用线上机器IP，绕过安全策略
	private String chooseOneRoute(DrainageIpAndPort serviceIpAndPort, String roomId) {
		return IpHostHelper.toIpIfHost(serviceIpAndPort.getIp());
		// return GlobalConfig.get(ROUTE_ADDRESS_CONFIG_PREFIX + roomId);
	}

	private String chooseOneIntercept(DrainageIpAndPort serviceIpAndPort, String roomId) {
		return GlobalConfig.get(INTERCEPT_ADDRESS_CONFIG_PREFIX + roomId);
	}

	private DrainageIpAndPort chooseOneProxy(DrainageIpAndPort serviceIpAndPort, String roomId) {
		DrainageIpAndPort proxyIpAndPort = proxyLoadBalancer.chooseOne(serviceIpAndPort, roomId);
		logger.info("{} choosed proxy: {}", serviceIpAndPort, proxyIpAndPort);
		return proxyIpAndPort;
	}

}
