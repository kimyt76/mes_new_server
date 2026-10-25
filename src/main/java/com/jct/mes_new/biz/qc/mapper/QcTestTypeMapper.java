package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.QcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.TestTypeMethodVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QcTestTypeMapper {

    /*품질검사유형 조회 */
    List<TestTypeMethodVo> getTestTypeMethodList(TestTypeMethodVo vo);
    
    /*품질검사유형 상세 */
    List<TestTypeMethodVo> getTestTypeMethod(String itemCd);

    int insertTestTypeMethod(TestTypeMethodVo item);
    int updateTestTypeMethod(TestTypeMethodVo item);


    /*품질검사유형*/
    void deleteTestTypeMethod(List<Long> deleteIds);


    List<QcTestTypeMethodVo> getQcTestTypeMethodComp(QcTestTypeMethodVo vo);

    List<QcTestTypeMethodVo> getQcTestTypeMethodList(Long qcTestId);

    List<QcTestTypeMethodVo> getTestTypeMethodList(String itemCd);




}
