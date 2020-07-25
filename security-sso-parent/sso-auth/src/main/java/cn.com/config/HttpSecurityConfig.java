package cn.com.config;

import cn.com.customizer.HttpSecurityContextCustomizer;
import cn.com.customizer.HttpSecurityCustomizer;
import cn.com.handler.LoginFailureHandler;
import cn.com.handler.LoginSuccessHandler;
import cn.com.handler.MyAccessDeniedHandler;
import cn.com.handler.MyLogoutSuccessHandler;
import cn.com.oauth2.OAuth2HeaderCheckFilter;
import cn.com.oauth2.OAuth2TokenConverterFilter;
import cn.com.service.CommonUserDetailService;
import cn.com.session.MyExpiredSessionStrategy;
import cn.com.verificationcode.img.ValidateCodeFilter;
import cn.com.verificationcode.sms.raw.SmsAuthenticationConfig;
import cn.com.verificationcode.sms.raw.SmsCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.sql.DataSource;

/**
 * @author wyl
 * @create 2020-07-17 11:48
 */
@Configuration
@Order(322)
public class HttpSecurityConfig {
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailureHandler loginFailureHandler;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;
    @Autowired
    private CommonUserDetailService commonUserDetailService;
    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;
    @Autowired
    private SmsCodeFilter smsCodeFilter;
    @Autowired
    private MyExpiredSessionStrategy sessionStrategy;
    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private MyAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private OAuth2TokenConverterFilter tokenConverterFilter;;
    @Autowired
    private UserLoginDaoAuthenticationProviderConfig daoAuthenticationProviderConfig;
    @Autowired
    private OAuth2HeaderCheckFilter oAuth2HeaderCheckFilter;


    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }


    /**
     * {@link SessionManagementConfigurer#init(HttpSecurityBuilder)}
     * 根据SessionManagementConfigurer的配置手动生成控制SecurityContext的 session 仓库
     */
    @Bean
    public HttpSecurityContextCustomizer<HttpSecurity> generateHttpSessionSecurityRepository() {
        return http -> {
            HttpSessionSecurityContextRepository httpSecurityRepository = new HttpSessionSecurityContextRepository();
            httpSecurityRepository.setDisableUrlRewriting(true);
            httpSecurityRepository.setAllowSessionCreation(true);
            AuthenticationTrustResolver trustResolver = http.getSharedObject(AuthenticationTrustResolver.class);
            if (trustResolver != null) {
                httpSecurityRepository.setTrustResolver(trustResolver);
            }
            http.setSharedObject(SecurityContextRepository.class, httpSecurityRepository);
        };
    }

    @Bean
    public HttpSecurityCustomizer<HttpSecurity> httpConsumer(HttpSecurityContextCustomizer<HttpSecurity> generateHttpSessionSecurityRepository) {
        return new HttpSecurityCustomizer<HttpSecurity>() {
            @Override
            public void accept(HttpSecurity http) throws Exception {
                generateHttpSessionSecurityRepository.accept(http);

                // 这个方法允许在已知的Filter 之前添加新 Filter
                http
                        .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class) // 短信验证码校验
                        .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 图片验证码
                        // 本应该在添加在OAuth2AuthenticationProcessingFilter之后，但是这个类是靠后面才装配的，目前HttpSecurity获取不到
                                                .addFilterAfter(tokenConverterFilter.setSecurityContextRepository(
                                                        http.getSharedObject(SecurityContextRepository.class)), HeaderWriterFilter.class) // 将自定义的cookie 转换为符合 oauth2标准的 header
                        .addFilterBefore(oAuth2HeaderCheckFilter, SessionManagementFilter.class)
                        .formLogin(
                                formLogin -> formLogin
                                        .loginPage("/authorized/required")
                                        .loginProcessingUrl("/login") // /login/mobile 是短信验证码
                                        .successHandler(loginSuccessHandler)
                                        .failureHandler(loginFailureHandler)
                        )

                        .sessionManagement( // session 管理 ConcurrentSessionFilter
                                sessionManagement -> sessionManagement
                                        .invalidSessionUrl("/session/invalid") // session失效跳转路径
                                        .maximumSessions(10) // 最大允许一个session
                                        // .maxSessionsPreventsLogin(true) // 并发控制策略：1.阻止后续登录(2.默认允许多端登录，会造成会话较老的一方下线)
                                        .expiredSessionStrategy(sessionStrategy) // 在ConcurrentSessionFilter中检测到session 过期后的行为，在2.中会触发
                        )
                        .logout( // 登出配置
                                logout -> logout
                                        .logoutUrl("/signOut") // 登出路径
                                        .logoutSuccessHandler(logoutSuccessHandler) // 登出成功逻辑
                                        .deleteCookies("JSESSIONID") // 删除session
                        )
                        .authorizeRequests( // 授权配置
                                authorizeRequests -> authorizeRequests
                                        .antMatchers(
                                                "/login.html", "/login2.html", "/authorized/required",
                                                "/vip", "/code/**", "/session/invalid", "/oauth/**")
                                        .permitAll()
                                        .anyRequest()  // 所有请求
                                        .authenticated() // 都需要认证
                        )

                        .exceptionHandling( // 异常控制
                                exceptionHandling -> exceptionHandling
                                        .accessDeniedHandler(accessDeniedHandler)
                                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login.html"))

                        )

                        .rememberMe( // 记住我功能
                                rememberMe -> rememberMe
                                        .tokenRepository(persistentTokenRepository) // 配置token 持久化仓库
                                        .tokenValiditySeconds(60 * 60 * 10) // 过期时间
                                        .userDetailsService(commonUserDetailService) // 当记住我令牌生效时的登录逻辑
                        )

                        // 注意关闭csrf防御，否则在进入UsernamePasswordAuthenticationFilter
                        // 授权之前，代码内部就会报错，导致无法继续执行过滤链
                        // 问题是实际上我并没有跨域也会出现这个问题
                        .csrf(
                                csrf -> csrf.disable()
                        )

                        // 短信验证码登录逻辑
                        .apply(smsAuthenticationConfig)
                        .and()
                        // 添加daoAuthenticationProvider,用于密码模式获取token和用户名密码登录
                        .apply(daoAuthenticationProviderConfig);
                ;
            }
        };
    }
}
