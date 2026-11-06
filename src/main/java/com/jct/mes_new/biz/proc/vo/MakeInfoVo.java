package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.lab.vo.BomProcVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import lombok.Data;

import java.util.List;

@Data
public class MakeInfoVo {

    WorkOrderInfoVo workOrderInfo;
    List<ProcWeighBomVo> makeBomList;
    MakeEtcVo makeEtcInfo;
}
