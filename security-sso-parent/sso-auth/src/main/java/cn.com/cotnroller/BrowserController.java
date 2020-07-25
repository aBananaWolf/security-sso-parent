package cn.com.cotnroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wyl
 * @create 2020-07-11 16:16
 */
@RestController
@RequestMapping("/authorized")
public class BrowserController {
    // spring 基于threadLocal 封装的请求类
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    // Spring Security 提供的用于缓存请求的对象，使用普通的请求只能获得 /authorized/required 的请求信息
    // 使用 HttpSessionRequestCache 可以获取到用户最初的请求信息
    private HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @RequestMapping("/required")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void login() {
        SavedRequest savedRequest = httpSessionRequestCache.getRequest(this.request, response);
        String redirectUrl;
        try {
            // 是否为 vip 页面
            if (savedRequest!= null
                    && (redirectUrl = savedRequest.getRedirectUrl()) != null
                    && redirectUrl.matches(".*(/vip)+.*")) {
                // 充钱页面
                redirectStrategy.sendRedirect(this.request, response, "/vip");
                return;
            }
            // 不是指定页面就跳转
            redirectStrategy.sendRedirect(this.request, response, "/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
