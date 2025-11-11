package com.jct.mes_new.biz.work.mapper;

import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WorkOrderMapper {
    List<WorkOrderVo> getWorkOrderList(WorkOrderVo vo);
}
