package com.jct.mes_new.biz.lab.service;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.lab.vo.MaterialRequestVo;
import com.jct.mes_new.biz.lab.vo.MaterialVo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaterialService {
    MaterialRequestVo getMaterialInfo(String itemCd);

    String saveMaterialInfo(MaterialRequestVo vo);

    List<ItemVo> getMaterialItemList(MaterialVo vo);
}
