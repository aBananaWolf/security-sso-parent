package cn.com.verificationcode.sms.raw;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author wyl
 * @create 2020-07-13 11:11
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public SmsAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken smsAuthenticationToken = (SmsAuthenticationToken)authentication;
        UserDetails userDetails = this.getUserDetailsService().loadUserByUsername((String)smsAuthenticationToken.getPrincipal());
        if (userDetails == null)
            throw new InternalAuthenticationServiceException("未找到与该手机号对应的用户");

        SmsAuthenticationToken smsAuthenticationResult = new SmsAuthenticationToken(userDetails, userDetails.getAuthorities());
        smsAuthenticationResult.setDetails(smsAuthenticationToken.getDetails());
        return smsAuthenticationResult;
    }

    private UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (SmsAuthenticationToken.class
                .isAssignableFrom(authentication));
    }
}
