package com.jct.mes_new.biz.lab.vo;

import lombok.Data;
import java.math.BigInteger;

@Data
public class HistoryVo {
    private BigInteger historyId;

    private String historyComment;
    private String updName;
    private String updDate;
    private int orderDist;

    private String itemCd;
    private String userId;
}
