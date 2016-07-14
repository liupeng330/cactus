package com.qunar.corp.cactus.exception;

/**
 * Date: 14-8-12
 * Time: 上午11:50
 *
 * @author: xiao.liang
 * @description:
 */
public class GroupClusterExistException extends RuntimeException {
    public GroupClusterExistException() {
        super();
    }

    public GroupClusterExistException(String message) {
        super(message);
    }

    public GroupClusterExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupClusterExistException(Throwable cause) {
        super(cause);
    }
}
