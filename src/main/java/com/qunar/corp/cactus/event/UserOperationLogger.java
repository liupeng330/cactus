package com.qunar.corp.cactus.event;

import com.google.common.eventbus.Subscribe;
import com.qunar.corp.cactus.bean.Log;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.dao.LogDao;
import com.qunar.corp.cactus.util.CommonCache;
import com.qunar.corp.cactus.util.ConstantHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zhenyu.nie created on 2014 2014/12/23 20:38
 */
@Service
public class UserOperationLogger implements EventListener {

    @Resource
    private LogDao logDao;

    private static final String HOSTNAME_PATTERN = "%s%s(%s)";

    @Subscribe
    public void logOperation(UserOperationEvent event) {
        Log log = new Log();
        User user = new User();
        user.setId((int) event.getUid());
        log.setUser(user);
        ZKCluster zkCluster = new ZKCluster();
        zkCluster.setId(event.getSign().getZkId());
        log.setZkCluster(zkCluster);
        log.setGroup(event.getSign().getGroup());
        log.setServiceGroup(event.getSign().getServiceGroup());
        log.setService(event.getSign().getServiceInterface());
        log.setVersion(event.getSign().getVersion());
        log.setHostName(makeHostName(event.getSign().getAddress()));
        log.setIp(event.getSign().getIp());
        log.setPort(event.getSign().getPort());
        log.setMessage(event.getMessage());
        log.setOperateTime(new Date());
        if (event.getUid() == ConstantHelper.CACTUS_API_ID) {
            logDao.addCactusApiLog(log);
        } else {
            logDao.add(log);
        }
    }

    private String makeHostName(String address) {
        int indexOfColon = address.indexOf(':');
        if (indexOfColon < 0) {
            return String.format(HOSTNAME_PATTERN, CommonCache.getHostNameByIp(address), "", address);
        } else {
            String ip = address.substring(0, indexOfColon);
            String port = address.substring(indexOfColon + 1);
            return String.format(HOSTNAME_PATTERN, CommonCache.getHostNameByIp(ip), ":" + port, address);
        }
    }
}
