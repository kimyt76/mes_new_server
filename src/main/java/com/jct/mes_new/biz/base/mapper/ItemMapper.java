package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.ItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
}
