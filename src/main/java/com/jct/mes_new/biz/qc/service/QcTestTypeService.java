package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;

import java.util.List;
import java.util.Map;

public interface QcTestTypeService {
    List<QcTestTypeVo> getQcTestTypeList(QcTestTypeVo vo);

    List<QcTestTypeVo> getQcTestTypeMethod(String itemCd);

    String saveQcTestTypeMethod(QcTestRequestVo vo);

    List<QcTestTypeVo> getQcTestTypeMethodComp(QcTestTypeVo vo);
}
