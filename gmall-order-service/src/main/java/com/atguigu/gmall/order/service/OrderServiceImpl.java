package com.atguigu.gmall.order.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    DefaultRedisScript<Boolean> redisScript;

    @Override
    public String genTradeCode(String memberId) {
        String tradeKey = "user:"+memberId+":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        redisUtil.setnx(tradeKey,tradeCode,60*15,TimeUnit.SECONDS);
        return tradeCode;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        // 保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        // 保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
            // 删除购物车数据
            // cartService.delCart();
        }
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
            String tradeKey = "user:" + memberId + ":tradeCode";
            //String tradeCodeFromCache = redisUtil.get(tradeKey).toString();// 使用lua脚本在发现key的同时将key删除，防止并发订单攻击
            List<String> keys = Arrays.asList(tradeKey);
            //对比防重删令牌
            boolean  eval = redisUtil.eval(redisScript, keys,tradeCode);
            if (eval) {
                // jedis.del(tradeKey);
                return "success";
            } else {
                return "fail";
            }
    }
}
