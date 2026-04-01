package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;

import java.util.List;

public interface QcTestService {

    int insertQcTest(List<QcTestVo> qcTestList, Long tranId, String userId);

    List<QcTestVo> getQcTestList(QcTestVo vo);

    int updateQcTest(List<QcTestVo> qcTestList, String userId);

    QcTestVo getQcTestDetailInfo(Long qcTestId);

    QcTestRequestVo getQcTestInfo(Long qcTestId);

    String updateQcTestDetailInfo(QcTestVo vo);

    String insertRetestInfo(QcTestVo vo);

    String updateQcTestInfo(QcTestRequestVo vo);

    byte[] getPrintTest(List<Long> qcTestIds) throws Exception;

    byte[] certificateDownloadExcel(Long qcTestId);

    byte[] tesetDownloadExcel(Long qcTestId);
}
