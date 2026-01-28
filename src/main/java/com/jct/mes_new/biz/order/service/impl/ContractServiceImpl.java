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
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
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
    public String saveContractInfo(ContractVo contractInfo,
                                   List<ContractItemVo> itemList,
                                   List<MultipartFile> attachFileList) {

        Snowflake snowflake = new Snowflake(1, 1);
        String contractId = CommonUtil.generateUUID();

        try {
            // 1) mst key 세팅
            contractInfo.setContractId(contractId);

            // 2) 첨부파일 저장 + attachFileId 세팅
            if (attachFileList != null && !attachFileList.isEmpty()) {
                List<FileVo> fileVoList = FileUpload.multiFileUpload(attachFileList);
                // 업로드 결과가 비정상인 경우 방어
                if (fileVoList == null || fileVoList.isEmpty() || fileVoList.get(0).getAttachFileId() == null) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
                contractInfo.setAttachFileId(fileVoList.get(0).getAttachFileId());

                for (FileVo f : fileVoList) {
                    f.setUserId(contractInfo.getUserId());
                    if (!fileHandlerMapper.saveFile(f)) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
            }
            // 4) mst INSERT
            int mstIns = contractMapper.insertContractInfo(contractInfo);
            if (mstIns <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            // 5) item INSERT(신규)
            if (itemList != null) {
                for (ContractItemVo item : itemList) {
                    item.setContractItemId(String.valueOf(snowflake.nextId()));
                    item.setContractId(contractId);
                    item.setUserId(contractInfo.getUserId());

                    int itemIns = contractMapper.insertContractItem(item);
                    if (itemIns <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
            }

            return contractId;
        } catch (BusinessException e) {
            // 이미 우리가 의도한 예외 -> 그대로 던져서 롤백
            throw e;
        } catch (Exception e) {
            // 예기치 못한 예외도 롤백되게 BusinessException으로 통일
            log.error("saveContractInfo failed. contractId={}, userId={}", contractId, contractInfo.getUserId(), e);
            throw e; // 또는 BusinessException으로 감싸되 원인 포함
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateContractInfo(ContractSaveRequestVo vo) {
        Snowflake snowflake = new Snowflake(1, 1);
        String msg = "저장되었습니다.";

        try {
            ContractVo contractInfo = vo.getContractInfo();
            String contractId = contractInfo.getContractId();
            String userId = contractInfo.getUserId();

            // 2) mst UPDATE
            int mstUpd = contractMapper.updateContractInfo(contractInfo);
            if (mstUpd <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED); // FAIL_UPDATED 있으면 그걸로
            }
            // 3) item 전체 재등록(삭제 후 insert)
            contractMapper.deleteContractItemList(contractId);

            if (vo.getItemList() != null) {
                for (ContractItemVo item : vo.getItemList()) {
                    item.setContractItemId(String.valueOf(snowflake.nextId()));
                    item.setContractId(contractId);
                    item.setUserId(userId);

                    int itemIns = contractMapper.insertContractItem(item);
                    if (itemIns <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
            }
            // 4) 삭제 파일 처리
            if (vo.getDeleteFiles() != null && !vo.getDeleteFiles().isEmpty()) {
                for (FileVo f : vo.getDeleteFiles()) {
                    fileHandlerMapper.deleteFile(f.getAttachFileId(), f.getSeq());
                }
            }
            // 5) 신규 파일 업로드/저장
            if (vo.getNewFiles() != null && !vo.getNewFiles().isEmpty()) {
                List<FileVo> fileVoList = FileUpload.multiFileUpload(vo.getNewFiles());

                if (fileVoList != null && !fileVoList.isEmpty()) {
                    // attachFileId 없으면 새로 세팅
                    if (contractInfo.getAttachFileId() == null) {
                        contractInfo.setAttachFileId(fileVoList.get(0).getAttachFileId());
                    }

                    int nextSeq = fileHandlerMapper.nextSeq(contractInfo.getAttachFileId());

                    for (FileVo f : fileVoList) {
                        f.setAttachFileId(contractInfo.getAttachFileId());
                        f.setSeq(nextSeq++);
                        f.setUserId(userId);

                        if (!fileHandlerMapper.saveFile(f)) {
                            throw new BusinessException(ErrorCode.FAIL_CREATED);
                        }
                    }
                    // attachFileId가 새로 생긴 케이스면 mst에 반영 필요할 수 있음(선택)
                    // (현재 updateContractInfo SQL에 attach_file_id가 업데이트에 없으면 아래 추가 필요)
                    contractMapper.updateAttachFileId(contractId, contractInfo.getAttachFileId());
                }
            }
            return msg;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
    }


}
