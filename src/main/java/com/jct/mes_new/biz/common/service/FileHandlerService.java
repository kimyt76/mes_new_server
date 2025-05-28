package com.jct.mes_new.biz.common.service;

import com.jct.mes_new.biz.common.vo.FileVo;
import org.springframework.stereotype.Service;


@Service
public interface FileHandlerService {

    boolean saveFile(FileVo fileVo);

    boolean deleteFile(String fileId);
}
