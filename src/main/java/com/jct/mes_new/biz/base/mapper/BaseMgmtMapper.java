package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.ItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseMgmtMapper {

    List<ItemVo> getItemList(ItemVo itemVo);
}
