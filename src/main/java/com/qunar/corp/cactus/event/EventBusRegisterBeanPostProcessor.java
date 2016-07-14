package com.qunar.corp.cactus.event;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-23
 * Time: 下午3:08
 */
public class EventBusRegisterBeanPostProcessor implements BeanPostProcessor {

    @Resource
    private EventBus eventBus;

    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof EventListener) {
            eventBus.register(o);
        }
        return o;
    }

}
