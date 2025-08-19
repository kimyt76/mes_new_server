package com.jct.mes_new.biz.common.controller;


import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.config.common.FileUpload;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.FileInfo;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileHandlerController {

    private final FileHandlerService fileHandlerService;

    @GetMapping("/download")
    public ResponseEntity<Resource> download (@RequestParam String attachFileId, @RequestParam int seq) throws IOException {
        FileVo fileInfo = fileHandlerService.getAttachFileInfo(attachFileId, seq); // DB 조회

        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(fileInfo.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String encodedFileName = UriUtils.encode(fileInfo.getFileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                .body(resource);
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@RequestParam String fileId, @RequestParam int seq) throws Exception {
        String msg = "삭제되었습니다.";

        if( !fileHandlerService.deleteFile(fileId, seq)){
            throw new Exception("삭제중 오류가 발생했습니다.");
        }
        return msg;
    }

    /**
     * 엑셀파일 만들기  (List용)
     */
    @GetMapping("/excel/download")
    public void downloadExcel(HttpServletResponse response) throws IOException {
        String fileName = "";

        List<String[]> dummyData = List.of(
                new String[]{"ID", "Name", "Email"},
                new String[]{"1", "Kim", "kim@example.com"},
                new String[]{"2", "Lee", "lee@example.com"}
        );

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

        FileUpload.exportToExcel(dummyData).transferTo(response.getOutputStream());
    }
}
