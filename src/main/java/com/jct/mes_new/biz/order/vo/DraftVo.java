package com.jct.mes_new.biz.order.vo;

import lombok.Data;

@Data
public class DraftVo {


    private String draftId;
    private String draftDateSeq;
    private String draftDate;
    private String draftStatus;
    private int seq;
    private String draftUserId;
    private String draftUserName;

    private String draftDept;
    private String customerName;
    private String itemName;
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

}
