package com.jct.mes_new.biz.work.service.impl;

import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.service.WorkOrderService;
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

    public List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo){
        return workOrderMapper.getWorkOrderList(vo);
    }

    public WorkOrderVo getWorkOrderInfo(String workOrderId){
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

}
