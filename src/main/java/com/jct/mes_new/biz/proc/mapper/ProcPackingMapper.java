package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcPackingVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcPackingMapper {


    int startProcPacking(ProcPackingVo vo);

    ProcCoatingVo getPackingHeadInfo(Long workProcId);
}
