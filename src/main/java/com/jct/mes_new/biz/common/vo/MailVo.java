package com.jct.mes_new.biz.common.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MailVo {
    private String id;
    private String from;
    private String to;
    private String cc;      // 선택
    private String bcc;     // 선택
    private String subject;
    private String content;
    private boolean html;
    private String memo;

    private String toName;

    private List<MultipartFile> files; // 선택 (첨부파일 여러 개)
}
