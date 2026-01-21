package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcWeighMapper {
    List<ProcWeighVo> getWeighList(ProcWeighVo vo);
}
