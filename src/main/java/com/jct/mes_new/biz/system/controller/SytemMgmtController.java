package com.jct.mes_new.biz.system.controller;

import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.service.SystemMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/systemMgmt")
public class SytemMgmtController {

    final SystemMgmtService systemMgmtService;
    final UserService userService;
    final PasswordEncoder passwordEncoder;

    @PostMapping("/getUserList")
    public List<UserVo> getUserList(@RequestBody UserVo userVo) {
        return userService.getUserList(userVo);
    }

    @PatchMapping("/updateUserInfo")
    public String updateUserInfo(@RequestBody UserVo userVo) throws Exception {
        String msg = "";
        if ( userVo.getPassword() != null ) {
            userVo.setPassword(passwordEncoder.encode(userVo.getPassword())  );
        }

        try{
            if ( userService.updateUserInfo(userVo)  ){
                msg = "사용자 정보가 저장되었습니다.";
            }
        } catch (Exception e) {
            throw new Exception("저장중 오류가 발생했습니다.");
        }
        return msg;
    }

    @GetMapping("/getUserInfo/{id}")
    public UserVo getUserInfo(@PathVariable String id){
        return userService.getUserInfo(id);
    }

    @GetMapping("/userCheck/{id}")
    public String userCheck(@PathVariable String id){
        String msg = "사용가능한 ID입니다.";
        if( userService.userCheck(id) > 0) {
            msg = "중복된 ID입니다.";
        }

        return msg;
    }

    @GetMapping("/passwordInit/{id}")
    public String passwordInit(@PathVariable String id){
        String msg = "초기화 되었습니다.";
        String password = passwordEncoder.encode("1234");

        if ( !userService.passwordInit(id, password) ) {
            msg ="초기화중 실패했습니다.";
        }
        return msg;
    }

}
