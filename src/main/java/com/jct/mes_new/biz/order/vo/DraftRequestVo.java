package com.jct.mes_new.biz.order.vo;

import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DraftRequestVo {

    private DraftVo draftInfo;
    private List<DraftApprovalVo> draftApprovalList;

    private List<FileVo> attachFileInfo;
    private List<MultipartFile> newFiles;
    private List<FileVo> deleteFileList;
}
