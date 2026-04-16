package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.QcTestVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface QcTestMapper {


    /**
     * 품질검사 요청 조회
     * @param vo
     * @return
     */
    List<QcTestVo> getQcTestList(QcTestVo vo);

    /**
     * 품질검사 상세 
     * @param qtTestId
     * @return
     */
    QcTestVo getQcTestDetailInfo(@Param("qcTestId") Long qtTestId);
    
    //다건
    int insertQcTest(@Param("list") List<QcTestVo> qcTestList, @Param("userId") String userId);
    //1건
    int insertSingleQcTest(@Param("list") QcTestVo qcTest, @Param("userId") String userId);

    /**
     * 품질검사 테스트 번호 존재 여부
     * @param testNo
     * @param reqDate
     * @return
     */
    int getTestNoCnt(String testNo, LocalDate reqDate);

    /**
     * 구매 수정 시 품질검사 업데이트
     *
     * @param vo
     * @return
     */
    int updateQcTestInfo(@Param("vo") QcTestVo vo);


    /**
     * 품질검사요청 업데이트
     * @param vo
     * @return
     */
    int updateQcTestDetailInfo(QcTestVo vo);

    int insertRetestInfo(QcTestVo vo);

    int updateQcTestAllInfo(QcTestVo qcTesteMst);
}
