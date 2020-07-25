package cn.com.customizer;

import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * @author wyl
 * @create 2020-07-17 10:52
 */
public interface HttpSecurityContextCustomizer<H extends AbstractConfiguredSecurityBuilder<DefaultSecurityFilterChain, HttpSecurity>> {

    public void accept(HttpSecurity http) throws Exception ;

}
