package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.BomMapper;
import com.jct.mes_new.biz.lab.service.BomService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class BomServiceImpl implements BomService {

    private final BomMapper bomMapper;

    public BomVo getProductTypeInfo(String prodCd) {
        return bomMapper.getProductTypeInfo(prodCd);
    }

    public List<BomVo> getBomList(BomVo bomVo) {
        return bomMapper.getBomList(bomVo);
    }
    public List<BomVo> getBomMatList(BomVo bomVo) {
        return bomMapper.getBomMatList(bomVo);
    }
    public List<BomRecipeVo> getItemBomList(String itemCd){
        return bomMapper.getItemBomList(itemCd);
    }
    public BomRequestVo getBomInfo(String bomId) {
        BomRequestVo vo = new BomRequestVo();

        vo.setBomInfo(bomMapper.getBomInfo(bomId));
        vo.setBomRecipeList(bomMapper.getBomRecipeList(bomId));
        vo.setBomProcList(bomMapper.getBomProcList(bomId));

        return vo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveBomInfo(BomRequestVo vo) {
        BomVo bomMst = vo.getBomInfo();
        List<BomRecipeVo> bomRecipeList = vo.getBomRecipeList();
        List<BomProcVo> bomProcList = vo.getBomProcList();
        String userId = UserUtil.getUserId();

        String bomId = bomMst.getBomId();
        String msg = "저장되었습니다.";

        if (bomId == null || bomId.isEmpty()) {
            bomId = CommonUtil.generateUUID();
            bomMst.setBomId(bomId);
            bomMst.setUserId(userId);
            //신규
            // 1. BOM 저장
            if (bomMapper.insertBomMst(bomMst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }else{
            //업데이트
            if (bomMapper.updateBomMst(bomMst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }
        // 2. 처방정보 저장
        //처방전에 삭제가 있는지 확인
        if (vo.getDeleteBomRecipe() != null && !vo.getDeleteBomRecipe().isEmpty()) {
            bomMapper.deleteBomRecipe(vo.getDeleteBomProc());
        }

        if (bomRecipeList != null && !bomRecipeList.isEmpty()) {
            for (BomRecipeVo recipe : bomRecipeList) {
                recipe.setBomId(bomId);
                recipe.setUserId(userId);

                if( recipe.getBomItemId() == null || recipe.getBomItemId().isEmpty()) {
                    if ( bomMapper.insertBomRecipe(recipe) <= 0 ) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }else{
                    if ( bomMapper.updateBomRecipe(recipe) <= 0 ) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        // 3. 제조공정 저장
        //저장전에 삭제건이 있는지 확인
        if (vo.getDeleteBomProc() != null && !vo.getDeleteBomProc().isEmpty()) {
            bomMapper.deleteBomProc(vo.getDeleteBomProc());
        }

        if (bomProcList != null && !bomProcList.isEmpty()) {
            for (BomProcVo proc : bomProcList) {
                proc.setBomId(bomId);
                proc.setUserId(userId);

                if( proc.getBomProcId() == null || proc.getBomProcId().isEmpty()) {
                    proc.setBomProcId(CommonUtil.generateUUID());

                    if ( bomMapper.insertBomProc(proc) <= 0 ) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }else{
                    if ( bomMapper.updateBomProc(proc) <= 0 ) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        return msg;
    }


    @Transactional(rollbackFor = BusinessException.class)
    public String saveBomVerInfo(BomRequestVo vo) {
        BomVo bomMst = vo.getBomInfo();
        List<BomRecipeVo> bomRecipeList = vo.getBomRecipeList();
        List<BomProcVo> bomProcList = vo.getBomProcList();

        String asBomId = bomMst.getAsBomId();
        bomMst.setBomId(CommonUtil.generateUUID());
        String userId = UserUtil.getUserId();

        bomMapper.updateBomVer(asBomId);

        // 1. BOM 저장
        if (bomMapper.insertBomMst(bomMst) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        // 2. 처방정보 저장
        if (bomRecipeList != null && !bomRecipeList.isEmpty()) {
            for (BomRecipeVo recipe : bomRecipeList) {
                recipe.setBomItemId(CommonUtil.generateUUID());
                recipe.setBomId(bomMst.getBomId());
                recipe.setUserId(userId);

                if (bomMapper.insertBomRecipe(recipe) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
        // 3. 제조공정 저장
        if (bomProcList != null && !bomProcList.isEmpty()) {
            for (BomProcVo proc : bomProcList) {
                proc.setBomProcId(CommonUtil.generateUUID());
                proc.setBomId(bomMst.getBomId());
                proc.setUserId(userId);

                if (bomMapper.insertBomProc(proc) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
        return "저장되었습니다.";
    }





}
