package com.wety.demo.reatlimit.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.wety.demo.reatlimit.annotation.Limit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author wety
 */
@RestController
@RequestMapping("/limit/")
public class LimitController {

    private static final Logger log = LoggerFactory.getLogger(LimitController.class);

    private final RateLimiter limiter = RateLimiter.create(1, 1, TimeUnit.SECONDS);

    @GetMapping("/test1")
    public String limit1() {
        // 500毫秒内，没拿到令牌，就直接进入服务降级
        boolean tryAcquire = limiter.tryAcquire(500, TimeUnit.MILLISECONDS);

        if (!tryAcquire) {
            log.warn("进入服务降级");
            return "当前排队人数较多，请稍后再试！";
        }

        log.info("limit1获取令牌成功");
        return "请求成功";
    }

    @GetMapping("/test2")
    @Limit(key = "limit2", permitsPerSecond = 1, timeout = 0, msg = "当前排队人数较多，请稍后再试！")
    public String limit2() throws InterruptedException {
        log.info("limit2获取令牌成功");
        Thread.sleep(500);
        return "请求成功";
    }

    @GetMapping("/test3")
    @Limit(key = "limit3", permitsPerSecond = 2, timeout = 0, msg = "系统繁忙，请稍后再试！")
    public String limit3() throws InterruptedException {
        log.info("limit3获取令牌成功");
        Thread.sleep(500);
        return "请求成功";
    }
}
