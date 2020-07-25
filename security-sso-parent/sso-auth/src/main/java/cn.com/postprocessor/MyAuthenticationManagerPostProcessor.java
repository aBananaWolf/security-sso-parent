package cn.com.postprocessor;

import cn.com.config.MyAuthenticationManagerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;

/**
 * {@link MyAuthenticationManagerConfig}
 */
public class MyAuthenticationManagerPostProcessor implements BeanPostProcessor {
    private DefaultListableBeanFactory beanFactory;

    // 编程注册
    public MyAuthenticationManagerPostProcessor(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanFactory != null) {
            if (bean instanceof AuthorizationServerEndpointsConfiguration) {
                // 依赖查找
                AuthenticationManager manager = beanFactory.getBean(AuthenticationManager.class);
                beanFactory.getBean(AuthorizationServerEndpointsConfiguration.class);
                AuthorizationServerEndpointsConfiguration authorizationServerEndpointsConfiguration = (AuthorizationServerEndpointsConfiguration) bean;
                authorizationServerEndpointsConfiguration.getEndpointsConfigurer().authenticationManager(manager);
                return authorizationServerEndpointsConfiguration;
            }
        }
        return bean;
    }
}
