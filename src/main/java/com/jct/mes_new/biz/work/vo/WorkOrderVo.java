package com.jct.mes_new.biz.work.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class WorkOrderVo {
        // MST
        private Long workOrderId;
        private String workOrderDateSeq;
        private String workOrderDate;
        private Integer seq;
        private String areaCd;
        private String areaName;
        private String clientId;
        private String clientName;
        private String managerId;
        private String managerName;
        private BigDecimal deliveryQty;
        private String deliveryDate;
        private String itemCd;
        private String itemName;
        private String poNo;
        private String etc;

        private String strDate;
        private String toDate;

        private Integer batchCnt;
        private String userId;

        private List<Batch> batches;

        @Data
        public static class Batch {
            // BATCH
            private Long workBatchId;
            private Long workOrderId;
            private String poNo;
            private String matNo;
            private String lotNo;
            private String lotNo2;

            private String userId;

            private List<Item> items;
        }

        @Data
        public static class Item {
            // ITEM
            private Long workProcId;
            private Long workBatchId;
            private Long workOrderId;
            private String poNo;
            private String procCd;
            private String itemCd;
            private String itemName;
            private String workOrderDate;
            private BigDecimal workQty;
            private String workStatus;

            private String userId;
        }
}
