package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.common.mapper.FileHandlerMapper;
import com.jct.mes_new.biz.common.vo.FileVo;
import com.jct.mes_new.biz.order.mapper.ContractMapper;
import com.jct.mes_new.biz.order.service.ContractService;
import com.jct.mes_new.biz.order.vo.ContractSaveRequestVo;
import com.jct.mes_new.biz.order.vo.ContractVo;
import com.jct.mes_new.biz.order.vo.ContractItemVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.FileUpload;
import com.jct.mes_new.config.common.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final FileHandlerMapper fileHandlerMapper;

    public List<ContractVo> getContractList(ContractVo contractVo){
        return contractMapper.getContractList(contractVo);
    }

    public Map<String, Object> getContractInfo(String contractId){
        Map<String, Object> map = new HashMap<>();

        ContractVo contractInfo = contractMapper.getContractInfo(contractId);
        List<ContractItemVo> itemList = contractMapper.getItemList(contractId);

        map.put("contractInfo", contractInfo);
        map.put("itemList", itemList);
        map.put("attachFileInfo",  fileHandlerMapper.getAttachFileList(contractInfo.getAttachFileId()) );

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveContractInfo(ContractVo contractInfo, List<ContractItemVo> itemList, List<MultipartFile> attachFileList) throws Exception {
        Snowflake snowflake = new Snowflake(1, 1);
        String contractId = CommonUtil.generateUUID();

        try{
            /* 주문서id 채번*/
            contractInfo.setContractId(contractId);

            /* 첨부파일 정보 저장 및 ID 채번*/
            if ( attachFileList != null ){
                List<FileVo> fileVoList = FileUpload.multiFileUpload(attachFileList);
                contractInfo.setAttachFileId(fileVoList.get(0).getAttachFileId());

                for (FileVo item : fileVoList) {
                    item.setUserId(contractInfo.getUserId());
                    if (!fileHandlerMapper.saveFile(item)) {
                        throw new Exception("첨부 파일 저장 실패");
                    }
                }
            }

            if ( contractMapper.saveContractInfo(contractInfo) <= 0 ) {
                throw new Exception("주문서 저장에 실패했습니다.");
            }else{
                contractMapper.deleteContractItemList(contractInfo.getContractId());

                for (ContractItemVo item : itemList) {
                    item.setContractItemId(String.valueOf(snowflake.nextId()));
                    item.setContractId(contractInfo.getContractId());
                    item.setUserId(contractInfo.getUserId());

                    if ( contractMapper.saveItemList(item)  <= 0 ){
                        throw new Exception("품목리스트 저장 실패");
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return contractId;
    }


    public String updateContractInfo(ContractSaveRequestVo vo) throws Exception {
        Snowflake snowflake = new Snowflake(1, 1);
        String msg = "저장되었습니다.";

        try {
            if ( contractMapper.saveContractInfo(vo.getContractInfo()) <= 0 ) {
                throw new Exception("주문서 저장에 실패했습니다.");
            }

            String contractId = vo.getContractInfo().getContractId();
            String userId = vo.getContractInfo().getUserId();
            contractMapper.deleteContractItemList(contractId);

            for (ContractItemVo item : vo.getItemList()) {
                item.setContractItemId(String.valueOf(snowflake.nextId()));
                item.setContractId(contractId);
                item.setUserId(userId);

                if ( contractMapper.saveItemList(item)  <= 0 ){
                    throw new Exception("품목리스트 저장 실패");
                }
            }

            if ( vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty() ) {
                for (FileVo item : vo.getDeleteFiles()){
                    fileHandlerMapper.deleteFile(item.getAttachFileId(), item.getSeq() );
                }
            }

            List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());
            int nextSeq = fileHandlerMapper.nextSeq( vo.getContractInfo().getAttachFileId());

            if ( vo.getContractInfo().getAttachFileId()  == null ){
                vo.getContractInfo().setAttachFileId(fileVoList.get(0).getAttachFileId());
            }

            for (FileVo item : fileVoList) {
                item.setAttachFileId(vo.getContractInfo().getAttachFileId());
                item.setSeq(nextSeq);
                item.setUserId(userId);
                if (!fileHandlerMapper.saveFile(item)) {
                    throw new Exception("첨부 파일 저장 실패");
                }
                nextSeq++;
            }
        }catch (Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return msg;
    }
}
