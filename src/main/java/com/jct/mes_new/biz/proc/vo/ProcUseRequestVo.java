package com.jct.mes_new.biz.proc.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProcUseRequestVo {
    private ProcUseInfoVo prodInfo;
    private List<ProcUseInfoVo> prodUseList;
}
