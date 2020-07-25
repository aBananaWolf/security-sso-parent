package cn.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;

/**
 * @author wyl
 * @create 2020-07-21 18:55
 */
@EnableOAuth2Sso
@SpringBootApplication
public class ApplicationTwo {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationTwo.class, args);
    }
}
