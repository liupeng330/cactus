package com.qunar.corp.cactus.drainage.bean;

/**
 * @author sen.chai
 * @date 2015-05-07 19:30
 */
public class HttpInvokeResult {

    public static final int SUCCESS_STATUS = 0;

    public static final int EXCEPTION_STATUS = 500;

    private Object data;

    private int status;

    private String message;

    public HttpInvokeResult() {
    }


    public HttpInvokeResult(Object data) {
        this.data = data;
        this.status = SUCCESS_STATUS;
    }

    public HttpInvokeResult(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public HttpInvokeResult(int status) {
        this.status = status;
    }

    public static HttpInvokeResult errorResult(int status) {
        return new HttpInvokeResult(status);
    }

    public static HttpInvokeResult exceptionResult(String message) {
        return new HttpInvokeResult(message, EXCEPTION_STATUS);
    }

    public static HttpInvokeResult errorResult(int status, String message) {
        return new HttpInvokeResult(message, status);
    }

    public static HttpInvokeResult successResult() {
        return new HttpInvokeResult(0);
    }

    public static HttpInvokeResult successResult(Object data) {
        return new HttpInvokeResult(data);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
