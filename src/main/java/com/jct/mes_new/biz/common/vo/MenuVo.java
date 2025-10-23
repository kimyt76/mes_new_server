package com.jct.mes_new.biz.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class MenuVo {
    private Integer menuId;
    private Integer parentId;
    private String menuName;
    private String menuPath;
    private String routeName;
    private String icon;
    private String readYn;
    private String writeYn;
}
