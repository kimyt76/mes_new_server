package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DraftApprovalVo {

    private Long draftApprovalId;
    private Long draftId;
    private String approvalUserId;
    private String approvalUserName;
    private String approvalCd;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate draftApprovalDate;
    private String boardTxt;

    private String userId;
}
