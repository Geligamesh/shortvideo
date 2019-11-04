package com.gxb.controller;

import com.gxb.service.BgmService;
import com.gxb.utils.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bgm")
@Api(value = "背景音乐业务的接口",tags = {"背景音乐业务的controller"})
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @PostMapping("list")
    @ApiOperation(value = "获取背景音乐列表",notes = "获取背景音乐列表的接口")
    private JsonResult list() {
        return JsonResult.ok(bgmService.queryBgmList());
    }
}
