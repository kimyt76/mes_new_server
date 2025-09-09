package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.common.mapper.MenuMapper;
import com.jct.mes_new.biz.common.service.MenuService;
import com.jct.mes_new.biz.common.vo.MenuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public List<MenuVo> getMenus(String userId){
        return menuMapper.getMenus(userId);
    }
}
