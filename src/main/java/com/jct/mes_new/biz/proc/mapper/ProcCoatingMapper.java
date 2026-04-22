package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcCoatingMapper {
    List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo);

    int startProcCoating(ProcCommonVo vo);
}
