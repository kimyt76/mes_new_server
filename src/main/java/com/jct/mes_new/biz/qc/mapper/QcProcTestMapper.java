package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QcProcTestMapper {
    List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo);

    int createQcProcTestInfo(QcProcTestVo vo);


    QcProcTestVo getQcProcTestMst(Long workBatchId, String procCd);
    QcProcSampleVo getQcProcSample(String testType, Long qcProcTestMstId);
    List<QcProcMethodVo> getMethodList(String testType, Long qcProcTestMstId);
    List<QcProcDetailVo> getDetailList(String testType, Long qcProcTestMstId);
    List<QcProcLinelVo> getLineList(String testType, Long qcProcTestMstId);

    int updateQcProcTest(QcProcTestVo mst);
    int insertQcProcTestSample(QcProcSampleVo sample);
    int updateQcProcTestSample(QcProcSampleVo sample);
    int insertQcProcTestMethod(QcProcMethodVo method);
    int updateQcProcTestMethod(QcProcMethodVo method);
    int insertQcProcTestDetail(QcProcDetailVo detail);
    int updateQcProcTestDetail(QcProcDetailVo detail);


    int insertQcProcTestLine(QcProcLinelVo lineVo);
    int updateQcProcTestLine(QcProcLinelVo lineVo);

    QcProcTestVo getQcProcTestInfo(Long qcProcTestMstId);

    WorkOrderInfoVo getProcItem(Long workBatchId, String procCd);
}
