package com.jct.mes_new.biz.system.vo;

import com.jct.mes_new.auth.vo.UserVo;
import lombok.Data;

import java.util.List;

@Data
public class MenuRequestVo {

    private List<UserVo> userList;
    private List<MenuVo> grpMenuList;
    private List<MenuVo> menuList;
}
