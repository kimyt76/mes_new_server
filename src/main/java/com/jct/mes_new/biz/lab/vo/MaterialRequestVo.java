package com.jct.mes_new.biz.lab.vo;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class MaterialRequestVo {

    private MaterialVo materialInfo;
    private ItemVo itemInfo;
    private List<IngredientVo> materialList;
    private List<HistoryVo> historyList;

    private List<MultipartFile> newFiles;
    private List<FileVo> deleteFiles;
    private List<FileVo> keptFiles; // keptFilesJson → List<AttachFileVo> 파싱
    private List<FileVo> fileList;
}
