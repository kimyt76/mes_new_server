package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.IngredientMapper;
import com.jct.mes_new.biz.lab.service.IngredientService;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientMapper ingredientMapper;

    public List<IngredientVo> getIngredientList(IngredientVo ingredientVo) {
        return ingredientMapper.getIngredientList(ingredientVo);
    }

    public IngredientVo getIngredientInfo(String ingredientCode){
        IngredientVo vo = ingredientMapper.getIngredientInfo(ingredientCode);

        vo.setLimitCountries(ingredientMapper.getCountList(ingredientCode, "L"));
        vo.setBannedCountries(ingredientMapper.getCountList(ingredientCode, "B"));

        return vo;
    }


    @Transactional(rollbackFor = Exception.class)
    public String saveIngredientInfo(IngredientVo vo) throws Exception{
        String msg = "저장되었습니다.";
        String limit = "L";
        String ban = "B";

        try{
            if (vo.getIngredientCode() == null ){
                vo.setIngredientCode(ingredientMapper.getNewCode());
            }

            if( !ingredientMapper.saveIngredientInfo(vo)){
                throw new Exception("성분 정보 저장에 실패했습니다.");
            }

            if ( !vo.getLimitCountries().isEmpty() ){
                ingredientMapper.countryInit(vo.getIngredientCode(), limit);

                if (ingredientMapper.saveCountry(vo.getLimitCountries(), limit, vo.getIngredientCode(), vo.getUserId()) <= 0) {
                    throw new Exception("한도 국가 정보 저장에 실패했습니다.");
                }
            }else{
                ingredientMapper.countryInit(vo.getIngredientCode(), limit);
            }

            if ( !vo.getBannedCountries().isEmpty() ) {
                ingredientMapper.countryInit(vo.getIngredientCode(), ban);

                if (ingredientMapper.saveCountry(vo.getBannedCountries(), ban, vo.getIngredientCode(), vo.getUserId()) <= 0) {
                    throw new Exception("금지 국가 정보 저장에 실패했습니다.");
                }
            }else{
                ingredientMapper.countryInit(vo.getIngredientCode(), ban);
            }
        }catch (Exception e){
            throw new RuntimeException("저장 실패: " + e.getMessage(), e);
        }
        return msg;
    }
}
