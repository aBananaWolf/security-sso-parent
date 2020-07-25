package cn.com.global;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author wyl
 * @create 2020-07-21 14:36
 */
@ConfigurationProperties("security.customizer")
@Data
public class Environment{
    private boolean enableAlternateClientId;

}
