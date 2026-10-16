package com.jct.mes_new.biz.mobile.mapper;

import com.jct.mes_new.biz.monitoring.vo.MatMonitoringVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MobileMapper {
    List<MatMonitoringVo> getEquipmentList(String areaCd);

    List<WorkOrderInfoVo> getWorkOrderItems(WorkOrderInfoVo vo);

    List<WorkerVo> getWorkerAllList();

    ProcMakeVo getProcMake(Long weighId);

    int updateMakeMobile(ProcMakeVo procMakeVo);

    ProcMakeVo getWorkOrderProc(Long workProcId);

    int getMakeEtc(Long workProcId);

    void insertMakeEtc(ProcMakeVo vo);

    void updateMakeEtc(ProcMakeVo vo);
}
