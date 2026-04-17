package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import lombok.Data;

import java.util.List;

@Data
public class ProcProdInfoVo {

    private ItemVo itemInfo;
    private List<ProcUseInfoVo> prodList;
    private List<WorkRecodeVo> workRecodeList;
    private WorkOrderInfoVo workOrderProcInfo;
}
