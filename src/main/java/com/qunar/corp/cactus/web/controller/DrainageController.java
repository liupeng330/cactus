package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.*;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.drainage.bean.DrainageGroup;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;
import com.qunar.corp.cactus.drainage.bean.DrainageServiceInfo;
import com.qunar.corp.cactus.drainage.constant.Constants;
import com.qunar.corp.cactus.drainage.service.DrainageInfoDao;
import com.qunar.corp.cactus.drainage.service.DrainageManager;
import com.qunar.corp.cactus.drainage.tools.GlobalConfig;
import com.qunar.corp.cactus.drainage.tools.IpHostHelper;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.ProviderPredicates;
import com.qunar.corp.cactus.util.UrlHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import qunar.web.spring.annotation.JsonBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.qunar.corp.cactus.util.ConstantHelper.UID_COOKIE_NAME;
import static com.qunar.corp.cactus.util.ConstantHelper.USERNAME_COOKIE_NAME;
import static com.qunar.corp.cactus.web.CookieHelper.getDecodeUserInfoCookie;

/**
 * @author sen.chai
 * @date 2015-04-21 14:16
 */
@Controller
@RequestMapping("drainage")
public class DrainageController {

	private static final Logger logger = LoggerFactory.getLogger(DrainageController.class);

	@Resource
	private DrainageInfoDao drainageInfoDao;

	@Resource
	private DrainageManager drainageManager;

	@Resource
	private ProviderService providerService;

	private static final Splitter SPLITTER = Splitter.on("|").trimResults().omitEmptyStrings();

	@RequestMapping("start")
	@JsonBody
	public String start(@RequestParam String service, @RequestParam(required = false) String methodName, @RequestParam long zkId, @RequestParam String serviceKey,
			@RequestParam String groupAddressString, @RequestParam String providerAddressString, @RequestParam(required = false) String group, @RequestParam int n, HttpServletRequest request) {
		try {
			recordIfCookieValid(request);
			Preconditions.checkArgument(!Strings.isNullOrEmpty(groupAddressString), "beta地址不能为空");
			Set<DrainageGroup> groupTargets = transformToDrainageGroup(groupAddressString, n, true);

			validAddress(groupTargets);
			Set<DrainageIpAndPort> providerSet = transformToIpAndPort(providerAddressString);
			drainageManager.startDrainage(service, methodName, zkId, serviceKey, group, groupTargets, providerSet);

			return "success";
		} catch (Exception e) {
			logger.error("start error!", e);
			throw Throwables.propagate(e);
		}
	}

	private void recordIfCookieValid(HttpServletRequest request) {
		Map<String, String> cookieParam = getDecodeUserInfoCookie(request);
		UserContainer.setUserId(Integer.parseInt(cookieParam.get(UID_COOKIE_NAME)));
		UserContainer.setUserName(cookieParam.get(USERNAME_COOKIE_NAME));
		MDC.put("username", UserContainer.getUserName());

	}

