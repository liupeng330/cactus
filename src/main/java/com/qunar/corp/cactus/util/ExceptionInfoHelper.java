package com.qunar.corp.cactus.util;

import com.google.common.base.Joiner;
import com.qunar.corp.cactus.exception.*;
import com.qunar.corp.cactus.support.DelayedString;

/**
 * Date: 13-11-29 Time: 下午2:01
 * 
 * @author: xiao.liang
 * @description:
 */
public class ExceptionInfoHelper {

    private static final String DUBBO_EXCEPTION_INFO_PATTERN = "您的服务使用了%s以下的dubbo版本，系统无法对其进行操作，请升级dubbo版本。";
    private static final String STATIC_PROVIDER_EXCEPTION_INFO_PATTERN = "存在不支持的提供者 %s";

    public static String buildDubboExceptionInfo(DubboVersionException e) {
        return String.format(DUBBO_EXCEPTION_INFO_PATTERN, e.getLeastVersion());
    }

    public static String buildUnSupportProviderExceptionInfo(UnSupportProviderException e) {
        return String.format(STATIC_PROVIDER_EXCEPTION_INFO_PATTERN,
                DelayedString.toString(e.getUrls(), UrlHelper.URL_TO_FULL_STRING_FUNC));
    }

    public static String buildVariousExceptionInfo(Exception e) {
        if (e == null) {
            return "";
        }
        if (e instanceof LastProviderException) {
            return "该提供者是该服务下唯一一台提供者，无法下线！";
        } else if (e instanceof UnSupportProviderException) {
            return buildUnSupportProviderExceptionInfo((UnSupportProviderException) e);
        } else if (e instanceof DubboVersionException) {
            return buildDubboExceptionInfo((DubboVersionException) e);
        } else if (e instanceof IllegalKeyException) {
            IllegalKeyException illegalKeyException = (IllegalKeyException) e;
            return "您传入了非法的参数" + illegalKeyException.getIllegalKeys() + ",配置失败！";
        } else if (e instanceof IllegalParamException) {
            return "您给下列参数设置了非法的值" + ((IllegalParamException) e).getIllegalParams() + ",配置失败！";
        } else if (e instanceof EmptyParamException) {
            return "参数值不能为空！";
        } else if (e instanceof NoMatchedException) {
            return "没有合适的服务提供者或消费者满足您的匹配条件，配置失败！";
        } else if (e instanceof WrongMatcherException) {
            return "您的匹配参数有误，配置失败！错误参数为: " + COMMA_JOINER.join(((WrongMatcherException) e).getWrongMatchers());
        } else if (e instanceof ForceToUnMatchException) {
            return "您的设置会导致如下消费者[" + DelayedString.toString(((ForceToUnMatchException) e).getUrls()) + "]无法正常使用，配置失败！";
        } else if (e instanceof MultiWhiteListException) {
            return "已经有存在的白名单";
        } else if (e instanceof EnableToDeleteException) {
            return "请先禁用再删除！";
        } else {
            return "在配置时出现未知的异常，请与管理员联系！";
        }
    }

    private static final Joiner COMMA_JOINER = Joiner.on(", ");
}
