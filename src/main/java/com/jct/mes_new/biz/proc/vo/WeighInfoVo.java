package com.jct.mes_new.biz.proc.vo;

import com.jct.mes_new.biz.lab.vo.BomRecipeVo;
import lombok.Data;

import java.util.List;

@Data
public class WeighInfoVo {
    private ProcWeighVo procWeigh;
    private List<ProcWeighBomVo> weightBomList;
}
