package com.atsanwu.usergmall.mapper;

import com.atsanwu.usergmall.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UserMapper  extends Mapper<UmsMember> {

    List<UmsMember> selectAllUser();
}
