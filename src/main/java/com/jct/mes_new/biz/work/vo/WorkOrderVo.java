package com.jct.mes_new.biz.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class WorkOrderVo {
        // MST
        private Long workOrderId;
        private String workOrderDateSeq;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate workOrderDate;
        private Integer seq;
        private String areaCd;
        private String areaName;
        private String clientId;
        private String clientName;
        private String managerId;
        private String managerName;
        private BigDecimal deliveryQty;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate deliveryDate;
        private String itemCd;
        private String itemName;
        private String poNo;
        private String etc;
        private String workStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate toDate;
        private String matOrderDate;

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
            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate procOrderDate;
            private BigDecimal orderQty;
            private String procStatus;
            private String procStatusName;

            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate prodDate;
            private String storageCd;
            private String storageName;
            private String memo;
            private String etc;

            private String userId;
        }
}
