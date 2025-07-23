package com.jct.mes_new.biz.common.vo;

import lombok.Data;

@Data
public class CommonVo {

    public CommonVo() {
        super();
    }

    private String comId;
    private String comTypeCd;
    private String comTypeNm;
    private String code;
    private String codeNm;
    private String dispOrder;
    private String description;
    private String useYn;

    private String userId;

}
