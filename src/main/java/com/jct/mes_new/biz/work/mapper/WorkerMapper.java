package com.jct.mes_new.biz.work.mapper;

import com.jct.mes_new.biz.work.vo.WorkerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkerMapper {

    List<WorkerVo> getWorkerAllList(WorkerVo vo);

    int saveWorkerInfo(WorkerVo vo);

    WorkerVo getWorkerInfo(@Param("workerId") String workerId);
}
