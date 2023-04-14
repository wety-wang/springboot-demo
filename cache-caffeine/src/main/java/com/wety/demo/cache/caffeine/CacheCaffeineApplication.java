package com.wety.demo.cache.caffeine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author wety
 */
@SpringBootApplication
// 开启缓存
@EnableCaching
public class CacheCaffeineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CacheCaffeineApplication.class, args);
    }

}
