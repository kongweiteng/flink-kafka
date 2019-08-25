package flink;

import com.alibaba.fastjson.JSON;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.GeoRadiusParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class JedisUtil {
    private static final int DEFAULT_SETEX_TIMEOUT = 60 * 60;// setex的默认时间

    /**
     * 添加一个字符串值,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int set(String key, String value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis.set(key, value).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个字符串值,成功返回1,失败返回0,默认缓存时间为1小时,以本类的常量DEFAULT_SETEX_TIMEOUT为准
     *
     * @param key
     * @param value
     * @return
     */
    public static int setEx(String key, String value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis.setex(key, DEFAULT_SETEX_TIMEOUT, value).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个字符串值,成功返回1,失败返回0,缓存时间以timeout为准,单位秒
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public static int setEx(String key, String value, int timeout) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            if (jedis.setex(key, timeout, value).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个指定类型的对象,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> int set(String key, T value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeri(value);
            if (jedis.set(key.getBytes(), data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个指定类型的对象,成功返回1,失败返回0,默认缓存时间为1小时,以本类的常量DEFAULT_SETEX_TIMEOUT为准
     *
     * @param key
     * @param value
     * @return
     */
    public static <T> int setEx(String key, T value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeri(value);
            if (jedis.setex(key.getBytes(), DEFAULT_SETEX_TIMEOUT, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个指定类型的对象,成功返回1,失败返回0,缓存时间以timeout为准,单位秒
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public static <T> int setEx(String key, T value, int timeout) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeri(value);
            if (jedis.setex(key.getBytes(), timeout, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 将一个数值+1,成功返回+后的结果,失败返回null
     *
     * @param key
     * @return
     * @throws JedisDataException
     */
    public static Long incr(String key) throws JedisDataException {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.incr(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 将一个数值+n,成功返回+后的结果,失败返回null
     *
     * @param key
     * @return
     * @throws JedisDataException
     */
    public static Long incrBy(String key, long integer) throws JedisDataException {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.incrBy(key,integer);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 将一个数值-1,成功返回-后的结果,失败返回null
     *
     * @param key
     * @return
     * @throws JedisDataException
     */
    public static Long decr(String key) throws JedisDataException {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.decr(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 将一个数值-n,成功返回-后的结果,失败返回null
     *
     * @param key
     * @return
     * @throws JedisDataException
     */
    public static Long decrBy(String key,long integer) throws JedisDataException {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.decrBy(key, integer);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个字符串值到list中,,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setList(String key, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.rpush(key, value);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个字符串值到list中,全部list的key默认缓存时间为1小时,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setExList(String key, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.rpush(key, value);
            jedis.expire(key, DEFAULT_SETEX_TIMEOUT);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }

        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个字符串值到list中,全部list的key缓存时间为timeOut,单位为秒,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setExList(String key, int timeOut, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.rpush(key, value);
            jedis.expire(key, timeOut);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }

        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个<T>类型对象值到list中,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setList(String key, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.rpush(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个<T>类型对象值到list中,全部list的key默认缓存时间为1小时,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setExList(String key, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.rpush(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            jedis.expire(key, DEFAULT_SETEX_TIMEOUT);
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个<T>类型对象值到list中,全部list的key缓存时间为timeOut,单位秒,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setExList(String key, int timeOut, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.rpush(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            jedis.expire(key, timeOut);
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个List集合成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     * @throws IOException
     * @throws RuntimeException
     */
    public static <T> int setList(String key, List<T> value) throws RuntimeException, IOException {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeriList(value);
            if (jedis.set(key.getBytes(), data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个List<T>集合,成功返回1,失败返回0,默认缓存时间为1小时,以本类的常量DEFAULT_SETEX_TIMEOUT为准
     *
     * @param key
     * @param value
     * @return
     * @throws IOException
     * @throws RuntimeException
     */

    public static <T> int setExList(String key, List<T> value) throws RuntimeException, IOException {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeriList(value);
            if (jedis.setex(key.getBytes(), DEFAULT_SETEX_TIMEOUT, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个List<T>集合,成功返回1,失败返回0,缓存时间以timeout为准,单位秒
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     * @throws IOException
     * @throws RuntimeException
     */
    public static <T> int setExList(String key, List<T> value, int timeout) throws RuntimeException, IOException {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = enSeriList(value);
            if (jedis.setex(key.getBytes(), timeout, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个字符串到set,如果key存在就在就最追加,如果key不存在就创建,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setSet(String key, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.sadd(key, value);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个字符串set,如果key存在就在就最追加,整个set的key默认一小时后过期,如果key存在就在可以种继续添加,如果key不存在就创建,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setExSet(String key, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.sadd(key, value);
            jedis.expire(key, DEFAULT_SETEX_TIMEOUT);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个字符串set,如果key存在就在就最追加,整个set的key有效时间为timeOut时间,单位秒,如果key存在就在可以种继续添加,如果key不存在就创建,,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static int setExSet(String key, int timeOut, String... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Long result = jedis.sadd(key, value);
            jedis.expire(key, timeOut);
            if (result != null && result != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个<T>类型到set集合,如果key存在就在就最追加,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setSet(String key, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.sadd(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个<T>类型到set集合,如果key存在就在就最追加,整个set的key默认有效时间为1小时,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setExSet(String key, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.sadd(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            jedis.expire(key, DEFAULT_SETEX_TIMEOUT);
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个<T>类型到set集合,如果key存在就在就最追加,整个set的key有效时间为timeOut,单位秒,成功返回1,失败或者没有受影响返回0
     *
     * @param key
     * @param value
     * @return
     */
    @SafeVarargs
    public static <T> int setExSet(String key, int timeOut, T... value) {
        if (isValueNull(key, value)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            int res = 0;
            for (T t : value) {
                byte[] data = enSeri(t);
                Long result = jedis.sadd(key.getBytes(), data);
                if (result != null && result != 0) {
                    res++;
                }
            }
            jedis.expire(key, timeOut);
            if (res != 0) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 添加一个Map<K, V>集合,成功返回1,失败返回0
     *
     * @param key
     * @param value
     * @return
     */
    public static <K, V> int setMap(String key, Map<K, V> value) {
        if (value == null || key == null || "".equals(key)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            String data = JSON.toJSONString(value);
            if (jedis.set(key, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个Map<K, V>集合,成功返回1,失败返回0,默认缓存时间为1小时,以本类的常量DEFAULT_SETEX_TIMEOUT为准
     *
     * @param key
     * @param value
     * @return
     */
    public static <K, V> int setExMap(String key, Map<K, V> value) {
        if (value == null || key == null || "".equals(key)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            String data = JSON.toJSONString(value);
            if (jedis.setex(key, DEFAULT_SETEX_TIMEOUT, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 缓存一个Map<K, V>集合,成功返回1,失败返回0,缓存时间以timeout为准,单位秒
     *
     * @param key
     * @param value
     * @param timeout
     * @return
     */
    public static <K, V> int setExMap(String key, Map<K, V> value, int timeout) {
        if (value == null || key == null || "".equals(key)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            String data = JSON.toJSONString(value);
            if (jedis.setex(key, timeout, data).equalsIgnoreCase("ok")) {
                return 1;
            } else {
                return 0;
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获取一个字符串值
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.get(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个指定类型的对象
     *
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();

            byte[] data = jedis.get(key.getBytes());
            T result = deSeri(data, clazz);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个字符串集合,区间以偏移量 START 和 END 指定。 其中 0 表示列表的第一个元素， 1
     * 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static List<String> getList(String key, long start, long end) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            List<String> result = jedis.lrange(key, start, end);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个<T>类型的对象集合,区间以偏移量 START 和 END 指定。 其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public static <T> List<T> getList(String key, long start, long end, Class<T> clazz) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            List<byte[]> lrange = jedis.lrange(key.getBytes(), start, end);
            List<T> result = null;
            if (lrange != null) {
                for (byte[] data : lrange) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(deSeri(data, clazz));
                }
            }
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得list中存了多少个值
     *
     * @return
     */
    public static long getListCount(String key) {
        if (isValueNull(key)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.llen(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个List<T>的集合,
     *
     * @param key   键
     * @param clazz 返回集合的类型
     * @return
     * @throws IOException
     */
    public static <T> List<T> getList(String key, Class<T> clazz) throws IOException {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            byte[] data = jedis.get(key.getBytes());
            List<T> result = deSeriList(data, clazz);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个字符串set集合
     *
     * @param key
     * @return
     */
    public static Set<String> getSet(String key) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Set<String> result = jedis.smembers(key);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个字符串set集合
     *
     * @param key
     * @return
     */
    public static <T> Set<T> getSet(String key, Class<T> clazz) {
        if (isValueNull(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Set<byte[]> smembers = jedis.smembers(key.getBytes());
            Set<T> result = null;
            if (smembers != null) {
                for (byte[] data : smembers) {
                    if (result == null) {
                        result = new HashSet<>();
                    }
                    result.add(deSeri(data, clazz));
                }
            }
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得集合中存在多少个值
     *
     * @param key
     * @return
     */
    public static long getSetCount(String key) {
        if (isValueNull(key)) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.scard(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获得一个Map<v,k>的集合
     *
     * @param key
     * @param v
     * @param k
     * @return
     */
    public static <K, V> Map<K, V> getMap(String key, Class<K> k, Class<V> v) {
        if (key == null || "".equals(key)) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            String data = jedis.get(key);
            @SuppressWarnings("unchecked")
            Map<K, V> result = (Map<K, V>) JSON.parseObject(data);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 判斷key是否存在
     * @param key
     * @return
     */
    public static boolean exists(String key){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.exists(key);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 判斷key是否存在
     * @param keys
     * @return
     */
    public static Long exists(String... keys){
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.exists(keys);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 删除一个值
     *
     * @param key
     */
    public static void del(String... key) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            for (int i = 0; i < key.length; i++) {
                jedis.del(key);
            }
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    // --------------------------公用方法区------------------------------------

    /**
     * 检查值是否为null,如果为null返回true,不为null返回false
     *
     * @param obj
     * @return
     */
    private static boolean isValueNull(Object... obj) {
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] == null || "".equals(obj[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 序列化一个对象
     *
     * @param value
     * @return
     */
    private static <T> byte[] enSeri(T value) {
        @SuppressWarnings("unchecked")
        RuntimeSchema<T> schema = (RuntimeSchema<T>) RuntimeSchema.createFrom(value.getClass());
        byte[] data = ProtostuffIOUtil.toByteArray(value, schema,
                LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        return data;
    }

    /**
     * 反序列化一个对象
     *
     * @param data
     * @param clazz
     * @return
     */
    private static <T> T deSeri(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
        T result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, result, schema);
        return result;
    }

    /**
     * 序列化List集合
     *
     * @param list
     * @return
     * @throws IOException
     */
    private static <T> byte[] enSeriList(List<T> list) throws RuntimeException, IOException {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("集合不能为空!");
        }
        @SuppressWarnings("unchecked")
        RuntimeSchema<T> schema = (RuntimeSchema<T>) RuntimeSchema.getSchema(list.get(0).getClass());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ProtostuffIOUtil.writeListTo(out, list, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        byte[] byteArray = out.toByteArray();
        return byteArray;
    }

    /**
     * 反序列化List集合
     *
     * @param data
     * @param clazz
     * @return
     * @throws IOException
     */
    private static <T> List<T> deSeriList(byte[] data, Class<T> clazz) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }
        RuntimeSchema<T> schema = RuntimeSchema.createFrom(clazz);
        List<T> result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(data), schema);
        return result;
    }

    //----------------------geo start------------------------------------------

    /**
     * 增加地理位置的坐标
     *
     * @param key
     * @param coordinate
     * @param member
     * @return Long
     */
    public static Long geoadd(String key, GeoCoordinate coordinate, String member) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.geoadd(key, coordinate.getLongitude(), coordinate.getLatitude(), member);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 批量添加地理位置
     *
     * @param key
     * @param memberCoordinateMap
     * @return Long
     */
    public static Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.geoadd(key, memberCoordinateMap);
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 根据给定地理位置坐标获取指定范围内的地理位置集合（返回匹配位置的经纬度 + 匹配位置与给定地理位置的距离 + 从近到远排序，）
     *
     * @param key
     * @param coordinate
     * @param radius
     * @return List<GeoRadiusResponse>
     */
    public static List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate coordinate, double radius) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.georadius(key, coordinate.getLongitude(), coordinate.getLatitude(), radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().withCoord().sortAscending());
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 根据给定地理位置获取指定范围内的地理位置集合（返回匹配位置的经纬度 + 匹配位置与给定地理位置的距离 + 从近到远排序，）
     *
     * @param key
     * @param member
     * @param radius
     * @return List<GeoRadiusResponse>
     */
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            return jedis.georadiusByMember(key, member, radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().withCoord().sortAscending());
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 查询2位置距离
     *
     * @param key
     * @param member1
     * @param member2
     * @param unit
     * @return Double
     */
    public static Double geoDist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            Double dist = jedis.geodist(key, member1, member2, unit);
            return dist;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 查询位置的geohash
     *
     * @param key
     * @param members
     * @return List<String>
     */
    public static List<String> geoHash(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            List<String> resultList = jedis.geohash(key, members);
            return resultList;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }

    /**
     * 获取地理位置的坐标
     *
     * @param key
     * @param member
     * @return List<GeoCoordinate>
     */
    public static List<GeoCoordinate> geopos(String key, String... member) {
        Jedis jedis = null;
        try {
            jedis = JedisPoolUtil.getJedis();
            List<GeoCoordinate> result = jedis.geopos(key, member);
            return result;
        } finally {
            JedisPoolUtil.closeJedis(jedis);
        }
    }


    /*
     * 添加geo
     * @param key
     * @param longitude
     * @param latitude
     * @param name 位置名称
     * @return
     */
    /*public  Long geoADD(String key,double longitude,double latitude,String eleName){
        String[] params = new String[]{key,String.valueOf(longitude),String.valueOf(latitude),eleName};
        int keyCount = 4;
        return (Long)jedis.eval("return redis.call('GEOADD',KEYS[1],KEYS[2],KEYS[3],KEYS[4])", keyCount,params);
    }*/

    /**
     * 查询2位置距离
     * @param key
     * @param d1
     * @param d2
     * @param unit
     * @return
     */
    /*public  Double geoDist(String key,String d1,String d2,String unit){
        return Double.valueOf((String)jedis.eval("return redis.call('GEODIST',KEYS[1],KEYS[2],KEYS[3],KEYS[4])",4, key,d1,d2,unit));
    }*/

    /**
     * 查询位置的geohash
     * @param key
     * @param dName
     * @return
     */
    /*public  String geoHash(String key,String dName){
        Object data = jedis.eval("return redis.call('GEOHASH',KEYS[1],KEYS[2])", 2, new String[]{key,dName});//GEOPOS 也可以？
        List resultList = (List)data;
        if(resultList!=null&&resultList.size() > 0){
            return (String)resultList.get(0);
        }
        return null;
    }*/

    /**
     * 查询位置坐标
     * @param key
     * @param dName
     * @return
     */
   /* public  List geoPos(String key,String dName){
        Object data = jedis.eval("return redis.call('GEOPOS',KEYS[1],KEYS[2])", 2, key,dName);
        List<List> resultList = (List<List>)data;
        if(resultList!=null&&resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
    }*/

    /**
     * 查询附近坐标地址
     * @param key
     * @param longitude
     * @param latitude
     * @param unit
     * @param asc
     * @return
     */
    /*public  List geoRadius(String key,double longitude,double latitude,int radius,String unit,boolean asc){
        Object data = jedis.eval("return redis.call('GEORADIUS',KEYS[1],KEYS[2],KEYS[3],KEYS[4],KEYS[5],KEYS[6])", 6, key,String.valueOf(longitude),
                String.valueOf(latitude),String.valueOf(radius),unit,asc?"ASC":"DESC");
        return (List)data;
    }*/

    /**
     * 附近的成员们，根据距离
     *
     * 根据位置查询附近点
     * @param key
     * @param dName
     * @param unit
     * @param asc
     * @return
     */
   /* public  List geoNearByMembersByDistance(String key,String dName,int radius,String unit,boolean asc){
        Object data = jedis.eval("return redis.call('GEORADIUSBYMEMBER',KEYS[1],KEYS[2],KEYS[3],KEYS[4],KEYS[5])", 5, key,dName,String.valueOf(radius),unit,asc?"ASC":"DESC");
        return (List)data;
    }*/

    //------------------------------geo end---------------------------------------

}
