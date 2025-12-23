package com.jct.mes_new.biz.work.mapper;

import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkOrderMapper {
    //    메인 리스트 조회
    List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo);

    //상세 조회
    WorkOrderVo getWorkOrderMst(@Param("workOrderId") String workOrderId);
    List<WorkOrderVo.Batch> getWorkOrderBatch(@Param("workOrderId") String workOrderId);
    List<WorkOrderVo.Item> getWorkOrderProc(@Param("workOrderId") String workOrderId);
//등록 및 수정
    void insertWorkOrderMst(WorkOrderVo vo);
    void updateWorkOrderMst(WorkOrderVo vo);
    void insertWorkOrderBatch(WorkOrderVo.Batch b);
    void updateWorkOrderBatch(WorkOrderVo.Batch b);
    void insertWorkOrderProc(WorkOrderVo.Item i);
    void updateWorkOrderProc(WorkOrderVo.Item i);
}
