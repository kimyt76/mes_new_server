package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QcTestTypeMapper {
    List<QcTestTypeVo> getQcTestTypeList(QcTestTypeVo vo);

    List<QcTestTypeVo> getQcTestTypeMethod(String itemCd);

    void deleteQcTestTypeMethod(String itemCd);

    int saveQcTestTypeMethod(List<QcTestTypeVo> list);
}
