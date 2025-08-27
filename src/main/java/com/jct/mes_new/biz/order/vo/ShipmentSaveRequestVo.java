package com.jct.mes_new.biz.order.vo;

import com.jct.mes_new.biz.common.vo.FileVo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShipmentSaveRequestVo {
    private ShipmentVo shipmentInfo;
    private List<ShipmentItemListVo> itemList;
    private List<MultipartFile> newFiles;
    private List<FileVo> deleteFiles;
    private List<FileVo> keptFiles; // keptFilesJson → List<AttachFileVo> 파싱
}
