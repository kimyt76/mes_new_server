package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

@Data
public class MoveStockProcMapVo {

    private Long moveStockProcId;
    private Long moveStockId;
    private Long workProcId;
    private String moveStatus;
    private String userId;

}
