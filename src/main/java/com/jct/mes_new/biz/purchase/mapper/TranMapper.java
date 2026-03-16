package com.jct.mes_new.biz.purchase.mapper;

import com.jct.mes_new.biz.purchase.vo.TranVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TranMapper {

    /* 신규등록*/
    Long insertTranMst(TranVo mst);

    /* 수정*/
    int updateTranMst(TranVo mst);
    void deleteItemList(Long tranId, List<Long> deletedItemIds);
    int insertTranItem(TranVo.TranItemListVo item);
    int updateTranItem(TranVo.TranItemListVo item);

    /* tranId 조회*/
    Long getTranId(Long purId);

    /* 원장 삭제*/
    void deleteTranItem(Long tranId);
    void deleteTranMst(Long tranId);

    /* 원장 품목 조회*/
    List<TranVo.TranItemListVo> getTranItemListByTranId(Long tranId);
}
