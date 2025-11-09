package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.SampleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SampleMapper {

    List<SampleVo> getSampleList(SampleVo sampleVo);

    SampleVo getSampleInfo(String sampleId);

    int saveSampleInfo(SampleVo vo);

    String nextSeq();
}
