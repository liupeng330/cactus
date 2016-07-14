package com.qunar.corp.cactus.web.interceptor;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.Strings;
import com.qunar.corp.cactus.bean.Role;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.service.graph.GraphService;
import com.qunar.corp.cactus.service.UserService;
import com.qunar.corp.cactus.support.UserContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import static com.qunar.corp.cactus.util.ConstantHelper.*;
import static com.qunar.corp.cactus.web.CookieHelper.getDecodeUserInfoCookie;

/**
 * Date: 14-1-10 Time: 上午11:18
 * 
 * @author: xiao.liang
 * @description:
 */
@Component
public class VisitInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String USERNAME_MDC = "username";

    @Resource
    private UserService userService;

    @Resource
    private GraphService graphService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String visitURL = request.getRequestURL().toString();
        String queryString = request.getQueryString() == null ? "" : ("?" + request.getQueryString());
        queryString = queryString.replace("&", BACK_URL_PARAM_SPLITTER);
        String retUrl = visitURL + queryString;
        if (!recordIfCookieValid(request)) {
            return redirectToHomePage(request, response, retUrl);
        }
        logger.info("visit url: [{}]", retUrl);
        return hasRightToVisit(request, response, visitURL, retUrl);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        super.afterCompletion(request, response, handler, ex);
        UserContainer.remove();
        MDC.remove(USERNAME_MDC);
    }

    private boolean hasRightToVisit(HttpServletRequest request, HttpServletResponse response, String visitURL,
            String retUrl) throws Exception {
        String username = UserContainer.getUserName();
        Role role = getRole(username);
        if (role == null) {
            return redirectToHomePage(request, response, retUrl);
        }
        if (Role.ADMIN.equals(role)) {
            return true;
        }
        if (!visitURL.contains("show") && !visitURL.contains("distribute") && !visitURL.contains("search")
                && !visitURL.contains("mock") && !isIndexPage(visitURL)) {
            String group = getGroup(request);
            if (graphService.isOwnerOfGroup(username, group)) {
                return true;
            } else {
                logger.info("user [{}] is illegal access to group [{}] with url [{}]", username, group, retUrl);
                response.sendRedirect(request.getContextPath() + "/403.jsp");
                return false;
            }
        }
        return true;
    }

    private boolean isIndexPage(String visitURL) {
        String matchPart = visitURL.substring("http://".length());
        return matchPart.indexOf("/") == (matchPart.length() - 1);
    }

    private String getGroup(HttpServletRequest request) {
        String group = request.getParameter("group");
        if (group == null) {
            String refererUrlStr = request.getHeader("referer");
            if (refererUrlStr != null) {
                URL refererUrl = URL.valueOf(refererUrlStr);
                group = refererUrl.getParameter("group");
            }
        }
        return group;
    }

    private Role getRole(String username) {
        User user = userService.loadUser(username);
        if (user == null) {
            return null;
        }
        return user.getRole();
    }

    private boolean redirectToHomePage(HttpServletRequest request, HttpServletResponse response, String retUrl) {
        try {
            response.sendRedirect(request.getContextPath() + "/home.jsp?back=" + URLEncoder.encode(retUrl, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean recordIfCookieValid(HttpServletRequest request) {
        Map<String, String> cookieParam = getDecodeUserInfoCookie(request);
        if (isCookieValid(cookieParam)) {
            UserContainer.setUserId(Integer.parseInt(cookieParam.get(UID_COOKIE_NAME)));
            UserContainer.setUserName(cookieParam.get(USERNAME_COOKIE_NAME));
            MDC.put(USERNAME_MDC, UserContainer.getUserName());
            return true;
        }
        return false;
    }

    private boolean isCookieValid(Map<String, String> cookieParam) {
        if (Strings.isNullOrEmpty(cookieParam.get(UID_COOKIE_NAME))
                || Strings.isNullOrEmpty(cookieParam.get(USERNAME_COOKIE_NAME))) {
            return false;
        }
        String username = null;
        int uid = INVALID_UID;
        try {
            username = cookieParam.get(USERNAME_COOKIE_NAME);
            uid = Integer.parseInt(cookieParam.get(UID_COOKIE_NAME));

        } catch (NumberFormatException e) {
            logger.warn("Read an invalid uid cookie [{}],assume that the user [{}] may modified it!",
                    cookieParam.get(UID_COOKIE_NAME), cookieParam.get(USERNAME_COOKIE_NAME));
            return false;
        }
        return isValidUsernameAndUid(username, uid);
    }

    private boolean isValidUsernameAndUid(String username, int uid) {
        try {
            User user = userService.loadUser(username);

            if (user == null || uid == UNLOGIN_UID) {
                return false;
            }
            if (uid == INVALID_UID) {
                return false;
            } else {
                if (uid != user.getId()) {
                    logger.warn(
                            "The uid [{}] no matched username [{}] in database,guess that this user may modify cookie's data!",
                            uid, username);
                    return false;
                }
            }

        } catch (Exception e) {
            logger.error("An exception accured when operate database!", e);
        }
        return true;
    }
}
