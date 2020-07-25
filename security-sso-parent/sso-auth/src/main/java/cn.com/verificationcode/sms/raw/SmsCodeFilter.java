package cn.com.verificationcode.sms.raw;

import cn.com.handler.LoginFailureHandler;
import cn.com.verificationcode.img.ValidateCodeController;
import cn.com.verificationcode.img.ValidateCodeException;
import cn.com.verificationcode.sms.common.SmsCodeService;
import cn.com.verificationcode.sms.session.SessionSmsCodeService;
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
public class SmsCodeFilter extends OncePerRequestFilter {
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    private HttpSessionRequestCache sessionRequestCache = new HttpSessionRequestCache();

    @Autowired
    private SmsCodeService smsCodeService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if ("/login/mobile".equalsIgnoreCase(request.getRequestURI())
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
        String parameterCode = smsCodeService.obtainSmsCode();
        SmsCode smsCode;
        // 根据手机号获取验证码
        if ((smsCode = smsCodeService.get()) == null) {
            throw new ValidateCodeException("请重新获取页面验证码");
        }
        // 判断输入的验证码
        if (parameterCode == null)
            throw new ValidateCodeException("输入的验证码不能为空");
        // 是否过期，过期释放session中资源
        if (smsCode.isExpire()) {
            smsCodeService.remove();
            throw new ValidateCodeException("验证码过期");
        }
        if (!smsCode.getCode().equalsIgnoreCase(parameterCode)) {
            throw new ValidateCodeException("验证码输入错误");
        }
        // 成功后释放session 中资源
        if (smsCodeService instanceof SessionSmsCodeService)
            smsCodeService.remove();
    }
}
