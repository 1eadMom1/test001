package com.atguigu.gmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Override
    public List<UmsMember> getAllUser() {
        List<UmsMember> umsMemberList = userMapper.selectAllUser();//userMapper.selectAllUser();
        return umsMemberList;
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId) {

//        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
//        umsMemberReceiveAddress.setMemberId(memberId);
        Example e = new Example(UmsMemberReceiveAddress.class);
        e.createCriteria().andEqualTo("memberId", memberId);
        return umsMemberReceiveAddressMapper.selectByExample(e);
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        if(redisUtil!=null){
            String umsMemberStr =
                    redisUtil.get("user:" + umsMember.getPassword() + ":info")!=null?
                            redisUtil.get("user:" + umsMember.getPassword() + ":info").toString():null;
            if (StringUtils.isNotBlank(umsMemberStr)) {
                // 密码正确
                UmsMember umsMemberFromCache = JSON.parseObject(umsMemberStr, UmsMember.class);
                return umsMemberFromCache;
            }
        }
        // 链接redis失败，开启数据库
        UmsMember umsMemberFromDb =loginFromDb(umsMember);
        if(umsMemberFromDb!=null){
            redisUtil.setnx("user:" + umsMember.getPassword() + ":info",JSON.toJSONString(umsMemberFromDb),60*60*24,TimeUnit.SECONDS);
        }
        return umsMemberFromDb;
    }

    @Override
    public void addUserToken(String token, String memberId) {
        redisUtil.setnx("user:"+memberId+":token",token,60*60*2,TimeUnit.SECONDS);
    }

    @Override
    public UmsMember checkOauthUser(UmsMember umsCheck) {
        UmsMember umsMember = userMapper.selectOne(umsCheck);
        return umsMember;
    }

    @Override
    public void addOauthUser(UmsMember umsMember) {
        userMapper.insertSelective(umsMember);
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        UmsMemberReceiveAddress umsMemberReceiveAddress1 = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddress1;
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        List<UmsMember> umsMembers = userMapper.select(umsMember);
        if (umsMembers != null&&umsMembers.size()>0) {
            return umsMembers.get(0);
        }
        return null;
    }
}










