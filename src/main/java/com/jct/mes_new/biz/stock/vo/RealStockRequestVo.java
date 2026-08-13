package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.util.List;

@Data
public class RealStockRequestVo {
    RealStockVo realStock;
    List<RealStockItemVo> realStockItemList;
}
