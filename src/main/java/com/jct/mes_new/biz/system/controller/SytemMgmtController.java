package com.jct.mes_new.biz.system.controller;

import com.jct.mes_new.auth.service.UserService;
import com.jct.mes_new.auth.vo.UserVo;
import com.jct.mes_new.biz.system.vo.MenuRequestVo;
import com.jct.mes_new.biz.system.vo.MenuVo;
import com.jct.mes_new.biz.system.service.SystemMgmtService;
import com.jct.mes_new.biz.system.vo.StorageVo;
import com.jct.mes_new.biz.system.vo.SystemUserVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/systemMgmt")
public class SytemMgmtController {

    final SystemMgmtService systemMgmtService;
    final UserService userService;
    final PasswordEncoder passwordEncoder;
    private final MessageUtil messageUtil;

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
            msg = userService.updateUserInfo(userVo);
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

    @GetMapping("/getMenuList")
    public List<MenuVo> getMenuList() {
        return systemMgmtService.getMenuList();
    }

    @PostMapping("/getMenuMgmtList")
    public List<MenuVo> getMenuMgmtList(@RequestBody MenuVo vo) {
        return systemMgmtService.getMenuMgmtList(vo);
    }
    @GetMapping("/getMenuDetail/{id}")
    public MenuVo getMenuDetail(@PathVariable("id") int menuId) {
        return systemMgmtService.getMenuDetail(menuId);
    }

    @PostMapping("/saveMenu")
    public ResponseEntity<ApiResponse<Void>> saveMenu (@RequestBody MenuVo vo) {
        String result = systemMgmtService.saveMenu(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }
    @PostMapping("/updateMenuUseYn")
    public ResponseEntity<ApiResponse<Void>> updateMenuUseYn (@RequestBody MenuVo vo) {
        String result = systemMgmtService.updateMenuUseYn(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/getAuthMenuInfo")
    public MenuRequestVo getAuthMenuInfo(@RequestBody UserVo vo) {
        return systemMgmtService.getAuthMenuInfo(vo);
    }

    @PostMapping("/getUserMenuAuthList")
    public List<MenuVo> getUserMenuAuthList(@RequestBody MenuVo vo) {
        return systemMgmtService.getUserMenuAuthList(vo);
    }

    @PostMapping("/saveMenuAuth")
    public ResponseEntity<ApiResponse<Void>> saveMenuAuth (@RequestBody MenuVo vo) {
        String result = systemMgmtService.saveMenuAuth(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/copyMenuAuthInfo")
    public ResponseEntity<ApiResponse<Void>> copyMenuAuthInfo (@RequestBody MenuVo vo) {
        String result = systemMgmtService.copyMenuAuthInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


}
