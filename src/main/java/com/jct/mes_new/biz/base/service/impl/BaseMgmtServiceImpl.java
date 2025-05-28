package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.BaseMgmtMapper;
import com.jct.mes_new.biz.base.service.BaseMgmtService;
import com.jct.mes_new.biz.base.vo.ItemVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BaseMgmtServiceImpl implements BaseMgmtService {

    private final BaseMgmtMapper baseMgmtMapper;

    public List<ItemVo> getItemList(ItemVo itemVo){

        return baseMgmtMapper.getItemList(itemVo);
    }
}
