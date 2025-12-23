package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.util.List;

@Data
public class BomRequestVo {
    private BomVo  bomInfo;
    private List<BomProcVo> bomProcList;
    private List<BomRecipeVo> bomRecipeList;
}
