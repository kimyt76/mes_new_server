package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.qc.vo.QcProcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcProcTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;

import java.util.List;

public interface QcProcTestService {
    List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo);

    String createQcProcTestInfo(QcProcTestVo vo);

    QcProcTestRequestVo getQcProcTestTabInfo(QcProcTestVo vo);

    String saveQcProcTestTabInfo(QcProcTestRequestVo vo);

    String saveQcProcTestLineList(QcProcTestRequestVo vo);

    byte[] downloadQcProcTest(Long qcProcTestMstId) throws Exception;
}
