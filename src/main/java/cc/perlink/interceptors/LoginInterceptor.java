package cc.perlink.interceptors;

import cc.perlink.enums.exception.UserExceptionEnum;
import cc.perlink.exception.ExceptionMissing;
import cc.perlink.pojo.po.redis.TokenRedisPo;
import cc.perlink.util.JwtUtil;
import cc.perlink.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * @Description: 登录拦截器
 * @Author: htobs
 * @Date: 2024/10/4
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 拦截验证用户令牌
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) { // 对于OPTIONS预检请求，直接放行
            return true;
        }
        // 令牌验证
        String token = request.getHeader("Authorization");
        if (token == null) {
            response.setStatus(401);
            throw new ExceptionMissing(UserExceptionEnum.USER_NOT_LOGGED_IN.getMessage());
        }
        // 解析token
        Map<String, Object> claims;
        try{
             claims = JwtUtil.parseToken(token);
        }catch (Exception e){
            response.setStatus(401);
            throw new ExceptionMissing(UserExceptionEnum.USER_NOT_LOGGED_IN.getMessage());
        }
        // redis查询token
        TokenRedisPo tokenRedisPo = new TokenRedisPo();
        tokenRedisPo.setEmail(claims.get("email").toString());
        String tokenData = stringRedisTemplate.opsForValue().get(tokenRedisPo.getRedisKey());
        if (tokenData == null) {
            response.setStatus(401);
            throw new ExceptionMissing(UserExceptionEnum.USER_NOT_LOGGED_IN.getMessage());
        }
        ThreadLocalUtil.set(claims);
        return true;
    }

    /**
     * 清除ThreadLoad当中的数据
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}

