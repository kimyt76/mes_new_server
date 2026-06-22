package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProdOutRequestVo {

    private ProdOutVo prodOutInfo;
    private List<ProdOutItemVo> prodOutItemList;

}
