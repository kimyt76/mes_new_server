package com.jct.mes_new.biz.common.vo;

import lombok.Data;

@Data
public class FileVo {

    private String attachFileId;
    private int seq;
    private String fileName;
    private String realFileName;
    private String filePath;
    private long fileSize;

    private String userId;
}
