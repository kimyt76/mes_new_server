package com.jct.mes_new.biz.lab.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SampleVo {

    private String  areaName;
    private String  clientName;
	private String  itemName;
	private String  formulationCd;
	private String  businessManagerName;
	private String  labManagerName;
	@JsonDeserialize(using = DateStringToYmdDeserializer.class)
	private String  reqDate;
	private String  prodMgmtNo;
	private BigDecimal  countQty;
	private BigDecimal qty;
	private String  etc;
	private String  confirmYn;
	private String  endReason;
	private String  statusType;
    private String  sampleId;
	private String  areaCd;
    private String  userId;
}
