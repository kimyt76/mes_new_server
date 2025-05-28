package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    private final FileHandlerMapper fileHandlerMapper;

    public boolean saveFile(FileVo fileVo){
        return fileHandlerMapper.saveFile(fileVo);
    }

    public boolean deleteFile(String fileId) {
        log.info(("==================fileId============== : " + fileId));
        return fileHandlerMapper.deleteFile(fileId);
    }
}
