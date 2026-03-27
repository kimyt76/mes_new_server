package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.qc.mapper.QcProcTestMapper;
import com.jct.mes_new.biz.qc.service.QcProcTestService;
import com.jct.mes_new.biz.qc.vo.QcProcTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QcProcTestServiceImpl implements QcProcTestService {

    private final QcProcTestMapper qcProcTestMapper;


    public List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo){
        return qcProcTestMapper.getQcProcTestList(vo);
    }

    public String createQcProcTestInfo(QcProcTestVo vo){
        vo.setUserId(UserUtil.getUserId());

        if(qcProcTestMapper.createQcProcTestInfo(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "저장했습니다.";
    }
}
