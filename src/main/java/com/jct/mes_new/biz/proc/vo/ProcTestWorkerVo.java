package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

@Data
public class ProcTestWorkerVo {
    private Long procWorkerId;
    private Long procTestId;
    private String type;
    private String workerName;
    private Integer distOrder;

    private String userId;
}
