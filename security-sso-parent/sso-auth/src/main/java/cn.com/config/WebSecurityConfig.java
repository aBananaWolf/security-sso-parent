package cn.com.config;

import cn.com.customizer.HttpSecurityCustomizer;
import cn.com.global.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * @author wyl
 * @create 2020-07-11 11:37
 */
@Order(321)
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(Environment.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private HttpSecurityCustomizer<AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>> httpConsumer;

    // 获取认证管理器
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        httpConsumer.accept(http);
    }
}
