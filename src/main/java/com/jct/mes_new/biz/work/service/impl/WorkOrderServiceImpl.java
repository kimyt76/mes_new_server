package com.jct.mes_new.biz.work.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.service.WorkOrderService;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderResetRequestVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                workOrderMapper.insertWorkOrderBatch(b); // useGeneratedKeys -> workBatchId 채워짐
            } else {
                workOrderMapper.updateWorkOrderBatch(b);
            }

            if (b.getItems() == null) continue;

            // 3) ITEM upsert
            for (WorkOrderVo.Item i : b.getItems()) {
                i.setProcStatus("00");
                i.setWorkBatchId(b.getWorkBatchId());
                i.setWorkOrderId(vo.getWorkOrderId());
                i.setUserId(userId);
                if (i.getWorkProcId() == null) {
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

    @Transactional(rollbackFor = Exception.class)
    public  String resetWorkOrderProc(WorkOrderResetRequestVo vo) {
        String userId = UserUtil.getUserId();
        String procCd = vo.getProcCd();
        List<Long> workProcIds = vo.getWorkProcIds();

        // 공정별 작업 데이터 삭제
        for (Long workProcId : workProcIds) {
            if (workProcId == null) {
                throw new IllegalArgumentException("공정 ID가 없습니다.");
            }

            if ("PRC001".equals(procCd)) {
                // 칭량
                workOrderMapper.deleteWeighInv(workProcId);
                workOrderMapper.deleteWeighInfo(workProcId);
            } else if ("PRC002".equals(procCd)) {
                // 제조
                workOrderMapper.deleteMatInfo(workProcId, userId);
            } else if ("PRC003".equals(procCd) || "PRC004".equals(procCd) || "PRC005".equals(procCd)) {
                // 코팅, 충전 / 포장
                workOrderMapper.deleteProcUse(workProcId);
                workOrderMapper.deleteProcInfo(workProcId);
                workOrderMapper.deleteWorkRecodeInfo(workProcId);
            }
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
