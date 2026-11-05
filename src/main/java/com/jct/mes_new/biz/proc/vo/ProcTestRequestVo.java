package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class ProcTestRequestVo {

    WorkOrderInfoVo workOrderInfo;
    ProcTestVo procTest;
    List<ProcTestWorkerVo> workerList;
    List<ProcTestEquipVo> equipList;
    List<ProcTestMethodVo> methodList;

}
