package cn.com.handler;

import cn.com.global.Environment;
import cn.com.oauth2.OAuth2TokenConverterFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;


/**
 * 自定义成功处理器获取token
 */
@Component
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    // 获取最初的请求
    private HttpSessionRequestCache httpSessionRequestCache = new HttpSessionRequestCache();
    // 重定向工具类
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    // 可直接生成token的OAuth2
    private AuthorizationServerTokenServices authorizationServerTokenServices;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Environment environment;
    @Autowired
    private ServerProperties serverProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 按照oauth2 标准来说，请求头要带上clientId 和 clientSecret(只有获取授权码的时候不需要这些信息，所以需要判断)
        if (exactOauth2AuthorizationMakeToken(request, response, authentication)
                || exactHeaderMakeToken(request, response, authentication)) {
            // 授权码请求
        } else  {
            throw new UnapprovedClientAuthenticationException("无clientId 信息");
        }
    }

    private void addToken(HttpServletResponse response, OAuth2AccessToken token) {
        // 自定义的token(因为资源服务器和授权服务器都在一个服务上), 再制作一个自己的过滤器来将token 转换为请求头
        Cookie cookie = new Cookie(OAuth2TokenConverterFilter.OAUTH2_TOKEN_KEY, token.getValue());
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }

    /**
     * 判断最初请求是否为授权请求(获取授权码要先登录),是则颁发一个本系统的所有权token(虽然这个系统里没有...)
     */
    private boolean exactOauth2AuthorizationMakeToken(HttpServletRequest request, HttpServletResponse response,
                                                           Authentication authentication) throws IOException {
        OAuth2AccessToken oAuth2AccessToken;
        SavedRequest savedRequest = httpSessionRequestCache.getRequest(request, response);
        String initialRequestURL;
        String[] array;
        if (savedRequest != null
                && (initialRequestURL = savedRequest.getRedirectUrl()) != null
                && initialRequestURL.matches(".*(oauth)+.*")
                // 判断授权码请求
                && ArrayUtils.isNotEmpty((array = savedRequest.getParameterValues(OAuth2Utils.RESPONSE_TYPE)))
                && "code".equals(array[0])
        ) {
            // 1. 从请求中获取 ClientId
            String[] parameterValues = savedRequest.getParameterValues(OAuth2Utils.CLIENT_ID);
            if (ArrayUtils.isEmpty(parameterValues)) {
                throw new UnapprovedClientAuthenticationException("授权请求无" + OAuth2Utils.CLIENT_ID + "信息");
            }

            // 2. 通过 ClientDetailsService 获取 ClientDetails
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(parameterValues[0]);

            // 3. 校验 ClientId和 ClientSecret的正确性
            if (clientDetails == null) {
                throw new UnapprovedClientAuthenticationException("clientId:" + parameterValues[0] + "对应的信息不存在");
            }
            // 制作token
            oAuth2AccessToken = this.makeToken(authentication, parameterValues[0], clientDetails);

            // adding a token to customize the cookie is a critical step, The order of redirectUrl and adding cookie should not be disordered
            addToken(response, oAuth2AccessToken);

//            // 8. 响应信息
//            response.setContentType("application/json;charset=UTF-8");

            // 跳转到授权页面
            response.sendRedirect(initialRequestURL);

            return true;
        }
        return false;
    }

    /**
     * 根据请求头获取token
     */
    private boolean exactHeaderMakeToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 1. 从请求头中获取 ClientId
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic")) {
            // 方便测试用的环境，一般测试的情况下没有clientId(Oauth2标准要求你携带这个请求头，符合标准，普通登录可以再追加判断)
            if (environment.isEnableAlternateClientId()) {
                header = "Basic " + new String(Base64.getEncoder().encode("test1:test1111".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                log.warn("enable security customizer alternate ClientId");
            }
            else return false;
        }

        String[] tokens = this.extractAndDecodeHeader(header, request);
        String clientId = tokens[0];
        String clientSecret = tokens[1];

        // 2. 通过 ClientDetailsService 获取 ClientDetails
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

        // 3. 校验 ClientId和 ClientSecret的正确性
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId:" + clientId + "对应的信息不存在");
            // 要注意，这里的比较器左边的是"原始密码"
        } else if (!passwordEncoder.matches(clientSecret,clientDetails.getClientSecret())){
            throw new UnapprovedClientAuthenticationException("clientSecret不正确");
        }
        // 制作token
        OAuth2AccessToken oAuth2AccessToken = makeToken(authentication, clientId, clientDetails);

        // adding a token to customize the cookie is a critical step, The order of redirectUrl and adding cookie should not be disordered
        addToken(response, oAuth2AccessToken);

        // 8. 响应信息
        // 如果授权服务器和资源服务器都在同一个服务中，这里的重定向不会将登录信息的Authentication添加到session当中
        // 那么我们需要自定义一个过滤器来包装请求，过滤器需要干两件事
        // 1.将cookie转换为OAuth2标准的请求头
        // 2.将Authentication登录信息添加进session
        // SecurityContextPersistenceFilter过滤器在过滤链中排名第四，当再次请求的时候，其过滤器会将session提取到
        // SecurityContextHolder 中
        response.sendRedirect(request.getContextPath() + "/index");
        return true;
    }

    /**
     * 制作token
     */
    private OAuth2AccessToken makeToken(Authentication authentication, String clientId, ClientDetails clientDetails) {
        // 4. 通过 TokenRequest构造器生成 TokenRequest
        TokenRequest tokenRequest = new TokenRequest(new HashMap<>(),
                clientId, clientDetails.getScope(), "custom");

        // 5. 通过 TokenRequest的 createOAuth2Request方法获取 OAuth2Request
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
        // 6. 通过 Authentication和 OAuth2Request构造出 OAuth2Authentication
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        // 7. 通过 AuthorizationServerTokenServices 生成 OAuth2AccessToken
        return authorizationServerTokenServices.createAccessToken(auth2Authentication);
    }

    /**
     * 转换请求头base64信息
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request) {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new BadCredentialsException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        } else {
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
    }
}
