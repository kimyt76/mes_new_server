package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.QcTestTypeMapper;
import com.jct.mes_new.biz.qc.service.QcTestTypeService;
import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodRequestVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodVo;
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

    /*검사유형 리스트 조회 */
    public List<TestTypeMethodVo> getTestTypeMethodList(TestTypeMethodVo vo){
        return qcTestTypeMapper.getTestTypeMethodList(vo);
    }

    /*검사유형 리스트 상세 */
    public List<TestTypeMethodVo> getTestTypeMethod(String itemCd){
        return qcTestTypeMapper.getTestTypeMethod(itemCd);
    }

    public String saveTestTypeMethod(TestTypeMethodRequestVo vo){
        String userId = UserUtil.getUserId();

        //삭제 건이 있을 경우 먼저 처리
        List<Long> getDeleteIds = vo.getDeleteIds();
        if (getDeleteIds != null && !getDeleteIds.isEmpty()) {
            qcTestTypeMapper.deleteTestTypeMethod(getDeleteIds);
        }

        for(TestTypeMethodVo item : vo.getTestTypeMethodList()){
            item.setUserId(userId);

            if ( item.getTestTypeMethodId() == null ){
                if(qcTestTypeMapper.insertTestTypeMethod(item) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if(qcTestTypeMapper.updateTestTypeMethod(item) <=0 ) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return "저장되었습니다.";
    }

    public String deleteTestTypeMethod(TestTypeMethodRequestVo vo){
        List<Long> deleteIds = vo.getDeleteIds();
        String itemCd = vo.getQcTestInfo().getItemCd();
        qcTestTypeMapper.deleteTestTypeMethod(deleteIds);
        return " 전체 삭제되었습니다.";
    }


    public List<QcTestTypeMethodVo> getQcTestTypeMethodComp(QcTestTypeMethodVo vo){
        return  qcTestTypeMapper.getQcTestTypeMethodComp(vo );
    }
}
