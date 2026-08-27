package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.ProdVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProdMapper {
    List<ProdVo> getProdPerformanc(ProdVo vo);

    List<ProdVo> getProdCompany(ProdVo vo);
}
