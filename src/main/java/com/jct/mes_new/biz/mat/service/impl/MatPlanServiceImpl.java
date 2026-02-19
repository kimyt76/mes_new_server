package com.jct.mes_new.biz.mat.service.impl;

import com.jct.mes_new.biz.mat.mapper.MatPlanMapper;
import com.jct.mes_new.biz.mat.service.MatPlanService;
import com.jct.mes_new.biz.mat.vo.MatPlanVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MatPlanServiceImpl implements MatPlanService {

    private final MatPlanMapper matPlanMapper;

    public List<MatPlanVo> getMatPlanList(MatPlanVo vo) {
        return matPlanMapper.getMatPlanList(vo);
    }
    public List<MatPlanVo> getMatPlanDetailList(String matPlanId) {
        return matPlanMapper.getMatPlanDetailList(matPlanId);
    }


    public void saveMatPlanList(List<MatPlanVo> voList) {
        String userId = UserUtil.getUserId();

        for (MatPlanVo vo : voList) {
            vo.setUserId(userId);
log.info("=================getUserId===================== : " + vo.getUserId());
log.info("===================getMatPlanId=================== : " + vo.getMatPlanId());
            if ( "".equals(vo.getMatPlanId()) || vo.getMatPlanId() == null ) {
                if (matPlanMapper.saveMatPlanList(vo) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if ( matPlanMapper.updateMatPlan(vo) <= 0 ) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }


}