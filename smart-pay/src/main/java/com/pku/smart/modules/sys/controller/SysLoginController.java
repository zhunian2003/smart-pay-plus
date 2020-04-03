package com.pku.smart.modules.sys.controller;

import com.pku.smart.common.security.JwtTokenService;
import com.pku.smart.common.security.SecurityLoginUser;
import com.pku.smart.common.security.SecurityPermissionService;
import com.pku.smart.common.constant.Constants;
import com.pku.smart.common.domain.AjaxResult;
import com.pku.smart.modules.sys.entity.SysMenu;
import com.pku.smart.modules.sys.entity.SysUser;
import com.pku.smart.modules.sys.service.ISysLoginService;
import com.pku.smart.modules.sys.service.ISysMenuService;
import com.pku.smart.utils.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class SysLoginController {

    @Autowired
    ISysLoginService loginService;

    @Autowired
    JwtTokenService tokenService;

    @Autowired
    ISysMenuService menuService;

    @Autowired
    SecurityPermissionService permissionService;

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @param uuid     唯一标识
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(String username, String password, String uuid) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(username, password, uuid);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        SecurityLoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        SecurityLoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
