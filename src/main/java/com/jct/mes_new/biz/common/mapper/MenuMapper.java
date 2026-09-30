package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.system.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<MenuVo> getMenus(String userId);

    List<MenuVo> getMenuListByUser(@Param("userId") String userId);
}
