package cn.com.verificationcode.sms.raw;

import cn.com.verificationcode.sms.common.SmsCodeService;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wyl
 * @create 2020-07-13 10:58
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    // ~ Static fields/initializers
    // =====================================================================================

    public static final String SPRING_SECURITY_FORM_MOBILE_KEY = SmsCodeService.PARAMETER_MOBILE_KEY;
    private boolean postOnly = true;

    private String mobileParameter = SPRING_SECURITY_FORM_MOBILE_KEY;

    public SmsAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/mobile", "POST"));
    }

    public SmsAuthenticationFilter(SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        super(new AntPathRequestMatcher("/login/mobile", "POST"));
        super.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String mobile = obtainMobile(request);

        if (mobile == null) {
            mobile = "";
        }

        mobile = mobile.trim();

        SmsAuthenticationToken authRequest = new SmsAuthenticationToken(mobile);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }
    @Nullable
    protected String obtainMobile(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_MOBILE_KEY);
    }

    protected void setDetails(HttpServletRequest request,
                              SmsAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }
    public void setMobile(String mobileParameter) {
        Assert.hasText(mobileParameter, "Password parameter must not be empty or null");
        this.mobileParameter = mobileParameter;
    }
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }
    public final String getMobileParameter() {
        return mobileParameter;
    }
}
