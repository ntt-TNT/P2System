package com.example.demo.util;

import com.auth0.jwt.JWT;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "*");
        response.setHeader("Access-Control-Request-Headers",
                "*");
        String method=request.getMethod();
        if(method.equals("OPTIONS"))
        {
            response.setStatus(200);
            return false;
        }
        String token = request.getHeader("authorization");//获取请求头中的令牌
        if(JwtUtil.checkSign(token))
        //已登录，放行
        return true;
        else
            return false;
    }

}
