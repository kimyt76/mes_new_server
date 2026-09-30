package com.jct.mes_new.biz.system.mapper;

import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemMgmtMapper {

    boolean updateUserInfo(UserVo userVo);

    List<MenuVo> getMenuList(@Param("userId") String userId);

    int insertMenuAuth(String userId);

    List<MenuVo> getMenuMgmtList(MenuVo vo);

    MenuVo getMenuDetail(int menuId);

    int insertMenuMst(MenuVo vo);
    int updateMenuMst(MenuVo vo);

    boolean insertMenuAuthAllUser(@Param("menuId") int menuId);

    void updateMenuUseYn(MenuVo vo);
}
