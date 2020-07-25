package cn.com.config;

import cn.com.service.CommonUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

/**
 * @author wyl
 * @create 2020-07-16 10:36
 */
@Order(315)
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerEnableConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CommonUserDetailService commonUserDetailService;
    @Resource
    private TokenStore tokenStore;
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private TokenEnhancer tokenEnhancer;

    /**
     * {@link AuthorizationServerConfigurerAdapter#configure(AuthorizationServerEndpointsConfigurer)} 的优先级较高，它需要一个加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        ArrayList<TokenEnhancer> material = new ArrayList<>(2);
        // 注意顺序
        material.add(tokenEnhancer);
        material.add(jwtAccessTokenConverter);
        tokenEnhancerChain.setTokenEnhancers(material);

        endpoints.userDetailsService(commonUserDetailService);
        endpoints.tokenStore(tokenStore);
        endpoints.accessTokenConverter(jwtAccessTokenConverter);
        endpoints.tokenEnhancer(tokenEnhancerChain);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("isAuthenticated()");

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        String passowrd1;
        String passowrd2;
        // 注意要使用加密后的盐(否则授权的时候会有 Encoded password does not look like BCrypt 的告警)
        System.out.println("test1 Authorization");
        System.out.println("test1:" + (passowrd1 = passwordEncoder.encode("test1111")));
        // dGVzdDE6dGVzdDExMTE=
        System.out.println(new String(Base64.getEncoder().encode("test1:test1111".getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8));
        System.out.println("test2 Authorization");
        System.out.println("test2:" + (passowrd2 = passwordEncoder.encode("test2222")));
        // dGVzdDI6dGVzdDIyMjI=
        System.out.println(new String(Base64.getEncoder().encode("test2:test2222".getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8));
        clients.inMemory()
                .withClient("test1")
                .secret(passowrd1)
                // 必须指定scope
                .scopes("all", "a", "b", "c")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(864000)
                // 必须指定授权类型,可填入多个，sso 默认通过 authorization_code 模式授权
                .authorizedGrantTypes("password", "refresh_token", "authorization_code")
                // 需要注册一下授权码模式的重定向url，否则会报错，受保护的服务默认使用/login作为重定向url
                .redirectUris("http://localhost:8081/one/vip","http://localhost:8081/login")
                // 受保护的服务，每次跨服务都需要授权一次，是的，每次，所以我们要开启自动确认
                .autoApprove(true)

                .and()
                .withClient("test2")
                .secret(passowrd2)
                .authorizedGrantTypes("authorization_code")
                .scopes("all")
                .accessTokenValiditySeconds(7200)
                .redirectUris("http://localhost:8082/login")
                .autoApprove(true);

    }
}
