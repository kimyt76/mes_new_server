package com.jct.mes_new.biz.work.mapper;

import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkOrderMapper {
    //    메인 리스트 조회
    List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo);

    //상세 조회
    WorkOrderVo getWorkOrderMst(@Param("workOrderId") Long workOrderId);
    List<WorkOrderVo.Batch> getWorkOrderBatch(@Param("workOrderId") Long workOrderId);
    List<WorkOrderVo.Item> getWorkOrderProc(@Param("workOrderId") Long workOrderId);

    //등록 및 수정
    void insertWorkOrderMst(WorkOrderVo vo);
    void updateWorkOrderMst(WorkOrderVo vo);
    void insertWorkOrderBatch(WorkOrderVo.Batch b);
    void updateWorkOrderBatch(WorkOrderVo.Batch b);
    void insertWorkOrderProc(WorkOrderVo.Item i);
    void updateWorkOrderProc(WorkOrderVo.Item i);

    int deleteWorkOrderProcByOrderIds(@Param("workOrderIds") List<Long> workOrderIds);

    int deleteWorkOrderBatchByOrderIds(@Param("workOrderIds") List<Long> workOrderIds);

    int deleteWorkOrderMstByOrderIds(@Param("workOrderIds") List<Long> workOrderIds);

    /**
     * 작업지시 상세  (workProcId)
     * @param workProcId
     * @return
     */
    WorkOrderInfoVo getWorkOrderProcInfo(@Param("procCd") String procCd, @Param("workProcId") Long workProcId);

    List<WorkOrderInfoVo> getWorkOrderProgressList(WorkOrderVo vo);

    void deleteWeighInv(Long workProcId);
    void deleteWeighInfo(Long workProcId);
    void deleteMatInfo(Long workProcId, String userId);
    void deleteProcUse(Long workProcId);
    void deleteProcInfo(Long workProcId);
    void deleteWorkRecodeInfo(Long workProcId);

    void updateCancelWorkOrderProc(Long workProcId);
    void updateCancelWorkOrderBatch(Long workProcId);
}
