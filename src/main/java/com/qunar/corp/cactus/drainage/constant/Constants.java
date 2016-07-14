package com.qunar.corp.cactus.drainage.constant;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author sen.chai
 * @date 2015-04-21 14:31
 */
public class Constants {

    public static final Joiner VERTICAL_JOINER = Joiner.on("|").skipNulls();

    public static final Splitter VERTICAL_SPLITTER = Splitter.on("|").omitEmptyStrings().trimResults();
    
    public static final Splitter GROUP_SPLITTER = Splitter.on("=").omitEmptyStrings().trimResults();

    public static final String PROXY_ADDRESS_CONFIG_KEY_PREFIX = "proxy.address.";

    public static final String PROXY_PORT_CONFIG_KEY = "proxy.port";

    public static final String ROUTE_ADDRESS_CONFIG_PREFIX = "route.address.";

    public static final String INTERCEPT_ADDRESS_CONFIG_PREFIX = "intercept.address.";

    public static final String BETA_URL = "drainage.beta.url";
}
