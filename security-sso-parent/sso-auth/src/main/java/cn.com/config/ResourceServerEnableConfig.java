package cn.com.config;

import cn.com.customizer.HttpSecurityCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * @author wyl
 * @create 2020-07-16 14:53
 */
@Order(320)
@Configuration
@EnableResourceServer
public class ResourceServerEnableConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private HttpSecurityCustomizer<AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>> httpConsumer;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        httpConsumer.accept(http);
    }
}
