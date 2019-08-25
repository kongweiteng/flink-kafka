package flink.redis;

import flink.PropertUtil;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

public class RedisConfig {

    public RedisTemplate redis;

    public RedisConfig(RedisTemplate redis) {
        this.redis = redis;
    }

    public RedisConfig() {
        //StringRedisTemplate的构造方法中默认设置了stringSerializer
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        //设置开启事务
        //redisTemplate.setEnableTransactionSupport(true);
        //set key serializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(connectionFactory());

        redisTemplate.afterPropertiesSet();
        redis=redisTemplate;

    }

    public RedisConnectionFactory connectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(maxActive);
//        poolConfig.setMaxIdle(maxIdle);
//        poolConfig.setMaxWaitMillis(maxWait);
//        poolConfig.setMinIdle(minIdle);
//        poolConfig.setTestOnBorrow(true);
//        poolConfig.setTestOnReturn(false);
//        poolConfig.setTestWhileIdle(true);
        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling().poolConfig(poolConfig).and().readTimeout(Duration.ofMillis(3000)).build();

        // 单点redis
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        // 哨兵redis
        // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
        // 集群redis
        // RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
        redisConfig.setHostName(PropertUtil.getPropertValue("redis.host"));
        redisConfig.setPassword(RedisPassword.of(PropertUtil.getPropertValue("redis.auth")));
        redisConfig.setPort(Integer.parseInt(PropertUtil.getPropertValue("redis.port")));
        //暂时不指定database
//        redisConfig.setDatabase(1);

        return new JedisConnectionFactory(redisConfig,clientConfig);
    }

}
