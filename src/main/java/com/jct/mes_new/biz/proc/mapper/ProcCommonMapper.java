package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcCommonMapper {
    List<ProcCommonVo> getWorkerList(String procCd);

    List<ProcCommonVo> getEquipmentList(String storageCd);
}
