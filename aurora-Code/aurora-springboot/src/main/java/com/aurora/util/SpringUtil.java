package com.aurora.util;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
@SuppressWarnings("all")
public class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {

    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    /**
    * @Description: 后处理器，在Spring容器初始化完成后执行。可以获取到Spring容器中的所有bean实例。
    * @Param: [beanFactory]
    * @return: void
    */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }

    /**
    * @Description: 在Spring容器初始化完成后，将ApplicationContext应用上下文对象设置到SpringUtil工具类的静态变量中，以便在其他地方可以使用该对象进行Bean的获取和上下文的访问。
    * @Param: [applicationContext]
    * @return: void
    */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
    * @Description: 获取name对应的bean实例
    * @Param: [name]
    * @return: T
    */
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

    /**
    * @Description: 通过参数（所需类的class对象）获取spring容器中的bean实例
    * @Param: [clz]
    * @return: T
    */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return (T) beanFactory.getBean(clz);
    }

    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.isSingleton(name);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getType(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return beanFactory.getAliases(name);
    }

    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
    * @Description: 获取当前环境的激活配置文件列表
    * @Param: []
    * @return: java.lang.String[]
    */
    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return activeProfiles != null && activeProfiles.length > 0 ? activeProfiles[0] : null;
    }
}
