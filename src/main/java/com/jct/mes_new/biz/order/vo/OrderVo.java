package com.jct.mes_new.biz.order.vo;

import lombok.Data;

@Data
public class OrderVo {


    private String orderId;
    private String orderNm;
    private String orderDate;
    private String orderStatus;

    private int seq;
    private String draftDate;
    private String draftUserId;

    private String draftDept;
    private String customerNm;
    private String itemNm;
    private String orderQty;
    private String dueDate;
    private String approvalId;
    private String boardId;
    private String orderAttachFileId;
    private String prodAttachFileId;
    private String useYn;

    private String fromDate;
    private String toDate;
    private String userId;

    private String draftNm;
    private String boardTxt;
}
