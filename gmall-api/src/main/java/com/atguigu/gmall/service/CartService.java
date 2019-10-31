package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsCartItem;

public interface CartService {
    OmsCartItem ifCartExistByUser(String memberId, String skuId);

    void addCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void flushCartCache(String memberId);
}