	private void validAddress(Set<DrainageGroup> drainageGroups) {
		try {
			List<String> envKeyWords = SPLITTER.splitToList(GlobalConfig.get("env.key.word"));
			for (DrainageGroup drainageGroup : drainageGroups) {
				for (DrainageIpAndPort ipAndPort : drainageGroup.getDrainageIpAndPorts()) {
					String ip = ipAndPort.getIp();
					String hostName = IpHostHelper.toHostIfIp(ip);
					boolean flag = false;
					for (String env : envKeyWords) {
						if (hostName.contains(env)) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						throw new RuntimeException(String.format("ip=%s,hostName=%s, 不是%s环境的机器", ip, hostName, envKeyWords));
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("目标机参数不合法,引流失败, reason:" + e.getMessage(), e);
		}
	}

	private Set<DrainageIpAndPort> transformToIpAndPort(String providerAddressString) {
		Iterable<DrainageIpAndPort> drainageIpAndPortIterable = Iterables.transform(Constants.VERTICAL_SPLITTER.split(providerAddressString), new Function<String, DrainageIpAndPort>() {
			@Override
			public DrainageIpAndPort apply(String input) {
				return DrainageIpAndPort.of(input);
			}
		});
		return Sets.newHashSet(drainageIpAndPortIterable);
	}

	private Set<DrainageGroup> transformToDrainageGroup(String targetAddressString, int n, boolean status) {
		Map<String, DrainageGroup> map = Maps.newHashMap();
		List<String> lines = Constants.VERTICAL_SPLITTER.splitToList(targetAddressString);
		for (String line : lines) {
			List<String> groupAndTarget = Constants.GROUP_SPLITTER.splitToList(line);
			DrainageGroup group = null;
			if (groupAndTarget.size() == 1) {
				group = map.get("default");
				if (group == null) {
					group = new DrainageGroup("default", n, status);
					map.put("default", group);
				}
				group.getDrainageIpAndPorts().add(DrainageIpAndPort.of(groupAndTarget.get(0)));
			} else if (groupAndTarget.size() == 2) {
				group = map.get(groupAndTarget.get(0));
				if (group == null) {
					group = new DrainageGroup(groupAndTarget.get(0), n, status);
					map.put(groupAndTarget.get(0), group);
				}
				group.getDrainageIpAndPorts().add(DrainageIpAndPort.of(groupAndTarget.get(1)));
			}
		}
		return Sets.newHashSet(map.values());
	}

	@RequestMapping("getStopInfo")
	@JsonBody
	public Object getStopInfo(@RequestParam String service, @RequestParam(required = false) String methodName) {

		Multimap<String, String> result = HashMultimap.create();
		List<DrainageIpAndPort> serviceIpAndPorts = drainageInfoDao.queryServiceDrainageInfo(service, methodName);
		for (DrainageIpAndPort ipAndPort : serviceIpAndPorts) {
			List<DrainageServiceInfo> drainageServiceInfo = drainageInfoDao.queryServiceDrainageInfo(ipAndPort);
			for (DrainageServiceInfo serviceInfo : drainageServiceInfo) {
				Collection<String> collection = result.get(ipAndPort.buildHostFormalString());
				if (collection != null && collection.contains(serviceInfo.getServiceName())) {
					continue;
				}
				result.put(ipAndPort.buildHostFormalString(), serviceInfo.getServiceName());
			}
		}
		return result.asMap();
	}

	@RequestMapping("stop")
	@JsonBody
	public String stop(@RequestParam String service, @RequestParam long zkId, @RequestParam String serviceKey, @RequestParam(required = false) String group,
			@RequestParam String serviceNameListString, HttpServletRequest request) {
		try {
			recordIfCookieValid(request);
			Iterable<String> iterable = Constants.VERTICAL_SPLITTER.split(serviceNameListString);
			for (String serviceName : iterable) {
				String methodName = drainageInfoDao.queryServiceDrainageInfo(serviceName);
				drainageManager.stopDrainage(serviceName, methodName, zkId, serviceKey, group);
			}
			return "success";
		} catch (Exception e) {
			logger.error("stop error!", e);
			throw Throwables.propagate(e);
		}
	}

	@RequestMapping("getAllInfo")
	@JsonBody
	public Object getAllInfo() {
		return drainageInfoDao.queryAllTargetForDubboCopy();
	}

	@RequestMapping("getBetaAddress")
	@JsonBody
	public Object getBetaAddress(@RequestParam String service, @RequestParam(required = false, defaultValue = "") String methodName, @RequestParam long zkId, @RequestParam String serviceKey,
			@RequestParam(required = false) String group) {

		return drainageManager.getBetaAddress(service, methodName, GlobalConfig.getLong("drainage.beta.zkId"), serviceKey, group);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("getMethods")
	@JsonBody
	public Object getMethods(@RequestParam long zkId, @RequestParam String serviceKey, @RequestParam(required = false) String group) {
		ServiceSign serviceSign = ServiceSign.makeServiceSign(zkId, group, serviceKey);
		return providerService.getMethods(serviceSign.getGroup(), serviceSign.serviceInterface, ProviderPredicates.serviceKeyEqual(serviceSign), UrlHelper.zkIdEqual(serviceSign.zkId));
	}

	@RequestMapping("getProviderAddress")
	@JsonBody
	public Object getAllAddress(@RequestParam String service, @RequestParam(required = false) String methodName, @RequestParam long zkId, @RequestParam String serviceKey,
			@RequestParam(required = false) String group) {

		return drainageManager.getProviderAddress(service, methodName, zkId, serviceKey, group);
	}

	@RequestMapping("mock")
	@JsonBody
	public Object mockStart(@RequestParam String serviceip, @RequestParam String interceptip, @RequestParam int serviceport, @RequestParam String proxyip, @RequestParam int proxyport,
			@RequestParam String routeip, @RequestParam String action) {

		logger.info("{}", Lists.newArrayList(serviceip, interceptip, serviceport, proxyip, proxyport, routeip, action));
		return Lists.newArrayList(ImmutableMap.of(serviceip, 0));
	}

}
