package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.common.vo.CommonVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommonMapper {

    List<CommonVo> getCommonList(CommonVo commonVo);

    List<CommonVo> getCodeList(@Param("comTypeCd") String type);

    String newSeq(String itemTypeCd, String cd, int seqLen);

    CommonVo getCommonInfo(String comId);

    int saveCommonInfo(CommonVo commonVo);

    int getNextSeq(String tb, String cd, String date);
}
