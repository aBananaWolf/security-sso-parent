package cn.com.config;

import cn.com.customizer.HttpSecurityCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author wyl
 * @create 2020-07-22 10:32
 */
@Configuration
public class HttpSecurityConfig {

    @Bean
    public HttpSecurityCustomizer<HttpSecurity> httpSecurityCustomizer() {
        return new HttpSecurityCustomizer<HttpSecurity>() {
            @Override
            public void accept(HttpSecurity http) throws Exception {
                http.formLogin()
                        .loginProcessingUrl("/login")

                        .and()
                        .csrf().disable();
            }
        };
    }
}
