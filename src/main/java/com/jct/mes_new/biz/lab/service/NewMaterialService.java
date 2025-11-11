package com.jct.mes_new.biz.lab.service;


import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialRequestVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialVo;

import java.util.List;
import java.util.Map;

public interface NewMaterialService {
    List<NewMaterialVo> getNewMaterialList(NewMaterialVo vo);

    NewMaterialRequestVo getNewMaterialInfo(String newMaterialCd);

    String saveNewMaterialInfo(NewMaterialVo newMaterialInfo, List<IngredientVo> materialMappingList);

    List<IngredientVo> getNewMaterialListMapping(String newMaterialCd);

    String saveNewMaterialMapping(NewMaterialVo newMaterialInfo, List<IngredientVo> materialMappingList);
}
