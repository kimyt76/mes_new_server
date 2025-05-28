package com.jct.mes_new.biz.system.service;

import com.jct.mes_new.auth.vo.UserVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SystemMgmtService {

    boolean updateUserInfo(UserVo userVo);
}
