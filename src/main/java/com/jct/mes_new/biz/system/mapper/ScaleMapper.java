package com.jct.mes_new.biz.system.mapper;

import com.jct.mes_new.biz.system.vo.ScaleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScaleMapper {
    List<ScaleVo> getScaleList(ScaleVo scaleVo);

    ScaleVo getScaleInfo(@Param("scaleId") String scaleId);

    ScaleVo saveScaleInfo(ScaleVo vo);
}
