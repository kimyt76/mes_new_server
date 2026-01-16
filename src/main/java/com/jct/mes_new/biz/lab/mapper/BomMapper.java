package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.BomProcVo;
import com.jct.mes_new.biz.lab.vo.BomRecipeVo;
import com.jct.mes_new.biz.lab.vo.BomVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BomMapper {
    List<BomVo> getBomList(BomVo bomVo);

    BomVo getBomInfo(String bomId);

    List<BomRecipeVo> getBomRecipeList(String bomId);

    List<BomProcVo> getBomProcList(String bomId);

    BomVo getProductTypeInfo(String prodCd);

    void deleteBomRecipeList(String bomId);

    int saveBomRecipeList(BomRecipeVo recipe);

    void deleteBomProcList(String bomId);

    int saveBomProcList(BomProcVo proc);

    int saveBomInfo(BomVo bomInfo);

    void updateBomVer(String asBomId);

    List<BomRecipeVo> getItemBomList(String itemCd);

    List<BomVo> getBomMatList(BomVo bomVo);

    List<BomVo> getItemsBomList(@Param("itemTypeCd") String itemTypeCd, @Param("itemCds") String itemCds);

    Object getItemStockList(@Param("itemTypeCd") String itemTypeCd, @Param("itemCds") String itemCds);
}
