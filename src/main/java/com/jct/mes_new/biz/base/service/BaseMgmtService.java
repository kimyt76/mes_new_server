package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.ItemVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BaseMgmtService {
    List<ItemVo> getItemList(ItemVo itemVo);
}
