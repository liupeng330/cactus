package com.qunar.corp.cactus.drainage.bean;

import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author kelly.li
 * @date 2015-07-01
 */
public class DrainageGroup {

	private String groupName;
	private int n;
	private Set<DrainageIpAndPort> drainageIpAndPorts = Sets.newHashSet();
	private boolean status;
	private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
	private static final Joiner JOINER = Joiner.on(",");
	

	public DrainageGroup(String groupName, int n, boolean status) {
		super();
		this.groupName = groupName;
		this.n = n;
		this.status = status;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<DrainageIpAndPort> getDrainageIpAndPorts() {
		return drainageIpAndPorts;
	}

	public void setDrainageIpAndPorts(Set<DrainageIpAndPort> drainageIpAndPorts) {
		this.drainageIpAndPorts = drainageIpAndPorts;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public String getTargetList() {
		return JOINER.join(Iterables.transform(drainageIpAndPorts, new Function<DrainageIpAndPort, String>() {
			public String apply(DrainageIpAndPort drainageIpAndPort) {
				return drainageIpAndPort.getIp() + ":" + drainageIpAndPort.getPort();
			}
		}));
	}

	public void setTargetList(String format) {
		drainageIpAndPorts = Sets.newHashSet(Iterables.transform(SPLITTER.splitToList(format), new Function<String, DrainageIpAndPort>() {
			public DrainageIpAndPort apply(String drainageIpAndPortString) {
				return DrainageIpAndPort.of(drainageIpAndPortString);
			}
		}));
	}

	@Override
	public String toString() {
		return "DrainageGroup={groupName:\"" + groupName + "\",drainageIpAndPorts:" + drainageIpAndPorts + ",status:" + status + "}";
	}

}
