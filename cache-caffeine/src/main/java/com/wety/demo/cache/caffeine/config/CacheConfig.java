package com.wety.demo.cache.caffeine.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * caffeine缓存配置
 *
 * @author wety
 */
@Configuration
public class CacheConfig {

    /**
     * 必须要指定这个Bean，refreshAfterWrite=5s这个配置属性才生效
     *
     * @return
     */
    @Bean
    public CacheLoader<Object, Object> cacheLoader() {

        return new CacheLoader<Object, Object>() {

            @Override
            public Object load(Object key) {
                return null;
            }

            @Override
            public Object reload(Object key, Object oldValue) {
                return oldValue;
            }
        };
    }

}
