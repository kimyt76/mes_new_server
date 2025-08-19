package com.jct.mes_new.config.common;

import com.jct.mes_new.biz.common.service.FileHandlerService;
import com.jct.mes_new.biz.common.service.impl.FileHandlerServiceImpl;
import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@UtilityClass
@Slf4j
public class FileUpload {

    private final Path basePath = Paths.get("D:/mesUploads");

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

        Path realPath = Paths.get(baseDir , year, month);
        File dir = new File(realPath.toString());

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (!file.isEmpty()) {
            try{
                String fileName = file.getOriginalFilename();
                String realFileName = getNewFileName(realPath, fileName);
                Path filePath = realPath.resolve(realFileName);
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
     * @return
     * */
    public List<FileVo> multiFileUpload(@RequestParam("files") List<MultipartFile> files) {
        Map<String, Object> result = new HashMap<>();
        List<FileVo> uploadedFiles = new ArrayList<>();

        String attachFileId = CommonUtil.createUUId();
        int seq = 0;

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

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = file.getOriginalFilename();
                    String realFileName = getNewFileName(realPath, fileName);
                    Path filePath = realPath.resolve(realFileName);
                    seq++;
                    long fileSize = file.getSize();

                    FileVo fileVo = new FileVo();

                    fileVo.setAttachFileId(attachFileId);
                    fileVo.setSeq(seq);
                    fileVo.setFileName(fileName);
                    fileVo.setRealFileName(realFileName);
                    fileVo.setFilePath(filePath.toString());
                    fileVo.setFileSize(fileSize);
                    file.transferTo(new File(filePath.toString()));

                    uploadedFiles.add(fileVo);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("첨부파일 저장 오류 : " + e.getMessage());
                }
            }
        }
        return uploadedFiles;
    }


    public static String getNewFileName(Path dir, String originalFileName) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);

        String newFileName = originalFileName;
        int count = 1;

        Path filePath = dir.resolve(newFileName);

        while (Files.exists(filePath)) {
            newFileName = String.format("%s(%d).%s", baseName, count, extension);
            filePath = dir.resolve(newFileName);
            count++;
        }
        return newFileName;
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
