package com.qunar.corp.cactus.drainage.service;


import com.qunar.corp.cactus.drainage.bean.TcpcopyParam;

/**
 * @author sen.chai 15-5-8 上午10:15
 */
public interface TcpcopyService {

    void start(TcpcopyParam tcpcopyParam);

    void stop(TcpcopyParam tcpcopyParam);

}
