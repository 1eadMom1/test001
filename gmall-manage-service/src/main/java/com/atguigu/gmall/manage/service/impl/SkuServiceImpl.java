package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.utils.StringUtils;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        //插入skuinfo
        int i = pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();
        //插入平台属性关联
        List<PmsSkuAttrValue> skuAttrValues = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue skuAttrValue : skuAttrValues) {
            skuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(skuAttrValue);
        }
        //插入销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValues) {
            skuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }
        //插入图片
        List<PmsSkuImage> pmsSkuImages = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : pmsSkuImages) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
    }

    public PmsSkuInfo getSkuByIdFromDb(String skuId){
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo skuInfo = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        System.out.println(skuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);
        skuInfo.setSkuImageList(pmsSkuImages);
        return skuInfo;
    }

    @Override
    public PmsSkuInfo getSkuById(String skuId,String ip) {
        System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"进入的商品详情的请求");
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        // 链接缓存
        //Jedis jedis = redisUtil.getJedis();
        // 查询缓存
        String skuKey = "sku:"+skuId+":info";
        String skuJson = redisUtil.get(skuKey)!=null?redisUtil.get(skuKey).toString():null;

        if(StringUtils.isNotBlank(skuJson)){//if(skuJson!=null&&!skuJson.equals(""))
            System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"从缓存中获取商品详情");

            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
        }else{
            // 如果缓存中没有，查询mysql
            System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"发现缓存中没有，申请缓存的分布式锁："+"sku:" + skuId + ":lock");

            // 设置分布式锁
            String token = UUID.randomUUID().toString();
            boolean OK = redisUtil.setnx("sku:" + skuId + ":lock", token, 10*1000, TimeUnit.MILLISECONDS);// 拿到锁的线程有10秒的过期时间
            if(OK){
                // 设置成功，有权在10秒的过期时间内访问数据库
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"有权在10秒的过期时间内访问数据库："+"sku:" + skuId + ":lock");

                pmsSkuInfo =  getSkuByIdFromDb(skuId);

                if(pmsSkuInfo!=null){
                    // mysql查询结果存入redis
                    redisUtil.set("sku:"+skuId+":info",JSON.toJSONString(pmsSkuInfo));
                }else{
                    // 数据库中不存在该sku
                    // 为了防止缓存穿透将，null或者空字符串值设置给redis
                    redisUtil.setnx("sku:"+skuId+":info",JSON.toJSONString(""),60*3,TimeUnit.SECONDS);
                }
                // 在访问mysql后，将mysql的分布锁释放
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"使用完毕，将锁归还："+"sku:" + skuId + ":lock");
                String lockToken = redisUtil.get("sku:" + skuId + ":lock").toString();
                if(StringUtils.isNotBlank(lockToken)&&lockToken.equals(token)){
                    //jedis.eval("lua");可与用lua脚本，在查询到key的同时删除该key，防止高并发下的意外的发生
                    redisUtil.del("sku:" + skuId + ":lock");// 用token确认删除的是自己的sku的锁
                }
            }else{
                // 设置失败，自旋（该线程在睡眠几秒后，重新尝试访问本方法）
                System.out.println("ip为"+ip+"的同学:"+Thread.currentThread().getName()+"没有拿到锁，开始自旋");

                return getSkuById(skuId,ip);
            }
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        return pmsSkuInfoMapper.selectSkuSaleAttrValueListBySpu(productId);
    }
}
