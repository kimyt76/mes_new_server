package com.jct.mes_new.biz.stock.service.impl;

import com.jct.mes_new.biz.stock.mapper.MoveStockMapper;
import com.jct.mes_new.biz.stock.service.MoveStockService;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MoveStockServiceImpl implements MoveStockService {

    private final MoveStockMapper moveStockMapper;

    public List<MoveStockVo> getMoveStockList(MoveStockVo vo){
        return moveStockMapper.getMoveStockList(vo);
    }



}
