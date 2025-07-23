package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.common.mapper.CommonMapper;
import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.vo.CommonVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

    public String newSeq(String itemTypeCd, String cd, int seqLen){
        return commonMapper.newSeq(itemTypeCd , cd, seqLen);
    }

    public CommonVo getCommonInfo(String comId){
        return commonMapper.getCommonInfo(comId);
    }

    public String saveCommonInfo(CommonVo commonVo){
        String msg ="저장되었습니다.";

        try{
            if ( commonMapper.saveCommonInfo(commonVo) <= 0 ) {
                throw new Exception("저장에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("저장 실패 : " + e.getMessage(), e );
        }
        return msg;
    }

}
