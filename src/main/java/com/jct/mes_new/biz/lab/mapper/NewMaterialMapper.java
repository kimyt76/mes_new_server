package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewMaterialMapper {
    List<NewMaterialVo> getNewMaterialList(NewMaterialVo vo);

    NewMaterialVo getNewMaterialInfo(String newMaterialCd);

    List<IngredientVo> getMaterialMappingList(String newMaterialCd);

    int saveNewMaterialInfo(NewMaterialVo newMaterialInfo);

    int saveNewMaterialMappingList(IngredientVo materialMappingList);

    String getNewCode();

    void deleteMaterialMappingList(String newMaterialCd);

    void updateItemCd(NewMaterialVo newMaterialVo);
}
