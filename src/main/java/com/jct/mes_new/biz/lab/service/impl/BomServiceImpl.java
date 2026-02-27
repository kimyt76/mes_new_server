package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.BomMapper;
import com.jct.mes_new.biz.lab.service.BomService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
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

    @Transactional  // RuntimeException 발생 시 자동 롤백
    public String saveBomInfo(BomVo bomInfo, List<BomRecipeVo> bomRecipeList, List<BomProcVo> bomProcList) {
        String bomId = bomInfo.getBomId();
        String msg = "저장되었습니다.";
        String userId = bomInfo.getUserId();

        if (bomId == null || bomId.isEmpty()) {
            bomId = CommonUtil.generateUUID();
            bomInfo.setBomId(bomId);
        }
        // 1. BOM 저장
        if (bomMapper.saveBomInfo(bomInfo) <= 0) {
            throw new RuntimeException("BOM 저장에 실패했습니다.");
        }
        // 2. 처방정보 저장
        if (bomRecipeList != null && !bomRecipeList.isEmpty()) {
            bomMapper.deleteBomRecipeList(bomId);

            for (BomRecipeVo recipe : bomRecipeList) {
                recipe.setBomItemId(CommonUtil.generateUUID());
                recipe.setBomId(bomId);
                recipe.setUserId(userId);

                if (bomMapper.saveBomRecipeList(recipe) <= 0) {
                    throw new RuntimeException("처방정보 저장에 실패했습니다.");
                }
            }
        }
        // 3. 제조공정 저장
        if (bomProcList != null && !bomProcList.isEmpty()) {
            bomMapper.deleteBomProcList(bomId);

            for (BomProcVo proc : bomProcList) {
                proc.setBomProcId(CommonUtil.generateUUID());
                proc.setBomId(bomId);
                proc.setUserId(userId);

                if (bomMapper.saveBomProcList(proc) <= 0) {
                    throw new RuntimeException("제조공정 저장에 실패했습니다.");
                }
            }
        }
        return msg;
    }


    @Transactional  // RuntimeException 발생 시 자동 롤백
    public String saveBomVerInfo(BomVo bomInfo, List<BomRecipeVo> bomRecipeList, List<BomProcVo> bomProcList) {
        String asBomId = bomInfo.getAsBomId();
        bomInfo.setBomId(CommonUtil.generateUUID());
        String msg = "저장되었습니다.";
        String userId = bomInfo.getUserId();

        bomMapper.updateBomVer(asBomId);

        // 1. BOM 저장
        if (bomMapper.saveBomInfo(bomInfo) <= 0) {
            throw new RuntimeException("BOM 저장에 실패했습니다.");
        }
        // 2. 처방정보 저장
        if (bomRecipeList != null && !bomRecipeList.isEmpty()) {
            for (BomRecipeVo recipe : bomRecipeList) {
                recipe.setBomItemId(CommonUtil.generateUUID());
                recipe.setBomId(bomInfo.getBomId());
                recipe.setUserId(userId);

                if (bomMapper.saveBomRecipeList(recipe) <= 0) {
                    throw new RuntimeException("처방정보 저장에 실패했습니다.");
                }
            }
        }
        // 3. 제조공정 저장
        if (bomProcList != null && !bomProcList.isEmpty()) {
            for (BomProcVo proc : bomProcList) {
                proc.setBomProcId(CommonUtil.generateUUID());
                proc.setBomId(bomInfo.getBomId());
                proc.setUserId(userId);

                if (bomMapper.saveBomProcList(proc) <= 0) {
                    throw new RuntimeException("제조공정 저장에 실패했습니다.");
                }
            }
        }
        return msg;
    }

}
