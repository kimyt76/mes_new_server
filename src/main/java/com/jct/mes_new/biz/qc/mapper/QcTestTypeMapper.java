package com.jct.mes_new.biz.qc.mapper;

import com.jct.mes_new.biz.qc.vo.QcTestTypeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QcTestTypeMapper {
    List<QcTestTypeVo> getQcTestTypeList(QcTestTypeVo vo);

    List<QcTestTypeVo> getQcTestTypeMethod(String itemCd);

    void deleteQcTestTypeMethod(@Param("itemCd") String itemCd, @Param("deleteIds") List<Long> deleteIds);

    int insertTestTypeMethod(QcTestTypeVo item);
    int updateTestTypeMethod(@Param("itemCd") String itemCd, @Param("item") QcTestTypeVo item);
}
