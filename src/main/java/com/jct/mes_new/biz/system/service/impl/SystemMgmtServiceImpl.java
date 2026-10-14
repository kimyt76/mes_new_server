package com.jct.mes_new.biz.system.service.impl;

import com.jct.mes_new.auth.mapper.UserMapper;
import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.vo.MenuRequestVo;
import com.jct.mes_new.biz.system.vo.MenuVo;
import com.jct.mes_new.biz.system.mapper.SystemMgmtMapper;
import com.jct.mes_new.biz.system.service.SystemMgmtService;
import com.jct.mes_new.biz.system.vo.SystemUserVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemMgmtServiceImpl implements SystemMgmtService {

    final SystemMgmtMapper systemMgmtMapper;
    final UserMapper userMapper;

    public boolean updateUserInfo(UserVo userVo){
        return systemMgmtMapper.updateUserInfo(userVo);
    }

    public List<MenuVo> getMenuList() {
        String userId = UserUtil.getUserId();
        return systemMgmtMapper.getMenuList(userId);
    }

    public List<MenuVo> getMenuMgmtList(MenuVo vo){
        return systemMgmtMapper.getMenuMgmtList(vo);
    }

    public MenuVo getMenuDetail(int menuId){
        return systemMgmtMapper.getMenuDetail(menuId);
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveMenu(MenuVo vo){
        if(vo.getMenuId() == null){
            if(systemMgmtMapper.insertMenuMst(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }else{
            if(systemMgmtMapper.updateMenuMst(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        //메뉴 권한에 등록
        systemMgmtMapper.insertMenuAuthAllUser(vo.getMenuId());

        return "저장되었습니다.";
    }

    public String updateMenuUseYn(MenuVo vo){
        systemMgmtMapper.updateMenuUseYn(vo);
        return "저장되었습니다.";
    }

    public MenuRequestVo getAuthMenuInfo(UserVo vo){
        MenuRequestVo menuInfo = new MenuRequestVo();
        menuInfo.setUserList(userMapper.getUserList(vo));
        menuInfo.setGrpMenuList(systemMgmtMapper.getGrpMenuList());
        menuInfo.setMenuList(systemMgmtMapper.getMenuAuthList(vo.getMemberNm()));
        return menuInfo;
    }

    public List<MenuVo> getUserMenuAuthList(MenuVo vo){
        return  systemMgmtMapper.getUserMenuAuthList(vo);
    }

    public String saveMenuAuth(MenuVo vo){
        systemMgmtMapper.updateMenuAuth(vo);

        return "저장되었습니다.";
    }

    public String copyMenuAuthInfo(MenuVo vo){

        systemMgmtMapper.copyMenuAuthInfo(vo.getSrcUserId(), vo.getTarUserId());

        return "저장되었습니다.";
    }

    @Transactional
    public void changePassword(SystemUserVo request) {
        if (request.getUserId() == null ||
                request.getUserId().isBlank()) {
            throw new IllegalArgumentException("사용자 ID가 없습니다.");
        }

        if (request.getPassword() == null ||
                request.getPassword().length() < 3) {
            throw new IllegalArgumentException("패스워드는 3자리 이상이어야 합니다.");
        }
        int result = systemMgmtMapper.changePassword(request);

        if (result == 0) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }
    }

}
