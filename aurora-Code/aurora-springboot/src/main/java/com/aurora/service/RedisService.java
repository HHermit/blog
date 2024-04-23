package com.aurora.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public interface RedisService {

    void set(String key, Object value, long time);

    void set(String key, Object value);

    Object get(String key);

    Boolean del(String key);

    Long del(List<String> keys);

    Boolean expire(String key, long time);

    Long getExpire(String key);

    Boolean hasKey(String key);

    Long incr(String key, long delta);

    Long incrExpire(String key, long time);

    Long decr(String key, long delta);

    Object hGet(String key, String hashKey);

    Boolean hSet(String key, String hashKey, Object value, long time);

    void hSet(String key, String hashKey, Object value);

    Map<String, Object> hGetAll(String key);

    Boolean hSetAll(String key, Map<String, Object> map, long time);

    void hSetAll(String key, Map<String, ?> map);

    void hDel(String key, Object... hashKey);

    Boolean hHasKey(String key, String hashKey);

    /**
    * @Description: delta：增量
    * @Param: [key, hashKey, delta]
    * @return: java.lang.Long
    */
    Long hIncr(String key, String hashKey, Long delta);

    Long hDecr(String key, String hashKey, Long delta);

    Double zIncr(String key, Object value, Double score);

    Double zDecr(String key, Object value, Double score);

    /**
    * @Description: 获取指定有序集合key中，score在start和end之间的元素及其对应的score，并以Map形式返回。
    * @Param: [key, start, end]
    * @return: java.util.Map<java.lang.Object,java.lang.Double>
    */
    Map<Object, Double> zReverseRangeWithScore(String key, long start, long end);

    /**
    * @Description: 获取指定键值对的分数（Zset 有序集合 按照分数进行排序）
    * @Param: [key, value]
    * @return: java.lang.Double
    */
    Double zScore(String key, Object value);

    /**
     * @Description: 获取redis中指定名字为key的有序集合，并将其value值 以及对应的score存入map结合中
     * @Param: [key]
     * @return: java.util.Map<java.lang.Object,java.lang.Double>
     */
    Map<Object, Double> zAllScore(String key);

    Set<Object> sMembers(String key);

    Long sAdd(String key, Object... values);

    Long sAddExpire(String key, long time, Object... values);

    Boolean sIsMember(String key, Object value);

    Long sSize(String key);

    Long sRemove(String key, Object... values);

    List<Object> lRange(String key, long start, long end);

    Long lSize(String key);

    Object lIndex(String key, long index);

    Long lPush(String key, Object value);

    Long lPush(String key, Object value, long time);

    Long lPushAll(String key, Object... values);

    Long lPushAll(String key, Long time, Object... values);

    Long lRemove(String key, long count, Object value);

    Boolean bitAdd(String key, int offset, boolean b);

    Boolean bitGet(String key, int offset);

    Long bitCount(String key);

    List<Long> bitField(String key, int limit, int offset);

    byte[] bitGetAll(String key);

    Long hyperAdd(String key, Object... value);

    Long hyperGet(String... key);

    void hyperDel(String key);

    Long geoAdd(String key, Double x, Double y, String name);

    List<Point> geoGetPointList(String key, Object... place);

    Distance geoCalculationDistance(String key, String placeOne, String placeTow);

    GeoResults<RedisGeoCommands.GeoLocation<Object>> geoNearByPlace(String key, String place, Distance distance, long limit, Sort.Direction sort);

    List<String> geoGetHash(String key, String... place);

}

