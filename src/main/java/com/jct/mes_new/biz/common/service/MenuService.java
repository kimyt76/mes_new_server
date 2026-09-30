package com.jct.mes_new.biz.common.service;

import com.jct.mes_new.biz.system.vo.MenuVo;

import java.util.List;

public interface MenuService {

    List<MenuVo> getMenus(String userId);

    List<MenuVo> getMenuListByUser(String userId);
}
