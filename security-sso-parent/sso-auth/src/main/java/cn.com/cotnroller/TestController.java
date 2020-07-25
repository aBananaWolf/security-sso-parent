package cn.com.cotnroller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author wyl
 * @create 2020-07-11 11:33
 */
@RestController
public class TestController {
    private HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    private ServerProperties serverProperties;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/hello")
    public String test() {
        return "Hello!";
    }

    @GetMapping("/vip")
    public String vip() {
        return "hello! vip";
    }

    @GetMapping("/vip/detail")
    @PreAuthorize("@X.hasAuthority('test')")
//    @PreAuthorize("hasAuthority('admin','test')")
    public String vipDetail(@P("c") User user) {
        return "hello! honoured guest";
    }

    @GetMapping("/parseToken")
    @PreAuthorize("@X.hasAuthority('test')")
    public String parseToken(@AuthenticationPrincipal Authentication authentication,
                             HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").substring(OAuth2AccessToken.BEARER_TYPE.length() + 1);
        String body = Jwts.parserBuilder().setSigningKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1OTUyNTIyODQsIm1lc3NhZ2UiOiJoZWxsbyB3b3JsZCIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6ImUyNzI3ODkyLWQ0ZTEtNGE0Yi04ZmFiLWRkYjNiMTk4M2FkZSIsImNsaWVudF9pZCI6InRlc3QxIn0awlrubdCB0NJgYIvJvXJqARt2FeK9s9vs42ZrUl7I".getBytes(StandardCharsets.UTF_8)).build().parseClaimsJws(authorization).getBody().toString();

        return  "<br/>" + "authenticationPrincipal: " + authentication +
                "<br/>" + "contextPrincipal: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
                "<br/>" + "Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities() +
                "<br/>" + "Details: " + SecurityContextHolder.getContext().getAuthentication().getDetails() +
                "<br/>" + "Details: " + SecurityContextHolder.getContext().getAuthentication().getName() +
                "<br/>" + "Details: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal() +
                "<br/>" + "Details: " + SecurityContextHolder.getContext().getAuthentication().getCredentials()
                // credentials是保护信息
        ;
    }

    @GetMapping("/index")
    public Object index() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/session/invalid")
    public Object sessionInvalid(HttpServletResponse response, HttpServletRequest request) {
        SavedRequest savedRequest = httpSessionRequestCache.getRequest(request, response);
        if (savedRequest == null) {
            System.out.println("保留请求为空");
        } else {
            System.out.println("保留请求不为空" + savedRequest.getRedirectUrl());
        }

        // 五秒钟后跳转
        response.addHeader("reFresh","5;URL=http://localhost:8080" + request.getContextPath() + "/login.html");
        return "登录失效，请登录 \n\n" +  SecurityContextHolder.getContext().getAuthentication() +
                "\n\n<a href='http://localhost:8080" + request.getContextPath() + "/login.html'>跳转</a>";
    }

    @GetMapping("/redis")
    public void redis() {
        stringRedisTemplate.opsForValue().set("a","b");
    }

}
