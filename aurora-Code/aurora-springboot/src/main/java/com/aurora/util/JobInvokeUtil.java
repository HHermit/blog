package com.aurora.util;

import com.aurora.entity.Job;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *  @description:job调用工具类
 *  主要就是在一些参数的基础上通过反射调用实际的方法，通过类名和方法名
*/
public class JobInvokeUtil {

    /**
     * 调用作业中指定的方法。
     * @param job 作业对象，其中包含了待调用的目标方法信息（包括类名、方法名以及参数）。
     */
    public static void invokeMethod(Job job) throws Exception {
        // 获取作业目标方法的完整路径
        String invokeTarget = job.getInvokeTarget();

        // 分解出bean名称（如果是Spring Bean）或类名称
        String beanName = getBeanName(invokeTarget);

        // 分解出方法名称
        String methodName = getMethodName(invokeTarget);

        // 解析并获取方法参数列表
        List<Object[]> methodParams = getMethodParams(invokeTarget);

        // 根据bean名称判断是通过Spring容器获取bean实例还是通过反射（类的全限定名）创建新的类实例
        if (!isValidClassName(beanName)) {
            // 如果是Spring Bean，通过SpringUtil获取已经在spring容器中声明Bean实例
            Object bean = SpringUtil.getBean(beanName);
            invokeMethod(bean, methodName, methodParams);
        } else {
            // 如果不是Spring Bean，通过反射创建类实例
            Object bean = Class.forName(beanName).newInstance();
            invokeMethod(bean, methodName, methodParams);
        }
    }

    /**
     * 根据提供的bean实例、方法名和参数列表，动态调用相应的方法。
     *
     * @param bean 要调用方法的对象实例。
     * @param methodName 方法名称。
     * @param methodParams 方法参数列表，包含参数值及对应的参数类型。
     * @throws NoSuchMethodException 如果在bean类中找不到与methodName对应的方法，则抛出此异常。
     * @throws SecurityException 如果存在安全限制，阻止对方法的访问，则抛出此异常。
     * @throws IllegalAccessException 如果无法访问该方法，则抛出此异常。
     * @throws IllegalArgumentException 如果给定的方法参数不符合要求，则抛出此异常。
     * @throws InvocationTargetException 如果在调用方法期间抛出了异常，则抛出此异常。
     */
    private static void invokeMethod(Object bean, String methodName, List<Object[]> methodParams)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (methodParams != null && methodParams.size() > 0) {
            // 当方法带有参数时，获取其Method对象，并进行调用
            Method method = bean.getClass().getDeclaredMethod(methodName, getMethodParamsType(methodParams));
            //执行方法调用
            method.invoke(bean, getMethodParamsValue(methodParams));
        } else {
            // 当方法无参数时，获取其Method对象并进行调用
            Method method = bean.getClass().getDeclaredMethod(methodName);
            method.invoke(bean);
        }
    }

    /**
     * 检查字符串是否符合Java类名的格式。从而判断是通过Spring IOC bean还是反射进行调用方法
     *
     * @param invokeTarget 待检查的字符串。
     * @return 如果字符串中包含至少两个"."（表示具有包名，则通过反射来调用），则返回true，表明它可能是一个合法的类名；
     *         否则返回false（通过spring 容器中的bean实例来调用）。
     */
    public static boolean isValidClassName(String invokeTarget) {
        return StringUtils.countMatches(invokeTarget, ".") > 1;
    }

    /**
     * 从invokeTarget字符串中提取类名称（或Spring Bean的名称）。
     *
     * @param invokeTarget 完整的调用目标字符串。
     * @return 提取出的类名称或Spring Bean名称。
     */
    public static String getBeanName(String invokeTarget) {
        //截取从开头到第一个出现的左括号 "(" 之前的子字符串  因为有的api调用接口包含参数 eg： com.aurora.quartz.AuroraQuartz.test（“blog”）
        //获得全限定名
        String beanName = StringUtils.substringBefore(invokeTarget, "(");
        //截取从最后一个出现的点号 "." 之前的字符串
        return StringUtils.substringBeforeLast(beanName, ".");
    }

    /**
     * 从invokeTarget字符串中提取方法名称。
     *
     * @param invokeTarget 完整的调用目标字符串。
     * @return 提取出的方法名称。
     */
    public static String getMethodName(String invokeTarget) {
        String methodName = StringUtils.substringBefore(invokeTarget, "(");
        return StringUtils.substringAfterLast(methodName, ".");
    }

    /**
     * 从invokeTarget字符串中解析并获取方法参数列表。
     *
     * @param invokeTarget 包含方法参数信息的字符串。
     * @return 解析后的参数列表，每个元素是一个包含参数值和参数类型的数组。
     */
    public static List<Object[]> getMethodParams(String invokeTarget) {
        String methodStr = StringUtils.substringBetween(invokeTarget, "(", ")");
        if (StringUtils.isEmpty(methodStr)) {
            return null;
        }
        String[] methodParams = methodStr.split(",");

        List<Object[]> classes = new LinkedList<>();

        // 遍历参数字符串，根据字符串形式将其转换为参数值和类型
        for (String methodParam : methodParams) {

            // 去除字符串首尾空格
            String str = StringUtils.trimToEmpty(methodParam);

            //参数类型转换
            if (StringUtils.contains(str, "'")) {
                // 字符串类型参数
                classes.add(new Object[]{StringUtils.replace(str, "'", ""), String.class});
            } else if (StringUtils.equals(str, "true") || StringUtils.equalsIgnoreCase(str, "false")) {
                // 布尔类型参数
                classes.add(new Object[]{Boolean.valueOf(str), Boolean.class});
            } else if (StringUtils.containsIgnoreCase(str, "L")) {
                // 长整型参数
                classes.add(new Object[]{Long.valueOf(StringUtils.replaceIgnoreCase(str, "L", "")), Long.class});
            } else if (StringUtils.containsIgnoreCase(str, "D")) {
                // 双精度浮点型参数
                classes.add(new Object[]{Double.valueOf(StringUtils.replaceIgnoreCase(str, "D", "")), Double.class});
            } else {
                // 整型参数
                classes.add(new Object[]{Integer.valueOf(str), Integer.class});
            }
        }
        return classes;
    }

    /**
     * 根据参数列表获取方法参数类型的数组。
     *
     * @param methodParams 已解析的方法参数列表。
     * @return 包含所有参数类型的Class对象数组。
     */
    public static Class<?>[] getMethodParamsType(List<Object[]> methodParams) {
        Class<?>[] classes = new Class<?>[methodParams.size()];
        int index = 0;
        for (Object[] os : methodParams) {
            //【1】 存储的是参数的类型class
            classes[index] = (Class<?>) os[1];
            index++;
        }
        return classes;
    }

    /**
     * 根据参数列表获取方法参数的值的数组。
     *
     * @param methodParams 已解析的方法参数列表。
     * @return 包含所有参数值的对象数组。
     */
    public static Object[] getMethodParamsValue(List<Object[]> methodParams) {
        Object[] values = new Object[methodParams.size()];
        int index = 0;
        //【0】 存储的是参数的值
        for (Object[] os : methodParams) {
            values[index] = os[0];
            index++;
        }
        return values;
    }
}

