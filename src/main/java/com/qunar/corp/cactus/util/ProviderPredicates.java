package com.qunar.corp.cactus.util;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.qunar.corp.cactus.bean.ServiceSign;
import com.qunar.corp.cactus.util.ConstantHelper;

import static com.alibaba.dubbo.common.Constants.GROUP_KEY;
import static com.alibaba.dubbo.common.Constants.VERSION_KEY;
import static com.google.common.base.Strings.nullToEmpty;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-12-19 Time: 下午2:31
 */
public class ProviderPredicates {

    public static Predicate<URL> addressEqual(String address) {
        return new AddressPredicate(address);
    }

    public static class AddressPredicate implements Predicate<URL> {
        private final String address;

        public AddressPredicate(String address) {
            this.address = address;

        }

        @Override
        public boolean apply(com.alibaba.dubbo.common.URL input) {
            return Objects.equal(address, input.getAddress());
        }
    }

    public static Predicate<URL> serviceKeyEqual(ServiceSign sign) {
        return new ServiceKeyPredicate(sign);
    }

    public static class ServiceKeyPredicate implements Predicate<URL> {
        private final ServiceSign sign;

        public ServiceKeyPredicate(ServiceSign sign) {
            this.sign = sign;
        }

        @Override
        public boolean apply(com.alibaba.dubbo.common.URL input) {
            String inputInterface = nullToEmpty(input.getServiceInterface());
            String serviceGroup = nullToEmpty(input.getParameter(GROUP_KEY));
            String version = nullToEmpty(input.getParameter(VERSION_KEY));
            return inputInterface.equals(sign.serviceInterface) && serviceGroup.equals(sign.serviceGroup)
                    && version.equals(sign.version);
        }
    }


}
