package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.stock.vo.RealStockItemVo;
import com.jct.mes_new.biz.stock.vo.RealStockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RealStockMapper {

    List<RealStockVo> getRealStockList(RealStockVo vo);
    List<RealStockItemVo> getRealStockItemList(Long realStockMstId);

    Long saveRealStock(RealStockVo vo);
    int realStockItemCnt(Long realStockMstId);

    RealStockVo getRealStockMst(Long realStockMstId);
    List<RealStockItemVo> getDocStockItemList(RealStockVo vo);

    List<TranItemVo> getInvTranItemList(TranItemVo vo);


    int insertRealStockItem(RealStockItemVo stockItem);
    int updateRealStockItem(RealStockItemVo stockItem);

    int updateRealStockComplete(RealStockVo mst);
}
