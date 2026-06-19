package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.vo.AdjustItemVo;
import com.jct.mes_new.biz.stock.vo.AdjustVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdjustMapper {

    List<AdjustVo> getAdjustList(AdjustVo vo);

    AdjustVo getAdjustMst(Long tranId);
    List<AdjustItemVo> getAdjustItemList(Long tranId);


    int insertAdjustMst(AdjustVo mst);

    int insertAdjustItemList(AdjustItemVo adjustItem);
    int updateAdjustItemList(AdjustItemVo adjustItem);
}
