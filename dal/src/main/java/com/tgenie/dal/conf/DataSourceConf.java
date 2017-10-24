/*
 * Copyright (c) 2016. 版权所有,归北京易精灵科技有限公司.
 */

package com.tgenie.dal.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * DataSourceConfiguration
 *
 * @author Eric
 * @date 16/4/28
 */
@Configuration
public class DataSourceConf {

    @Bean
    public JedisPoolConfig jedisPoolConfig(RedisProperties redisProperties) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisProperties.getRedisPoolMaxIdle());
        poolConfig.setMinIdle(redisProperties.getRedisPoolMinIdle());
        poolConfig.setMaxWaitMillis(redisProperties.getRedisPoolMaxWait());
        return poolConfig;
    }

    @Bean(name = "JCF")
    JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties, JedisPoolConfig config) {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisProperties.getRedisHost());
        factory.setPassword(redisProperties.getPassword());
        factory.setPort(redisProperties.getRedisPort());
        factory.setPoolConfig(config);
        return factory;
    }

    @Bean
    StringRedisTemplate redis(@Qualifier("JCF") JedisConnectionFactory factory) {
        final StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

}
