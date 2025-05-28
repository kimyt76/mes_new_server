package com.jct.mes_new.biz.system.mapper;

import com.jct.mes_new.auth.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemMgmtMapper {

    boolean updateUserInfo(UserVo userVo);
}
