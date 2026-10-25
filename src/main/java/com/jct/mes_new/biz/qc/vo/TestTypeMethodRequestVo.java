package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.util.List;

@Data
public class TestTypeMethodRequestVo {
    private QcTestVo qcTestInfo;
    private List<TestTypeMethodVo> testTypeMethodList;
    private List<Long> deleteIds;

}
