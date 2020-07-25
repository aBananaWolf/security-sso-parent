package cn.com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wyl
 * @create 2020-07-20 17:05
 */
@Component
public class TokenConfig {
//        @Autowired
//        private RedisConnectionFactory redisConnectionFactory;
//        @Bean
//        public TokenStore redisConnectionFactory() {
//            return new RedisTokenStore(redisConnectionFactory);
//        }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1OTUyNTIyODQsIm1lc3NhZ2UiOiJoZWxsbyB3b3JsZCIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6ImUyNzI3ODkyLWQ0ZTEtNGE0Yi04ZmFiLWRkYjNiMTk4M2FkZSIsImNsaWVudF9pZCI6InRlc3QxIn0awlrubdCB0NJgYIvJvXJqARt2FeK9s9vs42ZrUl7I");
        return jwtAccessTokenConverter;
    }

    @Bean
    public JwtTokenStore jwtTokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    public TokenEnhancer tokenEnhancer(JwtAccessTokenConverter jwtAccessTokenConverter) {
        HashMap hashMap = new HashMap<String, String>() {
            {
                put("message", "hello world");
            }
        };
        return new TokenEnhancer() {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(hashMap);
                return accessToken;
            }
        };
    }
}
