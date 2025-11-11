package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.base.vo.PriceHistoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ItemMapper {
    public List<ItemVo> getProdLList();

    public List<ItemVo> getProdMList(@Param("lcode") String id);

    int saveItemInfo(ItemVo itemVo);

    int updateItemInfo(ItemVo itemVo);

    List<ItemVo> getItemList(ItemVo itemVo);

    ItemVo getItemInfo(String itemCd);

    int getItemCdCheck(String itemCd);

    int saveItemDetailInfo(ItemVo itemVo);

    void insertItemDetial(String itemCd, String userId);

    void insertPriceHistory(String itemCd, String priceType, BigDecimal price, String userId);

    void updateItemPrice(String itemCd, BigDecimal inPrice, BigDecimal outPrice, String userId);

    List<PriceHistoryVo> getItemPriceHistory(String itemCd);
}
