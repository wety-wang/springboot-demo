package com.wety.demo.reatlimit.limit;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.wety.demo.reatlimit.annotation.Limit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

/**
 * AOP切面拦截限流注解
 *
 * @author wety
 */
@Aspect
@Component
public class LimitAop {

    private static final Logger log = LoggerFactory.getLogger(LimitAop.class);

    /**
     * 不同的接口，不同的流量控制
     * map的key为 Limiter.key
     */
    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    /**
     * 切面环绕通知<br>
     * 拦截带有@Limit注解的方法
     *
     * @throws Throwable 异常
     */
    @Around("@annotation(com.wety.demo.reatlimit.annotation.Limit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //拿limit的注解
        Limit limit = method.getAnnotation(Limit.class);
        if (limit == null) {
            return joinPoint.proceed();
        }

        String key = getLimitKey(limit.key());
        RateLimiter rateLimiter = getRateLimiter(key, limit.permitsPerSecond());

        // 获取令牌
        boolean acquire = rateLimiter.tryAcquire(limit.timeout(), limit.timeunit());
        if (!acquire) {
            log.warn("令牌桶={}，获取令牌失败", key);
            this.responseFail(limit.msg());
            return null;
        }
        return joinPoint.proceed();
    }

    /**
     * 获取限流的key，当传入的key为空的时候，使用当前请求的uri
     *
     * @param key 限流的key
     *
     * @return String 限流的key
     */
    private static String getLimitKey(String key) {
        // 当key为空的时候默认为当前uri
        if(StringUtils.isNotEmpty(key)) {
            return key;
        }
        // 获取当前方法的url
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getRequestURI();
    }

    /**
     * 获取令牌桶，如果缓存中没有命中，则新建一个令牌桶
     *
     * @param key 令牌桶的key
     * @param permitsPerSecond 每秒生成的令牌数
     *
     * @return RateLimiter
     */
    private RateLimiter getRateLimiter(String key, double permitsPerSecond) {
        // 验证缓存是否有命中key
        return limitMap.computeIfAbsent(key, k -> {
            log.info("新建了令牌桶={}，容量={}", key, permitsPerSecond);
            return RateLimiter.create(permitsPerSecond);
        });
    }

    /**
     * 服务降级, 直接向前端抛出异常
     *
     * @param msg 提示信息
     */
    private void responseFail(String msg) throws IOException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(msg);
        }
    }

}
