package com.jct.mes_new.auth.service;

import com.jct.mes_new.auth.vo.UserVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserVo getUserInfo(String userId);

    List<UserVo> getUserList(UserVo userVo);

    boolean updateUserInfo(UserVo userVo);

    int userCheck(String id);

    boolean passwordInit(String id, String password);

}
