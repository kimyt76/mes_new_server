package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;

import java.util.List;

public interface QcTestTypeService {
    List<QcTestTypeVo> getQcTestTypeList(QcTestTypeVo vo);

    List<QcTestTypeVo> getQcTestTypeMethod(String itemCd);

    String saveQcTestTypeMethod(List<QcTestTypeVo> list);
}
