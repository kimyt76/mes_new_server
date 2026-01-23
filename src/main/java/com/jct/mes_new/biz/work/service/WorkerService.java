package com.jct.mes_new.biz.work.service;

import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;

import java.util.List;

public interface WorkerService {
    List<WorkerVo> getWorkerAllList(WorkerVo vo);

    String saveWorkerInfo(WorkerVo vo);

    WorkerVo getWorkerInfo(String workerId);
}
