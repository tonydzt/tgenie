package com.tgenie.dal.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dzt
 */
@Component
@ConfigurationProperties
class RedisProperties {
	@Value("${spring.redis.pool.max-idle}")
    private int redisPoolMaxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private int redisPoolMinIdle;
    @Value("${spring.redis.pool.max-active}")
    private int redisPoolMaxActive;
    @Value("${spring.redis.pool.max-wait}")
    private int redisPoolMaxWait;
    
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.port}")
    private int redisPort;
    @Value("${spring.redis.timeout}")
    private  int redisTimeout;
    
	int getRedisPoolMaxIdle() {
		return redisPoolMaxIdle;
	}
	int getRedisPoolMinIdle() {
		return redisPoolMinIdle;
	}
	int getRedisPoolMaxWait() {
		return redisPoolMaxWait;
	}
	String getRedisHost() {
		return redisHost;
	}
	int getRedisPort() {
		return redisPort;
	}
	String getPassword() {
		return password;
	}

}
