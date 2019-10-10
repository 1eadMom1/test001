package com.atsanwu.usergmall.service;

import com.atsanwu.usergmall.bean.UmsMember;

import java.util.List;

public interface UserService {

    List<UmsMember> getAllUser();

    List<UmsMember> getUserById(String memberId);
}
