package com.wety.demo.cache.caffeine.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author wety
 */
@Service
// 可以在类上使用{@CacheConfig}注解，指定缓存的名称。
// 也可以在方法上使用{@Cacheable}、{@CachePut}、{@CacheEvict}注解的Value属性指定缓存的名称
@CacheConfig(cacheNames = {"userInfo"})
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * {@CachePut}注解，使用该注解标注的方法，会在方法执行后，将方法的返回值缓存到指定的缓存中
     * <p>
     * 常用于新增或修改方法
     */
    @CachePut(key = "#userId")
    public void saveUser(String userId) {
        log.info("保存用户，userId:{}", userId);
    }

    /**
     * {@Cacheable}注解，使用该注解标注的方法，会在方法执行前，先从指定的缓存中获取数据，
     * 如果获取到了，就不会执行该方法，如果没有获取到，就会执行该方法，并将方法的返回值缓存到指定的缓存中
     * <p>
     * condition：指定符合条件的情况下才缓存<br>
     * unless：否定缓存，当unless指定的条件为true，方法的返回值就不会被缓存，<strong>可以根据结果进行判断</strong><br>
     * key：缓存数据时使用的key，可以用它来指定。<br>
     * <p>
     * 常用于查询方法
     * <p><strong>注意：如果是一个方法A调同一个类里的另一个有缓存注解的方法B，这样是不走缓存的。</strong></p>
     */
    @Cacheable(condition = "#userId != '123'", unless = "#result == null || #result == ''", key = "#userId")
    public String getUserInfo(String userId) {
        log.info("查询用户，userId:{}", userId);
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("error: ", e);
        }
        return userId;
    }

    /**
     * {@CacheEvict}注解，使用该注解标注的方法，会在方法执行后，从指定的缓存中删除数据
     * <p>
     * 常用于删除方法
     */
    @CacheEvict(key = "#userId")
    public void deleteUser(String userId) {
        log.info("删除用户，userId:{}", userId);
    }
}
