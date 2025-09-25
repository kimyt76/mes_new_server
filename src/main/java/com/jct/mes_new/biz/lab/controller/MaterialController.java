package com.jct.mes_new.biz.lab.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.lab.service.MaterialService;
import com.jct.mes_new.biz.lab.vo.HistoryVo;
import com.jct.mes_new.biz.lab.vo.IngredientVo;
import com.jct.mes_new.biz.lab.vo.MaterialRequestVo;
import com.jct.mes_new.biz.lab.vo.MaterialVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lab")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping("/getMaterialItemList")
    public List<ItemVo> getMaterialItemList(@RequestBody MaterialVo vo) {
        return materialService.getMaterialItemList(vo);
    }

    @GetMapping("/getMaterialInfo/{id}")
    public MaterialRequestVo getMaterialInfo (@PathVariable("id") String itemCd) {
        return materialService.getMaterialInfo(itemCd);
    }

    @PostMapping("/saveMaterialInfo")
    public ResponseEntity<?> saveMaterialInfo(@RequestPart("materialInfo") String materialInfoJson,
                                              @RequestPart("materialList") String materialListJson,
                                              @RequestPart("historyList") String historyListJson,
                                              @RequestPart(value = "newFiles", required = false) List<MultipartFile> newFiles,
                                              @RequestPart(value = "deleteFiles", required = false) String deleteFilesJson,
                                              @RequestPart(value = "keptFiles", required = false) String keptFilesJson
                                              ) throws Exception {

        try {
            MaterialRequestVo vo = new MaterialRequestVo();
            ObjectMapper mapper = new ObjectMapper();

            vo.setMaterialInfo(mapper.readValue(materialInfoJson, MaterialVo.class));
            vo.setMaterialList(mapper.readValue(materialListJson, new TypeReference<List<IngredientVo>>() {}));
            vo.setHistoryList(mapper.readValue(historyListJson, new TypeReference<List<HistoryVo>>() {}));
            vo.setNewFiles(newFiles != null ? newFiles : new ArrayList<>());

            List<FileVo> deleteFiles = new ArrayList<>();
            if (deleteFilesJson != null && !deleteFilesJson.isEmpty()) {
                deleteFiles = mapper.readValue(deleteFilesJson, new TypeReference<List<FileVo>>() {});
            }
            vo.setDeleteFiles(deleteFiles);
            vo.setKeptFiles(keptFilesJson != null ?
                    mapper.readValue(keptFilesJson, new TypeReference<List<FileVo>>() {}) :
                    new ArrayList<>());

            String msg = materialService.saveMaterialInfo(vo);

            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }
}
