package com.jct.mes_new.biz.common.mapper;

import com.jct.mes_new.biz.common.vo.FileVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Mapper
public interface FileHandlerMapper {

    boolean saveFile(FileVo fileVo);

    FileVo getAttachFile(@Param("attachFileId") String attachFileId);

    boolean deleteFile(@Param("attachFileId") String fileId);
}
