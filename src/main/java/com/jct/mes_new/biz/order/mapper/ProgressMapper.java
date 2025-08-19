package com.jct.mes_new.biz.order.mapper;

import com.jct.mes_new.biz.order.vo.ProgressVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProgressMapper {
    List<ProgressVo> getProgressList(ProgressVo progressVo);
}
