package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.common.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<MenuVo> getMenus(String userId);
}
