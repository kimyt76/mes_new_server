package com.jct.mes_new.auth.service.impl;

import com.jct.mes_new.auth.mapper.UserMapper;
import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.auth.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final UserMapper userMapper;
    final UserDetailsService userDetailsService;

    public UserVo getUserInfo(String userId) {
        return userMapper.getUserInfo(userId);
    }

    public List<UserVo> getUserList(UserVo userVo) {
        return userMapper.getUserList(userVo);
    }

    public boolean updateUserInfo(UserVo userVo) {
        return userMapper.updateUserInfo(userVo);
    }

    public int userCheck(String id){
        return userMapper.userCheck(id);
    }

    public boolean passwordInit(String id, String password){
        return userMapper.passwordInit(id, password);
    }
}
