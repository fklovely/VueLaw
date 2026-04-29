package com.legal.platform.interceptor;

import com.alibaba.fastjson.JSON;
import com.legal.platform.common.Result;
import com.legal.platform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return unauthorized(response, "请先登录");
        }
        token = token.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return unauthorized(response, "登录已过期，请重新登录");
        }
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        String realName = jwtUtil.getRealName(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        request.setAttribute("realName", realName);
        return true;
    }

    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(JSON.toJSONString(Result.error(401, message)));
        return false;
    }
}
