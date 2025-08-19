package com.jct.mes_new.biz.order.service;

import com.jct.mes_new.biz.order.vo.ProgressVo;

import java.util.List;

public interface ProgressService {
    List<ProgressVo> getProgressList(ProgressVo progressVo);
}
