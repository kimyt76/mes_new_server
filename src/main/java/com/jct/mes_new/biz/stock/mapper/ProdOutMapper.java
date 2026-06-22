package com.jct.mes_new.biz.stock.mapper;

import com.jct.mes_new.biz.stock.vo.ProdOutItemVo;
import com.jct.mes_new.biz.stock.vo.ProdOutVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProdOutMapper {
    List<ProdOutVo> getProdOutList(ProdOutVo vo);

    ProdOutVo getProdOutMst(Long tranId);

    List<ProdOutItemVo> getProdOutItemList(Long tranId);

    int insertProdOutMst(ProdOutVo mst);

    int insertProdOutItemList(ProdOutItemVo prodOutItem);

    int updateProdOutItemList(ProdOutItemVo prodOutItem);
}
