package cn.com.oauth2;

import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wyl
 * @create 2020-07-17 20:13
 */

@Component
public class OAuth2TokenConverterFilter extends OncePerRequestFilter {
    // 自定义的cookie，存放OAuth2Token
    public static String OAUTH2_TOKEN_KEY = "OAUTH2_TOKEN";
    // 操作SecurityContext的类
    private SecurityContextRepository securityContextRepository;

    public OAuth2TokenConverterFilter setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
        return this;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies;
        Cookie oAuth2Token;
        String tokenValue;
        if ((cookies = request.getCookies()) != null
                && (oAuth2Token = this.getOAuth2Token(cookies)) != null
                && (tokenValue = oAuth2Token.getValue()) != null
        ) {
            // 包装一层，当重定向的时候往session中添加授权信息
            response = new OnCommitResponseSecurityContextWrapper(response, securityContextRepository, request);
            // 包装一层，默认的HttpServletRequest是没有添加请求头功能的
            MyHttpServletRequestWrapper headers = new MyHttpServletRequestWrapper(request);
            if (headers.getHeader("Authorization") == null) // 如果是授权请求就免了
                headers.putHeader("Authorization","Bearer " + tokenValue);
            request = headers;
        }
        this.doFilter(request, response, filterChain);
    }

    private Cookie getOAuth2Token(Cookie[] cookies) {
        for (int i = 0; i < cookies.length; i++) {
            if (OAUTH2_TOKEN_KEY.equals(cookies[i].getName()))
                return cookies[i];
        }
        return null;
    }
}
