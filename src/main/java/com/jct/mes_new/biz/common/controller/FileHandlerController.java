package com.jct.mes_new.biz.common.controller;


import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.config.common.FileUpload;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/uploadFile")
public class FileHandlerController {

    private final FileHandlerService fileHandlerService;

    @DeleteMapping("/deleteFile/{fileId}")
    public String deleteFile(@PathVariable String fileId) throws Exception {
        String msg = "삭제되었습니다.";

        if( !fileHandlerService.deleteFile(fileId)){
            throw new Exception("삭제중 오류가 발생했습니다.");
        }

        return msg;
    }

    /**
     * 첨부파일 단건 용
     * @param file
     * @return
     */
    @PostMapping("/saveFile")
    public Map<String, Object> singleFileUpload(@RequestParam("files") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> uploadedFiles = new ArrayList<>();
        FileVo fileVo = new FileVo();

        int seq = 0;
        String baseDir = "d:/uploads/"; // 상대 경로 (project root/uploads)

        //path 경로 년 월 일 폴더
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        String realPath = Paths.get(baseDir, year, month).toString();
        //String datePath = ROOT_DIR + File.separator + year + File.separator + month + File.separator + day;
        System.out.println("===============realPath========= : " +realPath);
        File dir = new File(realPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        //파일 id
        String attachFileId = UUID.randomUUID().toString();

        if (!file.isEmpty()) {
            try {
                //String filePath = realPath+"_" + file.getOriginalFilename();
                String realFileName = year+month+day+"_"+file.getOriginalFilename();
                System.out.println("===============realFileName========= : " +realFileName);
                String filePath = realPath+"_" + file.getOriginalFilename();
                System.out.println("===============filePath========= : " +filePath);
                seq = seq++;
                System.out.println("===============seq========= : " +seq);
                String fileName = file.getOriginalFilename();
                System.out.println("===============fileName========= : " +fileName);
                long fileSize = file.getSize();
                System.out.println("===============fileSize========= : " +fileSize);

                fileVo.setAttachFileId(attachFileId);
                fileVo.setSeq(seq);
                fileVo.setFileName(fileName);
                fileVo.setRealFileName(realFileName);
                fileVo.setFilePath(filePath);
                fileVo.setFileSize(fileSize);

                file.transferTo(new File(filePath));

                uploadedFiles.add(file.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
                result.put("error", "파일 업로드 실패: " + file.getOriginalFilename());
            }
        }

        if (fileHandlerService.saveFile(fileVo) ) {
            result.put(attachFileId, attachFileId);
        }else{
            result.put("error", "파일 DB 등록 실패: " );
        }

        result.put("uploaded", uploadedFiles);
        return result;
    }

    /**
     * 첨부파일 list 용
     * @param files
     * @return
     */
    @PostMapping("/multiSaveFile")
    public Map<String, Object> multiFileUpload(@RequestParam("files") List<MultipartFile> files) {
        Map<String, Object> result = new HashMap<>();
        List<String> uploadedFiles = new ArrayList<>();
        FileVo fileVo = new FileVo();

        int seq = 0;
        String baseDir = "d:/uploads/"; // 상대 경로 (project root/uploads)

        //path 경로 년 월 일 폴더
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        String realPath = Paths.get(baseDir, year, month).toString();
        //String datePath = ROOT_DIR + File.separator + year + File.separator + month + File.separator + day;
        System.out.println("===============realPath========= : " +realPath);
        File dir = new File(realPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        //파일 id
        String attachFileId = UUID.randomUUID().toString();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    //String filePath = realPath+"_" + file.getOriginalFilename();
                    String realFileName = year+month+day+"_"+file.getOriginalFilename();
                    System.out.println("===============realFileName========= : " +realFileName);
                    String filePath = realPath+"_" + file.getOriginalFilename();
                    System.out.println("===============filePath========= : " +filePath);
                    seq = seq++;
                    System.out.println("===============seq========= : " +seq);
                    String fileName = file.getOriginalFilename();
                    System.out.println("===============fileName========= : " +fileName);
                    long fileSize = file.getSize();
                    System.out.println("===============fileSize========= : " +fileSize);

                    fileVo.setAttachFileId(attachFileId);
                    fileVo.setSeq(seq);
                    fileVo.setFileName(fileName);
                    fileVo.setRealFileName(realFileName);
                    fileVo.setFilePath(filePath);
                    fileVo.setFileSize(fileSize);

                    file.transferTo(new File(filePath));

                    uploadedFiles.add(file.getOriginalFilename());

                } catch (IOException e) {
                    e.printStackTrace();
                    result.put("error", "파일 업로드 실패: " + file.getOriginalFilename());
                }
            }
        }

        if (fileHandlerService.saveFile(fileVo) ) {
            result.put(attachFileId, attachFileId);
        }else{
            result.put("error", "파일 DB 등록 실패: " );
        }

        result.put("uploaded", uploadedFiles);
        return result;
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
