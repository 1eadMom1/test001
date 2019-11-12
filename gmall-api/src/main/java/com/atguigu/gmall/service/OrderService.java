package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsOrder;

public interface OrderService {
    String genTradeCode(String memberId);

    void saveOrder(OmsOrder omsOrder);

    String checkTradeCode(String memberId, String tradeCode);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(OmsOrder omsOrder);
}
