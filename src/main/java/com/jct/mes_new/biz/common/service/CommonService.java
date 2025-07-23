package com.jct.mes_new.biz.common.service;

import com.jct.mes_new.biz.common.vo.CommonVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommonService {

    List<CommonVo> getCodeList(String type);

    List<CommonVo> getCommonList(CommonVo commonVo);

    String newSeq(String itemTypeCd, String cd, int seqLen);

    CommonVo getCommonInfo(String comId);

    String saveCommonInfo(CommonVo commonVo);
}
