package cn.com.verificationcode.img;

import cn.com.handler.LoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author wyl
 * @create 2020-07-12 14:21
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/login".equalsIgnoreCase(request.getRequestURI())
                && "post".equalsIgnoreCase(request.getMethod())
        ) {
            try {
                this.verificationCode(request);
            } catch (AuthenticationException e) {
                loginFailureHandler.onAuthenticationFailure(request,response,e);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private void verificationCode(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String imageCode = (String) request.getParameter("imageCode");
        ImageCode sessionImageCode;
        // 判断session 中的验证码
        if (session == null ||
                (sessionImageCode = (ImageCode) session.getAttribute(ValidateCodeController.SESSION_KEY_IMAGE_CODE)) == null) {
            throw new ValidateCodeException("请重新获取页面验证码");
        }
        // 判断输入的验证码
        if (imageCode == null)
            throw new ValidateCodeException("输入的验证码不能为空");
        // 是否过期，过期释放session中资源
        if (sessionImageCode.isExpire()) {
            session.removeAttribute(ValidateCodeController.SESSION_KEY_IMAGE_CODE);
            throw new ValidateCodeException("验证码过期");
        }
        if (!sessionImageCode.getCode().equalsIgnoreCase(imageCode)) {
            throw new ValidateCodeException("验证码输入错误");
        }
        // 成功后释放session 中资源
        session.removeAttribute(ValidateCodeController.SESSION_KEY_IMAGE_CODE);
    }
}
