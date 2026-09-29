package com.jct.mes_new.auth.service.impl;

import com.jct.mes_new.auth.mapper.UserMapper;
import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.mapper.SystemMgmtMapper;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    final UserMapper userMapper;
    final UserDetailsService userDetailsService;
    private final SystemMgmtMapper systemMgmtMapper;

    public UserVo getUserInfo(String userId) {
        return userMapper.getUserInfo(userId);
    }

    public List<UserVo> getUserList(UserVo userVo) {
        return userMapper.getUserList(userVo);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateUserInfo(UserVo userVo) {
        int cnt = this.userCheck(userVo.getUserId());
        userMapper.updateUserInfo(userVo);

        if( cnt <= 0 ) {
            String userId = userVo.getUserId();
            if(systemMgmtMapper.insertMenuAuth(userId) <= 0){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        return "저장되었습니다.";
    }

    public int userCheck(String id){
        return userMapper.userCheck(id);
    }

    public boolean passwordInit(String id, String password){
        return userMapper.passwordInit(id, password);
    }
}
