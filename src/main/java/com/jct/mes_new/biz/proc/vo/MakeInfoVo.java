package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.util.List;

@Data
public class MakeInfoVo {

    ProcMakeVo procMake;
    List<ProcWeighBomVo> makeBomList;
}
