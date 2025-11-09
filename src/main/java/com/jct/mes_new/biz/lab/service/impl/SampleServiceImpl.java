package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.SampleMapper;
import com.jct.mes_new.biz.lab.service.SampleService;
import com.jct.mes_new.biz.lab.vo.SampleVo;
import com.jct.mes_new.config.common.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleServiceImpl implements SampleService {

    private final SampleMapper sampleMapper;

    public List<SampleVo> getSampleList(SampleVo sampleVo){
        return sampleMapper.getSampleList(sampleVo);
    }

    public SampleVo getSampleInfo(String sampleId) {
        return  sampleMapper.getSampleInfo(sampleId);
    }

    public String saveSampleInfo(SampleVo vo) throws Exception{
        String msg ="저장되었습니다.";

        int result = sampleMapper.saveSampleInfo(vo);
        if ( result <= 0  ) {
            throw new Exception("샘플 저장에 실패했습니다.");
        }

        if (vo.getSampleId() != null) {
            System.out.println("▶ 생성된 Sample ID: " + vo.getSampleId());
        } else {
            System.out.println("▶ Sample ID 반환되지 않음 (INSERT 시 확인 필요)");
        }
        return msg;
    }

    public String getNextProdMgmtNo(){
        return sampleMapper.nextSeq();
    }
}
