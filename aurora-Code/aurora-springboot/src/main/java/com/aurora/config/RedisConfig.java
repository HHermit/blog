package com.aurora.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 创建并配置RedisTemplate，用于操作Redis数据库。
     *
     * @param factory Redis连接工厂，用于创建Redis连接。
     * @return 配置好的RedisTemplate实例，可以用于存取Redis中的数据。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(factory);

        // 配置值的序列化方式为JSON，将对象转化为JSON的形式，支持复杂数据结构的存储。注意是值的设置，而不是key的设置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        //ObjectMapper对象Jackson库中的一个类，用于进行Java对象与JSON之间的序列化和反序列化操作。
        ObjectMapper mapper = new ObjectMapper();
        // activateDefaultTyping 启用默认类型推断，以便于反序列化时能正确识别子类型。
        //LaissezFaireSubTypeValidator.instance 作为类型验证器，表示不对子类型进行验证。
        //ObjectMapper.DefaultTyping.NON_FINAL 表示对非最终类进行类型推断。
        //JsonTypeInfo.As.PROPERTY 表示将类型信息作为一个属性包含在JSON对象中。 该函数调用后，ObjectMapper将能够自动推断并序列化/反序列化非最终类的子类型，并在JSON对象中包含类型信息。
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //setObjectMapper之后在使用Jackson2JsonRedisSerializer进行JSON序列化和反序列化时，会使用我们配置的ObjectMapper对象
        jackson2JsonRedisSerializer.setObjectMapper(mapper);

        // 配置键和哈希键的序列化方式为String，保证键的一致性和兼容性。
        //默认的key序列化器为JdkSerializationRedisSerializer，导致我们存到Redis中后的数据和原始数据有差别
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 配置值和哈希值的序列化方式为JSON，以支持复杂数据结构的存储。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 初始化RedisTemplate对象，确保这个对象中自己对redis的所有配置生效。
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}

