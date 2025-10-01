package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.util.List;

@Data
public class NewMaterialRequestVo {
    private NewMaterialVo newMaterialInfo;
    private List<IngredientVo> materialMappingList;
}
