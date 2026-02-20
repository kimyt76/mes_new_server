package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.ProcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcTestTypeMapper {

    List<ProcTestTypeVo> getProcTestTypeList();
    List<ProcTestTypeMethodVo> getProcTestTypeMethod(String testType);

    int saveProcTestTypeMethod(ProcTestTypeMethodVo vo);

    ProcTestTypeMethodVo getProcTestTypeMethodInfo(String procTestTypeMethodId);
}
