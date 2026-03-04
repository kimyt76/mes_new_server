package com.jct.mes_new.biz.purchase.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderMailVo {
    public String itemCd;
    public String itemName;
    public String spec;
    public BigDecimal orderQty;
    public BigDecimal price;
    public BigDecimal supplyAmt;
    public BigDecimal vat;
    public String customerCd;
    public String orderState;
    public String memo;
}

