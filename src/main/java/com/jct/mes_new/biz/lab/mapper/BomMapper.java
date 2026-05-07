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

    List<BomProcVo> getBomProcInfo(String itemCd);

    /**
     * bom 마스터 정보
     * @param bomMst
     * @return
     */
    int insertBomMst(BomVo bomMst);
    int updateBomMst(BomVo bomMst);

    /**
     * bom 레시피 리스트
     * @param deleteBomProc
     */
    void deleteBomRecipe(List<String> deleteBomProc);
    int insertBomRecipe(BomRecipeVo recipe);
    int updateBomRecipe(BomRecipeVo recipe);

    /**
     * bom 제조 공정
     * @param deleteBomProc
     */
    void deleteBomProc(List<String> deleteBomProc);
    int insertBomProc(BomProcVo proc);
    int updateBomProc(BomProcVo proc);
}
