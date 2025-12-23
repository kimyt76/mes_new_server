package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

@Data
public class DraftVo {


    private String draftId;
    private String draftDateSeq;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
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
    private String statusType;

    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String toDate;
    private String userId;

}
