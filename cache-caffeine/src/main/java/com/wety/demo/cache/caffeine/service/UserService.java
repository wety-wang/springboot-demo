package com.wety.demo.cache.caffeine.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author wety
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
    * 使用Spring Cache注解，缓存getUserInfo方法的结果
    * 使用value属性指定缓存名称，使用key属性指定缓存的key
    */
    @Cacheable(value = "userInfo", condition = "#userId != '123'", unless = "#result == null || #result == ''", key = "#userId")
    public String getUserInfo(String userId) {
        log.info("userId:{}", userId);
        if(StringUtils.isEmpty(userId)){
            return null;
        }
        try {
            Thread.sleep(5000L);
        } catch (Exception e) {
            log.error("error: ", e);
        }
        return userId;
    }
}
