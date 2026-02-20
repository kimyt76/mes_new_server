package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class ProcTestTypeVo {
    private BigInteger procTestTypeId;
    private String testType;
    private String testTypeName;
    private String etc;
    private String userId;
}
