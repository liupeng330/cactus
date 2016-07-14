package com.qunar.corp.cactus.drainage.bean;
import com.qunar.corp.cactus.drainage.tools.IpHostHelper;
import com.qunar.corp.cactus.util.CommonCache;

/**
 * @author sen.chai
 * @date 2015-04-22 11:53
 */
public class DrainageIpAndPort implements Comparable<DrainageIpAndPort> {

	private String ip;

	private int port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DrainageIpAndPort() {
	}

	public static DrainageIpAndPort of(String ip, int port) {
		return new DrainageIpAndPort(ip, port);
	}

	public static DrainageIpAndPort of(String ipAndPortString) {
		int index = ipAndPortString.indexOf(":");
		String ip = IpHostHelper.toIpIfHost(ipAndPortString.substring(0, index));
		int port = Integer.parseInt(ipAndPortString.substring(index + 1));
		return new DrainageIpAndPort(ip, port);
	}

	public DrainageIpAndPort(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DrainageIpAndPort that = (DrainageIpAndPort) o;

		if (port != that.port)
			return false;
		if (ip != null ? !ip.equals(that.ip) : that.ip != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = ip != null ? ip.hashCode() : 0;
		result = 31 * result + port;
		return result;
	}

	@Override
	public int compareTo(DrainageIpAndPort o) {
		return this.ip.compareTo(o.getIp());
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}

	public String buildFormalString() {
		return "DrainageIpAndPort{ip:\"" + ip + "\",port:" + port + "}";
	}

	public String buildHostFormalString() {
		return CommonCache.getHostNameByIp(ip) + ":" + port;
	}

	public static String defaultFormalString() {
		return "127.0.0.1:8080";
	}

}
