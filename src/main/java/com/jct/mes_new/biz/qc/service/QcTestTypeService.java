package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.QcTestRequestVo;
import com.jct.mes_new.biz.qc.vo.QcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodRequestVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodVo;

import java.util.List;

public interface QcTestTypeService {
    /*품질검사유형 리스트 조회 */
    List<TestTypeMethodVo> getTestTypeMethodList(TestTypeMethodVo vo);

    /* 품질검사유형상세 */
    List<TestTypeMethodVo> getTestTypeMethod(String itemCd);

    /* 품질검사유형 저장 */
    String saveTestTypeMethod(TestTypeMethodRequestVo vo);

    /* 품질검사유형 전체삭제 */
    String deleteTestTypeMethod(TestTypeMethodRequestVo vo);

    /* 품질검사비교*/
    List<QcTestTypeMethodVo> getQcTestTypeMethodComp(QcTestTypeMethodVo vo);

}
