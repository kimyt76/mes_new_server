package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.lab.vo.BomRecipeVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class WeighInfoVo {
    private WorkOrderInfoVo workOrderInfo;
    private List<ProcWeighBomVo> weightBomList;
}
