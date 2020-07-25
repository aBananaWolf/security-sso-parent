package cn.com.config;

import cn.com.postprocessor.MyAuthenticationManagerPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 资源服务器和授权服务器混合后, authenticationManager无法在授权服务的配置上注册
 * 这个配置能够提前加载authenticationManager, 将参数注入给授权服务器 {@link AuthorizationServerConfigurerAdapter#configure(org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer)}
 * 后置处理器 {@link MyAuthenticationManagerPostProcessor}
 *
 */
@Component
@Import(MyAuthenticationManagerConfig.PreLoadAuthenticationManager.class)
public class MyAuthenticationManagerConfig {

    public static class PreLoadAuthenticationManager implements ImportBeanDefinitionRegistrar , BeanFactoryAware {
        private DefaultListableBeanFactory beanFactory;
        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = (DefaultListableBeanFactory)beanFactory;
        }
        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
            if (beanFactory != null) {
                // 注册AuthenticationManagerPostProcessor
                registerSyntheticBeanIfMissing(registry,
                        "MyAuthenticationManagerPostProcessor", MyAuthenticationManagerPostProcessor.class);
            }
        }
        private void registerSyntheticBeanIfMissing(BeanDefinitionRegistry registry, String name, Class<?> beanClass) {
            // 确保没有其它的AuthenticationManagerPostProcessor
            if (ObjectUtils.isEmpty(this.beanFactory.getBeanNamesForType(beanClass, true, false))) {
                RootBeanDefinition beanDefinition = new RootBeanDefinition(beanClass);
                beanDefinition.setSynthetic(true);
                registry.registerBeanDefinition(name, beanDefinition);
            }
        }
    }
}
