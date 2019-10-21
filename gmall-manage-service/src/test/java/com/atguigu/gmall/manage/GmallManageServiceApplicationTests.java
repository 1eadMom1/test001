package com.atguigu.gmall.manage;

import com.atguigu.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

    @Resource
    RedisUtil redisUtil;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() {
        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("k", "v", 1, TimeUnit.HOURS));
        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("k", "v", 1, TimeUnit.HOURS));
    }

}
