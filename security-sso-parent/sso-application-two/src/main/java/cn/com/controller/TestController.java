package cn.com.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wyl
 * @create 2020-07-22 10:48
 */
@RestController
@RequestMapping("/two")
public class TestController {
    @RequestMapping("vip")
    public Object vip(@AuthenticationPrincipal Authentication authentication) {
        return "欢迎登录！ " + SecurityContextHolder.getContext().getAuthentication().getName() +
                "\n\n"  + authentication;
    }
}
