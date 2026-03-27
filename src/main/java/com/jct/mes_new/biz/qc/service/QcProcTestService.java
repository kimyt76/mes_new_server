package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.QcProcTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;

import java.util.List;

public interface QcProcTestService {
    List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo);

    String createQcProcTestInfo(QcProcTestVo vo);
}
