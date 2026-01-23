package com.jct.mes_new.biz.work.service.impl;

import com.jct.mes_new.biz.work.mapper.WorkerMapper;
import com.jct.mes_new.biz.work.service.WorkerService;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkerServiceImpl implements WorkerService {

    private final WorkerMapper workerMapper;

    public List<WorkerVo> getWorkerAllList(WorkerVo vo){
        return workerMapper.getWorkerAllList(vo);
    }

    public WorkerVo getWorkerInfo(String workerId){
        return workerMapper.getWorkerInfo(workerId);
    }

    public String saveWorkerInfo(WorkerVo vo) {
        if ( workerMapper.saveWorkerInfo(vo) <= 0 ) {
            throw new RuntimeException(String.valueOf(ErrorCode.FAIL_CREATED));
        }
        return "저장되었습니다.";
    }

}
