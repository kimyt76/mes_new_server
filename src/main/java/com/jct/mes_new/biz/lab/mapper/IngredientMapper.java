package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.IngredientVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IngredientMapper {

    List<IngredientVo> getIngredientList(IngredientVo ingredientVo);

    IngredientVo getIngredientInfo(String ingredientCode);

    List<String> getCountList(String ingredientCode, String type);

    String getNewCode();

    boolean saveIngredientInfo(IngredientVo vo);

    int saveCountry(@Param("countries") List<String> countries,
                    @Param("type") String type,
                    @Param("ingredientCode") String ingredientCode,
                    @Param("userId") String userId );


    int countryInit(@Param("ingredientCode") String ingredientCode, @Param("type") String type);
}
