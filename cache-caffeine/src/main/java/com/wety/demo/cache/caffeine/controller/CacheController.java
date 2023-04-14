package com.wety.demo.cache.caffeine.controller;

import com.wety.demo.cache.caffeine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王宗满
 */
@RestController
@RequestMapping("/cache/")
public class CacheController {

    @Autowired
    private UserService userService;

    @GetMapping("/saveUser")
    public String saveUser(@RequestParam String userId) {
        userService.saveUser(userId);
        return "保存成功";
    }

    @GetMapping("/getUserInfo")
    public String getUserInfo(@RequestParam String userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam String userId) {
        userService.deleteUser(userId);
        return "修改成功";
    }
}
