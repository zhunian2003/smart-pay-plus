package com.pku.smart.modules.sys.controller;

import com.pku.smart.common.base.BaseController;
import com.pku.smart.common.domain.AjaxResult;
import com.pku.smart.modules.sys.vopackage.SysServerVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitor/server")
public class SysServerController extends BaseController {
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception {
        SysServerVo server = new SysServerVo();
        server.copyTo();
        return AjaxResult.success(server);
    }
}
