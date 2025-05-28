package com.jct.mes_new.biz.system.service.impl;

import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.mapper.SystemMgmtMapper;
import com.jct.mes_new.biz.system.service.SystemMgmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemMgmtServiceImpl implements SystemMgmtService {

    final SystemMgmtMapper systemMgmtMapper;

    public boolean updateUserInfo(UserVo userVo){
        return systemMgmtMapper.updateUserInfo(userVo);
    }
}
