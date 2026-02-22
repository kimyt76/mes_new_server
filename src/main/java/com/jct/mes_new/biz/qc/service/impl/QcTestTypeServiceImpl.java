package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.QcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
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
public class QcTestTypeServiceImpl implements QcTestTypeService {

    private final QcTestTypeMapper qcTestTypeMapper;

    public List<QcTestTypeVo> getQcTestTypeList(QcTestTypeVo vo){
        return qcTestTypeMapper.getQcTestTypeList(vo);
    }

    public List<QcTestTypeVo> getQcTestTypeMethod(String itemCd){
        return qcTestTypeMapper.getQcTestTypeMethod(itemCd);
    }

    public String saveQcTestTypeMethod(List<QcTestTypeVo> list){
        String msg ="저장되었습니다.";
        String itemCd = list.get(0).getItemCd();
        String userId = UserUtil.getUserId();

        qcTestTypeMapper.deleteQcTestTypeMethod(itemCd);
        for(QcTestTypeVo vo : list){
            vo.setUserId(userId);
        }
        if(qcTestTypeMapper.saveQcTestTypeMethod(list) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        return msg;
    }
}
