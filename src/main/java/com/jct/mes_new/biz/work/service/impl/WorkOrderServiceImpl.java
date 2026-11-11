package com.jct.mes_new.biz.work.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcUseInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.service.WorkOrderService;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderResetRequestVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderMapper workOrderMapper;
    private final ProcWeighMapper procWeighMapper;
    private final ProcCommonMapper procCommonMapper;
    private final ItemTestMapper itemTestMapper;


    public List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo){
        return workOrderMapper.getWorkOrderList(vo);
    }

    public List<WorkOrderInfoVo> getWorkOrderProgressList(WorkOrderVo vo) {
        return workOrderMapper.getWorkOrderProgressList(vo);
    }

    public WorkOrderVo getWorkOrderInfo(Long workOrderId){
        WorkOrderVo mst = workOrderMapper.getWorkOrderMst(workOrderId);

        List<WorkOrderVo.Batch> batches = workOrderMapper.getWorkOrderBatch(workOrderId);
        List<WorkOrderVo.Item> items = workOrderMapper.getWorkOrderProc(workOrderId);

        Map<Long, List<WorkOrderVo.Item>> itemsByBatchId =
                items.stream().collect(Collectors.groupingBy(WorkOrderVo.Item::getWorkBatchId));

        if (batches != null) {
            for (WorkOrderVo.Batch b : batches) {
                b.setItems(itemsByBatchId.getOrDefault(b.getWorkBatchId(), new ArrayList<>()));
            }
        }
        mst.setBatches(batches);
        return mst;
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkOrderVo saveWorkOrderInfo(WorkOrderVo vo) {
        String userId = UserUtil.getUserId();

        // 1) MST upsert
        vo.setUserId(userId);
        if (vo.getWorkOrderId() == null) {
            workOrderMapper.insertWorkOrderMst(vo); // useGeneratedKeys로 workOrderId 채워짐
        } else {
            workOrderMapper.updateWorkOrderMst(vo);
        }

        if (vo.getBatches() == null) return vo;
        // 2) BATCH upsert
        for (WorkOrderVo.Batch b : vo.getBatches()) {
            b.setUserId(userId);
            b.setWorkOrderId(vo.getWorkOrderId());

            if (b.getWorkBatchId() == null) {
                b.setBatchStatus("00");
                workOrderMapper.insertWorkOrderBatch(b); // useGeneratedKeys -> workBatchId 채워짐
            } else {
                workOrderMapper.updateWorkOrderBatch(b);
            }

            if (b.getItems() == null) continue;

            // 3) ITEM upsert
            for (WorkOrderVo.Item i : b.getItems()) {
                i.setWorkBatchId(b.getWorkBatchId());
                i.setWorkOrderId(vo.getWorkOrderId());
                i.setUserId(userId);

                if (i.getWorkProcId() == null) {
                    i.setProcStatus("00");
                    workOrderMapper.insertWorkOrderProc(i); // useGeneratedKeys -> workProcId 채워짐
                } else {
                    workOrderMapper.updateWorkOrderProc(i);
                }
            }
        }
        return vo;
    }

    @Override
    @Transactional
    public int deleteWorkOrders(List<Long> workOrderIds) {
        List<Long> ids = workOrderIds.stream()
                .filter(v -> v != null)
                .distinct()
                .collect(Collectors.toList());

        if (ids.isEmpty()) return 0;

        // FK 순서: PROC -> BATCH -> MST
        workOrderMapper.deleteWorkOrderProcByOrderIds(ids);
        workOrderMapper.deleteWorkOrderBatchByOrderIds(ids);
        return workOrderMapper.deleteWorkOrderMstByOrderIds(ids); // MST 삭제 건수
    }

    /* 공정 리셋*/
    @Transactional(rollbackFor = Exception.class)
    public  String resetWorkOrderProc(WorkOrderResetRequestVo vo) {
        String userId = UserUtil.getUserId();
        String procCd = vo.getProcCd();
        List<Long> workProcIds = vo.getWorkProcIds();
        String procStatus = "00";
        String batchStatus = "00";


        // 공정별 작업 데이터 삭제
        for (Long workProcId : workProcIds) {
            if (workProcId == null) {
                throw new IllegalArgumentException("공정 ID가 없습니다.");
            }

            WorkOrderInfoVo workOrderInfo = workOrderMapper.getWorkOrderProcInfo(procCd, workProcId);

            if ("PRC001".equals(procCd)) {
                //칭량 삭제
                workOrderMapper.deleteWeighInv(workProcId);
                workOrderMapper.deleteWeighInfo(workProcId);

                //칭량 삭제후 bom재 생성
                //List<ProcWeighBomVo>  recipeList = procWeighMapper.getBomWeighList(workProcId, workOrderInfo.getItemCd());

//                for ( ProcWeighBomVo item : recipeList ){
//                    item.setUserId(userId);
//                    item.setWorkProcId(workProcId);
//                    item.setWorkBatchId(workOrderInfo.getWorkBatchId());
//
//                    if ( procWeighMapper.insertWeighRecipe(item) <= 0 ){
//                        throw new BusinessException(ErrorCode.FAIL_CREATED);
//                    }
//                }
            } else if ("PRC002".equals(procCd)) {
                // 제조는 칭량 id로 초기화 해야함
                Long weighWorkProcId = workOrderMapper.getWeighWorkProcId(workProcId);
                workOrderMapper.deleteMatInfo(weighWorkProcId, userId);

            } else if ("PRC003".equals(procCd) || "PRC004".equals(procCd) || "PRC005".equals(procCd)) {
                //QC 정보 먼저 수정
                List<ProcUseInfoVo> prodList = procCommonMapper.getProdList(procCd, workProcId);

                if (!prodList.isEmpty()) {
                    for (ProcUseInfoVo item : prodList) {
                        ItemTestVo testVo = new ItemTestVo();
                        if(!"".equals(item.getTestNo() )) {
                            testVo.setTestNo(item.getTestNo());
                            testVo.setUserId(userId);
                            testVo.setQty(item.getUseQty());
                            itemTestMapper.updateQty(item.getTestNo(), item.getUseQty(), userId);
                        }
                    }
                }

                // 코팅, 충전 / 포장
                workOrderMapper.deleteWorkRecodeInfo(workProcId);
                workOrderMapper.deleteProcUse(workProcId);
                workOrderMapper.deleteProcInfo(workProcId);
//                //코팅, 충전 포장 bom 정보 가져와서 prod_info 에 등록
//                List<ProcUseInfoVo> prodList = procCommonMapper.getProdList(procCd, workProcId);
//                if (prodList.isEmpty()) {
//                    for (ProcUseInfoVo item : prodList) {
//                        if ("".equals(item.getMatName()) || item.getMatName() == null) {
//                            item.setUserId(userId);
//                            item.setWorkProcId(workProcId);
//                            procCommonMapper.insertProdInfo(item);
//                        }
//                    }
//                }
            }
            ProcCommonVo commonVo = new ProcCommonVo();
            commonVo.setWorkProcId(workProcId);
            commonVo.setUserId(userId);
            commonVo.setProcStatus(procStatus);
            commonVo.setBatchStatus(batchStatus);
            commonVo.setWorkBatchId(workOrderInfo.getWorkBatchId());
            procCommonMapper.updateProcStatus(commonVo);
            procCommonMapper.updateBatchStatus(commonVo);
            //procCommonMapper.updateProcQtyInit(workProcId);
        }
        return "리셋되었습니다.";
    }

    @Transactional(rollbackFor = Exception.class)
    public  String cancelWorkOrderProc(WorkOrderResetRequestVo vo) {
        String procCd = vo.getProcCd();
        List<Long> workProcIds = vo.getWorkProcIds();

        // 공정별 작업 데이터 삭제
        for (Long workProcId : workProcIds) {
            if (workProcId == null) {
                throw new IllegalArgumentException("공정 ID가 없습니다.");
            }
            workOrderMapper.updateCancelWorkOrderProc(workProcId);
            workOrderMapper.updateCancelWorkOrderBatch(workProcId);
        }

        return "리셋되었습니다.";
    }
}
