package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.QcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public String saveQcTestTypeMethod(QcTestRequestVo vo){
        String itemCd = vo.getQcTestTypeMethodList().get(0).getItemCd();
        String userId = UserUtil.getUserId();

        //삭제 건이 있을 경우 먼저 처리
        List<Long> getDeleteIds = vo.getDeleteIds();
        if (getDeleteIds != null && !getDeleteIds.isEmpty()) {
            qcTestTypeMapper.deleteQcTestTypeMethod(itemCd,getDeleteIds);
        }

        for(QcTestTypeVo item : vo.getQcTestTypeMethodList()){
            item.setUserId(userId);

            if ( item.getTestTypeMethodId() == null ){
                if(qcTestTypeMapper.insertTestTypeMethod(item) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if(qcTestTypeMapper.updateTestTypeMethod(itemCd, item) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return "저장되었습니다.";
    }



    public List<QcTestTypeVo> getQcTestTypeMethodComp(QcTestTypeVo vo){
        return  qcTestTypeMapper.getQcTestTypeMethodComp(vo );
    }
}
