package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.QcProcTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QcProcTestMapper {
    List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo);

    int createQcProcTestInfo(QcProcTestVo vo);
}
