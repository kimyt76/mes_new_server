package com.jct.mes_new.biz.common.vo;

import lombok.Data;

@Data
public class MenuVo {
    private int menuId;
    private Integer parentId;
    private String menuName;
    private String menuPath;
    private String icon;
    private String readYn;
    private String writeYn;
}
