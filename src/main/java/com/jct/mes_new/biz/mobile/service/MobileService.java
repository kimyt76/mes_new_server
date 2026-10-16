package com.jct.mes_new.biz.mobile.service;

import com.jct.mes_new.biz.monitoring.vo.MatMonitoringVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;

import java.util.List;

public interface MobileService {
    List<MatMonitoringVo> getEquipmentList(String areaCd);

    List<WorkOrderInfoVo> getWorkOrderItems(WorkOrderInfoVo vo);

    List<ProcWeighBomVo> getMatUseList(Long workProcId);

    List<WorkerVo> getWorkerAllList();

    List<ProcWeighBomVo> investmentMatUse(ProcMakeVo vo);

    Long startWorkOrderItem(ProcMakeVo vo);

    Long finishWorkOrderItem(ProcMakeVo vo);
}
