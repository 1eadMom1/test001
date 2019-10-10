package com.atsanwu.usergmall.controller;

import com.atsanwu.usergmall.bean.UmsMember;
import com.atsanwu.usergmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("index")
    public String index(){
        return "hello caonima";
    }

    @GetMapping("getAllUser")
    public List<UmsMember> getAllUser(){
        List<UmsMember> umsMembers = userService.getAllUser();
        return umsMembers;
    }

    @GetMapping("getUserById")
    public List<UmsMember> getReceiveAddressByMemberId(String memberId){
        List<UmsMember> umsMemberReceiveAddresses = userService.getUserById(memberId);
        return umsMemberReceiveAddresses;
    }

}
