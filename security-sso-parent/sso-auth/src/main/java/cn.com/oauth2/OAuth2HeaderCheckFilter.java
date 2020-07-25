package cn.com.oauth2;

import org.apache.catalina.connector.Request;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wyl
 * @create 2020-07-23 16:46
 */
@Component
public class OAuth2HeaderCheckFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI()!= null
                && request.getRequestURI().startsWith(request.getContextPath() + "/oauth/authorize")) {
            // 绕开检测
            if (request.getRequestedSessionId() != null
                    && !request.isRequestedSessionIdValid()) {
                request = new ConverterValidSessionRequestWrapper(request);
            }
        }
        filterChain.doFilter(request,response);
    }
}
