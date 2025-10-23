package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.common.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper {
    List<MenuVo> getMenus(String userId);

    List<MenuVo> getMenuListByUser(@Param("userId") String userId);
}
