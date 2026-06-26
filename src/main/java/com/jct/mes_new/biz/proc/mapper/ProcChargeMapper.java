package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcChargeMapper {

    int startProcCharge(ProcChargeVo vo);

}
