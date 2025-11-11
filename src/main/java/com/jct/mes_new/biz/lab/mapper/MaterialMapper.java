package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.lab.vo.HistoryVo;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.MaterialVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MaterialMapper {

    MaterialVo getMaterialInfo(String itemCd);

    List<HistoryVo> getHistoryList(String historyId);

    List<IngredientVo> getMaterialList(String itemCd);

    //int saveMaterialList(List<IngredientVo> materialList);
    int saveMaterialList(IngredientVo materialList);

    int saveMaterialMst(@Param("itemCd") String itemCd, @Param("attachFileId") String attachFileId, @Param("userId") String userId);

    void deleteMaterialList(String itemCd);

    int updateHistory(HistoryVo item);

    int insertHistory(HistoryVo item);

    List<ItemVo> getMaterialItemList(MaterialVo vo);

    int getItemCdCheck(String itemCd);
}
