package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;
import com.qunar.corp.cactus.drainage.bean.HttpInvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.hc.QunarAsyncClient;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author sen.chai
 * @date 2015-05-08 11:43
 */
@Service
public class StandardHttpService {

    private static final Logger logger = LoggerFactory.getLogger(StandardHttpService.class);

    @Resource
    private QunarAsyncClient httpClient;

    /**
     * @param url
     * @param httpParam
     * @param resultFunction 把response转换为标准json里面的data
     */
    public void invokeIgnoreResult(final String url, final Map<String, String> httpParam, final Function<String, Object> resultFunction) {
        try {
            HttpInvokeResult invokeResult = invokeWithResult(url, httpParam, resultFunction);
            if (invokeResult.getStatus() != HttpInvokeResult.SUCCESS_STATUS) {
                throw new RuntimeException("invoke http failed, status=" + invokeResult.getStatus() + ", message=" + invokeResult.getMessage());
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }

    }


    public HttpInvokeResult invokeWithResult(final String url, final Map<String, String> httpParam, final Function<String, Object> resultFunction) {
        try {
            return httpClient.post(url, httpParam, Charsets.UTF_8.toString(), new AsyncCompletionHandler<HttpInvokeResult>() {
                @Override
                public HttpInvokeResult onCompleted(Response response) throws Exception {
                    try {
                        int statusCode = response.getStatusCode();
                        if (statusCode != HttpServletResponse.SC_OK) {
                            logger.error("process response error!, statusCode={}, param={}", statusCode, httpParam);
                            return HttpInvokeResult.errorResult(statusCode);
                        }
                        String responseBody = response.getResponseBody();
                        logger.info("http response: {}", responseBody);
                        return HttpInvokeResult.successResult(resultFunction.apply(responseBody));
                    } catch (Exception e) {
                        return HttpInvokeResult.exceptionResult(e.getMessage());
                    }
                }
            }).get();
        } catch (Exception e) {
            return HttpInvokeResult.exceptionResult(e.getMessage());
        }
    }

}
