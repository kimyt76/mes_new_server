package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.common.mapper.CommonMapper;
import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.CommonVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {

    private final CommonMapper commonMapper;

    public List<CommonVo> getCommonList(CommonVo commonVo){
        return commonMapper.getCommonList(commonVo);
    }


    public List<CommonVo> getCodeList(String type){
        return commonMapper.getCodeList(type);
    }

}
