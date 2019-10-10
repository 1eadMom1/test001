package com.atsanwu.usergmall.service.impl;

import com.atsanwu.usergmall.bean.UmsMember;
import com.atsanwu.usergmall.mapper.UserMapper;
import com.atsanwu.usergmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UmsMember> getAllUser() {
        return userMapper.selectAllUser();
    }

    @Override
    public List<UmsMember> getUserById(String memberId) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        return userMapper.select(umsMember);
    }
}
