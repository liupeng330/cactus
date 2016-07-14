package com.qunar.corp.cactus.web.controller;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qunar.corp.cactus.bean.GovernanceData;
import com.qunar.corp.cactus.bean.Status;
import com.qunar.corp.cactus.bean.ZKCluster;
import com.qunar.corp.cactus.service.ZKClusterService;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.web.model.GovernanceView;
import com.qunar.corp.cactus.web.model.ListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import qunar.api.pojo.CodeMessage;
import qunar.api.pojo.json.JsonV2;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: liuzz Date: 13-11-18 Time: 下午2:21
 */
@Controller
public class AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    protected static final String INVALID_STATUS_TO_UPDATE_WARNING = "仅禁用状态的配置才能进行修改！";
    protected static final String INVALID_ID_WARNING = "无效的id,无法修改!";
    protected static final String VALID_DATA = "validData";

    @Resource
    private ZKClusterService zkClusterService;

    protected Object dataJson() {
        return dataJson("");
    }

    protected Object dataJson(Object value) {
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(2);
        data.put("ret", true);
        data.put("data", value);
        return data;
    }

    protected Object errorJson(String message) {
        return errorJson(CodeMessage.SYSTEM_ERROR, message);
    }

    protected Object errorJson(int errorCode, String message) {
        return new JsonV2<String>(errorCode, message, null);
    }

    protected Object oldErrorJson(int errorCode, String message) {
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(3);
        data.put("ret", false);
        data.put("errcode", errorCode);
        data.put("errmsg", message);
        return data;
    }

    @ExceptionHandler(TypeMismatchException.class)
    protected String typeMismatchExceptionHandler(TypeMismatchException e) {
        logger.error("", e);
        return "redirect:/400.jsp";
    }

    @ExceptionHandler(Exception.class)
    protected String exceptionHandler(Exception exception) {
        logger.error("", exception);
        return "redirect:/500.jsp";
    }

    @ModelAttribute
    public void addUsernameAttrToAllControllerMethod(Model model) {
        model.addAttribute("username", UserContainer.getUserName());
    }

    protected String getCheckDataResult(GovernanceData governanceData) {
        if (governanceData == null) {
            return INVALID_ID_WARNING;
        }
        if (governanceData.getStatus().code != Status.DISABLE.code) {
            return INVALID_STATUS_TO_UPDATE_WARNING;
        }
        return VALID_DATA;
    }

    protected <T> ListResult<T> pageResult(int pageNum, int pageSize, List<T> oriResult) {
        if (oriResult == null) {
            oriResult = Lists.newArrayList();
        }
        int fromIndex = transformPageNum2Index(pageNum, pageSize);
        int toIndex = pageNum * pageSize;
        ListResult<T> list = new ListResult<T>();
        list.setTotalRow(oriResult.size());
        list.setCurrentPageNum(pageNum);
        list.setPageSize(pageSize);
        fromIndex = fromIndex > oriResult.size() ? (oriResult.size() - 1 >= 0 ? oriResult.size() - 1 : 0) : fromIndex;
        List<T> data = oriResult.size() >= toIndex ? oriResult.subList(fromIndex, toIndex) : oriResult.subList(
                fromIndex, oriResult.size());
        list.setDatas(data);
        return list;
    }

    protected <T> ListResult<T> pageDatabaseResult(int pageNum, int pageSize, int totalSize, List<T> oriResult) {
        if (oriResult == null) {
            oriResult = Lists.newArrayList();
        }
        ListResult<T> list = new ListResult<T>();
        list.setTotalRow(totalSize);
        list.setCurrentPageNum(pageNum);
        list.setPageSize(pageSize);
        list.setDatas(oriResult);
        return list;
    }

    protected int transformPageNum2Index(int pageNum, int pageSize) {
        return (pageNum - 1) * pageSize;
    }

    protected static final Function<GovernanceData, GovernanceView> TO_GOVERNANCE_VIEW_FUNC = new Function<GovernanceData, GovernanceView>() {
        @Override
        public GovernanceView apply(GovernanceData input) {
            return new GovernanceView(input);
        }
    };

    protected static final Comparator<GovernanceView> COMPARE_STATUS_AND_LAST_UPDATE_TIME = new Comparator<GovernanceView>() {
        @Override
        public int compare(GovernanceView o1, GovernanceView o2) {
            return ComparisonChain.start()
                    .compare(o2.getGovernanceData().getStatus().getCode(), o1.getGovernanceData().getStatus().getCode())
                    .compare(o2.getGovernanceData().getLastUpdateTime(), o1.getGovernanceData().getLastUpdateTime())
                    .result();
        }
    };

    protected String getZKName(long zkId) {
        Optional<ZKCluster> zkCluster = zkClusterService.findZkCluster(zkId);
        return zkCluster.isPresent() ? zkCluster.get().getName() : null;
    }
}
