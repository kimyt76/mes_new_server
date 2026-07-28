package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientHistoryVo {
    private Long clientHistoryId;
    private Long clientId;
    private String managerName;
    private LocalDate changeDate;
    private String historyContents;
    private int orderDist;
    private String userId ;
}
