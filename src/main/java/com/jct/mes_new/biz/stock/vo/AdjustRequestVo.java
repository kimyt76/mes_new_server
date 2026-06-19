package com.jct.mes_new.biz.stock.vo;

import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import lombok.Data;

import java.util.List;

@Data
public class AdjustRequestVo {

    private AdjustVo adjustInfo;
    private List<AdjustItemVo> adjustItemList;

}
