package com.qunar.corp.cactus.zk;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午5:55
 */
public interface ZKService {

    List<String> getPathChildren(String path) throws Exception;

    void close();

    void register(String path, boolean ephemeral) throws Exception;

    void unRegister(String path) throws Exception;
}
