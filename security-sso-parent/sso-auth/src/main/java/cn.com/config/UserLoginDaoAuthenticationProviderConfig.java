package cn.com.config;

import cn.com.service.CommonUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.stereotype.Component;

/**
 * 解决没有password授权模式问题,根据源码可以发现没有支持的authenticationProvider
 * {@link SecurityConfigurerAdapter} 它是httpSecurity 的构造器
 */
@Component
@Order(323)
public class UserLoginDaoAuthenticationProviderConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonUserDetailService commonUserDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(commonUserDetailService);

        http.authenticationProvider(daoAuthenticationProvider);
    }
}
