package com.atguigu.gmall.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisUtil {

    private static Jedis jedis;
    private static final String HOST = "192.168.243.130";
    private static final int PORT = 6371;

    public Jedis getJedis() {
        Jedis jedis = new Jedis(HOST,PORT);
        return jedis;
    }

}
