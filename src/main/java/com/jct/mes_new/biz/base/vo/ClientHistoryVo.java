package com.jct.mes_new.biz.base.vo;

import lombok.Data;

@Data
public class ClientHistoryVo {
    private int historyId;
    private String clientId;
    private String changeDate;
    private String historyContents;
    private int orderDist;
    private String userId ;
}
