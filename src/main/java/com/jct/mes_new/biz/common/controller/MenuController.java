package com.jct.mes_new.biz.common.controller;

import com.jct.mes_new.biz.common.service.CommonService;
import com.jct.mes_new.biz.common.service.MenuService;
import com.jct.mes_new.biz.common.vo.MenuVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/menu")
public class MenuController {

    private MenuService menuService;

    @GetMapping("/getMenus")
    public List<MenuVo> getMenus(HttpSession session){
        String userId = (String) session.getAttribute("userInfo");
        return menuService.getMenus(userId);
    }
}
