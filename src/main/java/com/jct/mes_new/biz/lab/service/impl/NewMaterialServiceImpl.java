package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.MaterialMapper;
import com.jct.mes_new.biz.lab.mapper.NewMaterialMapper;
import com.jct.mes_new.biz.lab.service.NewMaterialService;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialRequestVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialVo;
import com.jct.mes_new.config.common.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.jdbc.OracleJdbcIndexedSessionRepositoryCustomizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NewMaterialServiceImpl implements NewMaterialService {

    private final NewMaterialMapper newMaterialMapper;
    private final MaterialMapper materialMapper ;

    public List<NewMaterialVo> getNewMaterialList(NewMaterialVo vo){
        return newMaterialMapper.getNewMaterialList(vo);
    }

    public NewMaterialRequestVo getNewMaterialInfo(String newMaterialCd){
        NewMaterialRequestVo vo = new NewMaterialRequestVo();

        vo.setNewMaterialInfo(newMaterialMapper.getNewMaterialInfo(newMaterialCd));
        vo.setMaterialMappingList(newMaterialMapper.getMaterialMappingList(newMaterialCd));

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveNewMaterialInfo(NewMaterialVo newMaterialInfo, List<IngredientVo> materialMappingList){
        String newMaterialId = newMaterialInfo.getNewMaterialId();
        String userId = newMaterialInfo.getUserId();

        try {
            if (newMaterialId == null ){
                newMaterialId = CommonUtil.generateUUID();
                newMaterialInfo.setNewMaterialId(newMaterialId);
                newMaterialInfo.setNewMaterialCd(newMaterialMapper.getNewCode());
            }
            if( newMaterialMapper.saveNewMaterialInfo(newMaterialInfo)  <= 0 ) {
                throw new Exception("신 원료 저장에 실패했습니다.");
            }
            if ( !materialMappingList.isEmpty() ){
                newMaterialMapper.deleteMaterialMappingList(newMaterialInfo.getNewMaterialCd());

                for(IngredientVo ingredient : materialMappingList) {
                    ingredient.setNewMaterialCd(newMaterialInfo.getNewMaterialCd());
                    ingredient.setUserId(userId);

                    if(newMaterialMapper.saveNewMaterialMappingList(ingredient) <= 0){
                    throw new Exception("성분저장에 실패했습니다.");
                   }
                }
            }
        }catch(Exception e) {
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return newMaterialInfo.getNewMaterialCd();
    }

    public List<IngredientVo> getNewMaterialListMapping(String newMaterialCd){
        return newMaterialMapper.getMaterialMappingList(newMaterialCd);
    }


    public String saveNewMaterialMapping(NewMaterialVo newMaterialInfo, List<IngredientVo> materialMappingList){
        String msg = "이관 되었습니다.";
        String itemCd = newMaterialInfo.getItemCd();
        String userId = newMaterialInfo.getUserId();

        try {
            if ( !materialMappingList.isEmpty() ){
                if ( materialMapper.getItemCdCheck(itemCd)  > 0 ) {
                    throw new Exception("중복된 품목코드입니다.");
                }
                newMaterialMapper.updateItemCd(newMaterialInfo);

                for(IngredientVo ingredient : materialMappingList) {
                    ingredient.setItemCd(itemCd);
                    ingredient.setUserId(userId);

                    if(materialMapper.saveMaterialList(ingredient) <= 0){
                        throw new Exception("성분저장에 실패했습니다.");
                    }
                }
            }
        }catch(Exception e) {
            throw new RuntimeException("이관에 실패했습니다.: " + e.getMessage(), e);
        }

        return msg;
    }



}
