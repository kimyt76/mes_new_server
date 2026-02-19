package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
public class ProcWeighVo {

    private BigInteger weighId;
    private String workProcId;
    private String workOrderId;

    private String areaCd;
    private String areaName;
    private String storageCd;
    private String storageName;

    private String matNo;
    private String itemCd;
    private String itemName;
    private String procOrderDate;
    private String procStatus;
    private BigDecimal orderQty;

    private String procDate;

    private String poNo;
    private String memo;
    private String etc;

    private List<weigh> weighs;
    private String userId;

    @Data
    public static class weigh{
        private BigInteger weighId;
        private String phase;
        private String appearance;
        private String itemCd;
        private String itemName;
        private BigDecimal orderQty;
        private BigDecimal weighQty;
        private BigDecimal bagWeight;
        private String testNo;
        private String weighYn;
        private String weigher;
        private String confirmer;
        private BigInteger distOrder;

        private String workProcId;
        private String userId;

    }
}
