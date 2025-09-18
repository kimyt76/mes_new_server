package com.jct.mes_new.biz.base.controller;

import com.jct.mes_new.biz.base.service.ClientService;
import com.jct.mes_new.biz.base.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/getClientList")
    public List<ClientVo> getClientList (@RequestBody ClientVo clientVo) {
        return clientService.getClientList(clientVo);
    }

    @GetMapping("/getClientInfo/{id}")
    public ClientRequestVo getClientInfo(@PathVariable("id") String clientId) {
        return clientService.getClientInfo(clientId);
    }

    @PostMapping("/saveClientInfo")
    public ResponseEntity<?> saveClientInfo(@RequestBody ClientRequestVo vo) throws Exception {

        try {
            String result = clientService.saveClientInfo(vo);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());  // 사용자에게 오류 메시지 반환
        }
    }

    @GetMapping("/getBusinessNoChecked/{id}")
    public String getBusinessNoChecked(@PathVariable("id") String businessNo) {
        return clientService.getBusinessNoChecked(businessNo);
    }



}
