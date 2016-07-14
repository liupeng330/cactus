package com.qunar.corp.cactus.web.controller;

import com.qunar.corp.cactus.bean.Role;
import com.qunar.corp.cactus.bean.User;
import com.qunar.corp.cactus.service.UserService;
import com.qunar.corp.cactus.util.ConstantHelper;
import com.qunar.security.QSSO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;

import static com.qunar.corp.cactus.util.ConstantHelper.INVALID_UID;
import static com.qunar.corp.cactus.web.CookieHelper.removeUserInfoCookie;
import static com.qunar.corp.cactus.web.CookieHelper.encodeAndWriteUserInfoCookie;

/**
 * @Description:
 * @Author: lian.jin
 * @see:
 * @Date: 13-8-14 09:54
 * */
@Controller
public class LoginController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public String welcomPage(@RequestParam(value = "back", required = false) String back, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        String username = null;
        if (token == null) {
            return "redirect:home.jsp";
        }
        try {
            username = QSSO.getUser(token);
        } catch (Exception e) {
            logger.error("An exception caught when getting info from QSSO!" + e);
            return "redirect:home.jsp";
        }
        try {
            User user = userService.loadUser(username);
            if (user == null) {
                user = new User();
                user.setUsername(username);
                user.setRole(Role.USER);
                userService.add(user);
                user = userService.loadUser(username);
            }
            encodeAndWriteUserInfoCookie(response, username, String.valueOf(user.getId()));
        } catch (Exception e) {
            encodeAndWriteUserInfoCookie(response, username, String.valueOf(INVALID_UID));
            logger.error("An exception accured when operate database!", e);
        }

        if (back != null) {
            back = URLDecoder.decode(back, "UTF-8");
            back = back.replace(ConstantHelper.BACK_URL_PARAM_SPLITTER, "&");
            return "redirect:" + back;
        }
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(@RequestParam(value = "ret", required = false) String ret, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        removeUserInfoCookie(response);
        return "redirect:home.jsp";
    }
}
