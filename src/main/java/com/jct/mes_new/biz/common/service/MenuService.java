package com.jct.mes_new.biz.common.service;

import com.jct.mes_new.biz.common.vo.MenuVo;

import java.util.List;
import java.util.Map;

public interface MenuService {

    List<MenuVo> getMenus(String userId);

    List<MenuVo> getMenuListByUser(String userId);
}
