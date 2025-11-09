package com.jct.mes_new.biz.lab.service;

import com.jct.mes_new.biz.lab.vo.SampleVo;

import java.util.List;

public interface SampleService {
    List<SampleVo> getSampleList(SampleVo sampleVo);

    SampleVo getSampleInfo(String sampleId);

    String saveSampleInfo(SampleVo vo) throws Exception;

    String getNextProdMgmtNo();
}
