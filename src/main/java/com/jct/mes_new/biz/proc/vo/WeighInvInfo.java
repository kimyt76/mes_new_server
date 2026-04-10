package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.util.List;

@Data
public class WeighInvInfo {
    ProcWeighVo weighInfo;
    List<ProcWeighVo> weighList;
    private List<Long> deleteWeighItems;
}
