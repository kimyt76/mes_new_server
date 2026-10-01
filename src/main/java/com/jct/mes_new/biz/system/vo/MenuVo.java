package com.jct.mes_new.biz.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class MenuVo {
    private Integer menuId;
    private Integer parentId;
    private String menuName;
    private String menuPath;
    private String icon;
    private Integer sortOrder;
    private Integer menuLevel;
    private String menuType;
    private String useYn;

    private String readYn;
    private String writeYn;
    private String grpMenuName;
    private String grpMenuId;

    private String userId;
    private String srcUserId;
    private String tarUserId;

    private List<MenuVo> menuList;
}
