package com.card.security;

import com.card.security.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

@Slf4j
public class NoSessionFilter extends BasicHttpAuthenticationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        // 1.从Cookie获取token
        String token = getTokenFromCookie(servletRequest);
        if (StringUtils.isBlank(token)) {
            // 2.从headers中获取
            token = servletRequest.getHeader("Authorization");
        }
        if (StringUtils.isBlank(token)) {
            // 3.从请求参数获取
            token = request.getParameter("Authorization");
        }
        if (StringUtils.isBlank(token)) {
            return false;
        }
        // 验证token
        JwtUtil jwtUtil = new JwtUtil();
        return jwtUtil.validateJWT(token);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        PrintWriter printWriter = response.getWriter();
        response.setCharacterEncoding("utf-8");
        printWriter.write("403");
        printWriter.flush();
        printWriter.close();
        return false;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        int len = null == cookies ? 0 : cookies.length;
        if (len > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Authorization")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }
}
