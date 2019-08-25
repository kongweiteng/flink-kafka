package flink;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileNotFoundException;
import java.io.IOException;

public class JedisPoolUtil {

    private static JedisPool jedisPool;

    static {
        if (jedisPool == null) {
            try {
                init();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private static void init() throws IOException {
        //开始配置JedisPool
        String host = (PropertUtil.getPropertValue("redis.host").trim() == null || "".equals(PropertUtil.getPropertValue("redis.host").trim())) ? "localhost" : PropertUtil.getPropertValue("redis.host");
        int port = (PropertUtil.getPropertValue("redis.port").trim() == null || "".equals(PropertUtil.getPropertValue("redis.port").trim())) ? 6379 : Integer.parseInt(PropertUtil.getPropertValue("redis.port"));
        String auth = PropertUtil.getPropertValue("redis.auth").trim();
        int poolTimeOut = (PropertUtil.getPropertValue("connectionTimeOut").trim() == null || "".equals(PropertUtil.getPropertValue("connectionTimeOut").trim())) ? 2000 : Integer.parseInt(PropertUtil.getPropertValue("connectionTimeOut").trim());
        //判断使用默认的配置方式还是采用自定义配置方式,
        boolean isSetDefault = (PropertUtil.getPropertValue("defaultSetting").trim() == null || "".equals(PropertUtil.getPropertValue("defaultSetting").trim())) ? true : Boolean.parseBoolean(PropertUtil.getPropertValue("defaultSetting"));
        if (isSetDefault) {
            jedisPool = new JedisPool(new GenericObjectPoolConfig(), host, port, poolTimeOut, auth);
        } else {
            JedisPoolConfig config = new JedisPoolConfig();
            String blockWhenExhausted = PropertUtil.getPropertValue("redis.blockWhenExhausted").trim();
            if (blockWhenExhausted != null) {
                config.setBlockWhenExhausted(Boolean.parseBoolean(blockWhenExhausted));
            }
            String evictionPolicyClassName = PropertUtil.getPropertValue("redis.evictionPolicyClassName").trim();
            if (evictionPolicyClassName != null) {
                config.setEvictionPolicyClassName(evictionPolicyClassName);
            }
            String jmxEnabled = PropertUtil.getPropertValue("redis.jmxEnabled").trim();
            if (jmxEnabled != null) {
                config.setJmxEnabled(Boolean.parseBoolean(jmxEnabled));
            }
            String lifo = PropertUtil.getPropertValue("redis.lifo").trim();
            if (lifo != null) {
                config.setLifo(Boolean.parseBoolean(lifo));
            }
            String maxIdle = PropertUtil.getPropertValue("redis.maxIdle");
            if (maxIdle != null) {
                config.setMaxIdle(Integer.parseInt(maxIdle));
            }
            String maxTotal = PropertUtil.getPropertValue("redis.maxTotal");
            if (maxTotal != null) {
                config.setMaxTotal(Integer.parseInt(maxTotal));
            }
            String maxWaitMillis = PropertUtil.getPropertValue("redis.maxWaitMillis");
            if (maxWaitMillis != null) {
                config.setMaxWaitMillis(Long.parseLong(maxWaitMillis));
            }
            String minEvictableIdleTimeMillis = PropertUtil.getPropertValue("redis.minEvictableIdleTimeMillis");
            if (minEvictableIdleTimeMillis != null) {
                config.setMinEvictableIdleTimeMillis(Long.parseLong(minEvictableIdleTimeMillis));
            }
            String minIdle = PropertUtil.getPropertValue("redis.minIdle");
            if (minIdle != null) {
                config.setMinIdle(Integer.parseInt(minIdle));
            }
            String numTestsPerEvictionRun = PropertUtil.getPropertValue("redis.numTestsPerEvictionRun");
            if (numTestsPerEvictionRun != null) {
                config.setNumTestsPerEvictionRun(Integer.parseInt(numTestsPerEvictionRun));
            }
            String softMinEvictableIdleTimeMillis = PropertUtil.getPropertValue("redis.softMinEvictableIdleTimeMillis");
            if (softMinEvictableIdleTimeMillis != null) {
                config.setSoftMinEvictableIdleTimeMillis(Long.parseLong(softMinEvictableIdleTimeMillis));
            }
            String testOnBorrow = PropertUtil.getPropertValue("redis.testOnBorrow");
            if (testOnBorrow != null) {
                config.setTestOnBorrow(Boolean.parseBoolean(testOnBorrow));
            }
            String testWhileIdle = PropertUtil.getPropertValue("redis.testWhileIdle");
            if (testWhileIdle != null) {
                config.setTestWhileIdle(Boolean.parseBoolean(testWhileIdle));
            }
            String timeBetweenEvictionRunsMillis = PropertUtil.getPropertValue("redus.timeBetweenEvictionRunsMillis");
            if (timeBetweenEvictionRunsMillis != null) {
                config.setTimeBetweenEvictionRunsMillis(Long.parseLong(timeBetweenEvictionRunsMillis));
            }
            jedisPool = new JedisPool(config, host, port, poolTimeOut, auth);
        }

    }

    /**
     * 获取jedis实例
     * @return
     */

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 关闭redis链接
     * @param jedis
     */

    public static void closeJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
