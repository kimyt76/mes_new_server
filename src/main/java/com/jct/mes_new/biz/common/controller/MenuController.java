package com.jct.mes_new.biz.common.controller;

import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.service.MenuService;
import com.jct.mes_new.biz.common.vo.MenuVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/menu")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/getMenuList/{id}")
    public ResponseEntity<List<MenuVo>> getMenuList(@PathVariable("id") String userId)  {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MenuVo> menuList = menuService.getMenuListByUser(userId);
        return ResponseEntity.ok(menuList);
    }
}
