package cn.com.verificationcode.sms.raw;

import cn.com.handler.LoginFailureHandler;
import cn.com.handler.LoginSuccessHandler;
import cn.com.service.CommonUserDetailService;
import cn.com.verificationcode.sms.common.SmsCodeDetailService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

/**
 * @author wyl
 * @create 2020-07-13 11:58
 */
@Component
public class SmsAuthenticationConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain,HttpSecurity> {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private SmsCodeDetailService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter(sessionAuthenticationStrategy);
        smsAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        smsAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler);
        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider(userDetailsService);

        http.authenticationProvider(smsAuthenticationProvider)
                .addFilterBefore(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
