package com.jct.mes_new.auth.mapper;

import com.jct.mes_new.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

     UserVo getUserInfo(@Param("userId") String userId);

     List<UserVo> getUserList(UserVo userVo);

     boolean updateUserInfo(UserVo userVo);

     int userCheck(@Param("userId") String id);

     boolean passwordInit( @Param("userId") String id, String password);
}
