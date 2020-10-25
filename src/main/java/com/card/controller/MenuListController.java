package com.card.controller;

import com.card.entity.vo.ExportFileVO;
import com.card.entity.vo.MenuListVO;
import com.card.entity.vo.ResultVO;
import com.card.service.MenuListService;
import com.card.util.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/menuList")
public class MenuListController {
    @Autowired
    private MenuListService menuListService;

    @PostMapping("/token/selectPage")
    public ResultVO<Object> selectPage(@RequestBody MenuListVO MenuListVO) {
        return ResultVOUtil.success(menuListService.selectPage(MenuListVO));
    }
}
