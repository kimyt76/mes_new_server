package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DraftVo {


    private Long draftId;
    private String draftDateSeq;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate draftDate;
    private int seq;
    private String draftDept;
    private String drafter;
    private String clientName;
    private String itemName;
    private BigDecimal orderQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private String attachFileId;
    private String endYn;

//    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
//    private LocalDate strDate;
//    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
//    private LocalDate endDate;

    private String userId;
}
