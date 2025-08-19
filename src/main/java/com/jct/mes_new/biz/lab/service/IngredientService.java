package com.jct.mes_new.biz.lab.service;

import com.jct.mes_new.biz.lab.vo.IngredientVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IngredientService {
    List<IngredientVo> getIngredientList(IngredientVo ingredientVo);

    IngredientVo getIngredientInfo(String ingredientCode);

    String saveIngredientInfo(IngredientVo ingredientVo) throws Exception;
}
