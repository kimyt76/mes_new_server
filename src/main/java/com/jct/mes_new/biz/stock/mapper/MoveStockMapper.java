package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MoveStockMapper {

    List<MoveStockVo> getMoveStockList(MoveStockVo vo);





}
