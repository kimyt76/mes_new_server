package com.jct.mes_new.biz.common.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileHandlerServiceImpl implements FileHandlerService {

    private final FileHandlerMapper fileHandlerMapper;

    public FileVo getAttachFileInfo(String attachFileId, int seq){
        return fileHandlerMapper.getAttachFileInfo(attachFileId, seq);
    }

    public boolean saveFile(FileVo fileVo){
        return fileHandlerMapper.saveFile(fileVo);
    }

    public boolean deleteFile(String fileId, int seq) {
        return fileHandlerMapper.deleteFile(fileId, seq);
    }
}
