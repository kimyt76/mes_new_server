package com.jct.mes_new.biz.order.vo;

import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ContractSaveRequestVo {
    private ContractVo contractInfo;
    private List<ContractItemVo> itemList;
    private List<MultipartFile> newFiles;
    private List<FileVo> deleteFiles;
    private List<FileVo> keptFiles; // keptFilesJson → List<AttachFileVo> 파싱
}
