package flink.redis;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class RedisUtil {
    public static RedisTemplate redisTemplate;

    static {
        redisTemplate=  new RedisConfig().redis;
        log.info("初始化redis设置");
    }


    public static String getString(String key) {
        log.info("从redis获取数据开始,key：{}", key);
        Object o = redisTemplate.opsForValue().get(key);
        log.info("从redis获取数据结束,key：{} ,value:{}", key, JSON.toJSONString(o));
        if (o != null) {
            return o.toString();
        }
        return null;
    }

    public static Boolean setString(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        return true;
    }
}
