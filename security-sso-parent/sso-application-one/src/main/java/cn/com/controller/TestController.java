package cn.com.controller;

import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/one")
public class TestController {
    @RequestMapping("vip")
    @PreAuthorize("@X.hasAuthority('test')")
    public Object vip(@AuthenticationPrincipal Authentication auth) {
        return "欢迎登录！ " + SecurityContextHolder.getContext().getAuthentication().getName() +
                "</br> @AuthenticationPrincipal"  + auth +
                "</br> SecurityContextAuthentication" + SecurityContextHolder.getContext().getAuthentication();
    }


    @RequestMapping("/deny")
    @PreAuthorize("hasAuthority('xxxx')")
    public Object denyTest() {
        return "拥有权限?";
    }
}
