package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemTestMapper {

    /**
     * 시험내역별 조회
     * @param vo
     * @return
     */
    List<ItemTestVo> getItemTestNoList(ItemTestVo vo);

    /**
     * 시험번호내역 상세 조회
     * @param testNo
     * @return
     */
    ItemTestVo getItemTestNoInfo(@Param("testNo") String testNo);

    /**
     * 시험번호내역 업데이트
     * @param vo
     * @return
     */
    int updateItemTestNoInfo(ItemTestVo vo);





    /**
     * 테스트번호 여부 카운트
     * @param testNo
     * @return
     */
    int countTestNo(String testNo);

    /**
     * 시험내역 등록
     * @param item
     * @return
     */
    int insertItemTestNo(@Param("item") ItemTestVo item);

    /**
     * 구매에서 시험내역 수정
     * @param item
     * @return
     */
    int updateItemTestNo(@Param("item") ItemTestVo item);


    /**
     * 품질검사에서 시험진행상테 업데이트
     * @param qcTestMst
     * @return
     */
    int updateState(QcTestVo qcTestMst);
}
