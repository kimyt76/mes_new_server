package com.jct.mes_new.config.common;

import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@UtilityClass
@Slf4j
public class FileUpload {

    /**
     * 첨부파일 다운로드
     * @param filepath
     * @return
     * @throws IOException
     */
    @GetMapping("/files/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filepath) throws IOException {
        Path path = Paths.get(filepath).normalize();

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(path.toUri());
        String fileName = path.getFileName().toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     *  첨부파일 단건 업로드
     */
    public FileVo singleFileUpload(@RequestParam("files") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        FileVo fileVo = new FileVo();

        String attachFileId = CommonUtil.createUUId();
        int seq = 1;

        String baseDir = "d:/mesUploads/"; // 상대 경로 (project root/uploads)

        //path 경로 년 월 일 폴더
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());

        Path realPath = Paths.get(baseDir, year, month);
        File dir = new File(realPath.toString());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!file.isEmpty()) {
            try{
                String realFileName = year+month+"_"+file.getOriginalFilename();
                Path filePath = realPath.resolve(realFileName);
                String fileName = file.getOriginalFilename();
                long fileSize = file.getSize();

                fileVo.setAttachFileId(attachFileId);
                fileVo.setSeq(seq);
                fileVo.setFileName(fileName);
                fileVo.setRealFileName(realFileName);
                fileVo.setFilePath(filePath.toString());
                fileVo.setFileSize(fileSize);

                file.transferTo(new File(filePath.toString()));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileVo;
    }

    /**
     * 멀티 첨부파일 업로드
     * @param files
     * @return
     */
    public String multiFileUpload(@RequestParam("files") List<MultipartFile> files) {
        Map<String, Object> result = new HashMap<>();
        List<String> uploadedFiles = new ArrayList<>();
        FileVo fileVo = new FileVo();

        //파일 id
        String attachFileId = CommonUtil.createUUId();
        int seq = 0;

        String baseDir = "d:/uploads/"; // 상대 경로 (project root/uploads)

        //path 경로 년 월 일 폴더
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        //String day = String.format("%02d", today.getDayOfMonth());

        Path realPath = Paths.get(baseDir, year, month);
        File dir = new File(realPath.toString());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    //String filePath = realPath+"_" + file.getOriginalFilename();
                    String realFileName = year+month+"_"+file.getOriginalFilename();
                    Path filePath = realPath.resolve(realFileName);
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
                    fileVo.setFilePath(filePath.toString());
                    fileVo.setFileSize(fileSize);

                    file.transferTo(new File(filePath.toString()));

                    uploadedFiles.add(file.getOriginalFilename());

                } catch (IOException e) {
                    e.printStackTrace();
                    result.put("error", "파일 업로드 실패: " + file.getOriginalFilename());
                }
            }
        }
        //result.put("uploaded", uploadedFiles);
        return attachFileId;
    }

    /**
     * 엑셀파일 읽기
     * @param file
     * @return
     * @throws Exception
     */
    public List<Map<String, String>> readExcelFile(MultipartFile file) throws Exception{

        if (file.isEmpty()) {
            throw new Exception("파일이 비어 있습니다.");
        }

        try{
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = workbook.getSheetAt(0);
            List<Map<String, String>> result = new ArrayList<>();
            Iterator<Row> rows = sheet.iterator();
            Row headerRow = rows.hasNext() ? rows.next() : null;

            if (headerRow == null) {
                throw new Exception("헤더가 없습니다.");
            }
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                Map<String, String> rowData = new LinkedHashMap<>();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i);
                    String value = getCellValueAsString(cell);
                    rowData.put(headers.get(i), value);
                }

                result.add(rowData);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    /**
     * 엑셀파일 만들기
     */
    public ByteArrayInputStream exportToExcel(List<String[]> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sheet1");

            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i);
                String[] rowData = data.get(i);

                for (int j = 0; j < rowData.length; j++) {
                    row.createCell(j).setCellValue(rowData[j]);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
