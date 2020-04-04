package com.pku.smart.modules.sys.service;

import com.pku.smart.common.security.SecurityLoginUser;
import com.pku.smart.vopackage.UserOnlineVo;

public interface ISysUserOnlineService {
    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr 登录地址
     * @param user   用户信息
     * @return 在线用户信息
     */
    UserOnlineVo selectOnlineByIpaddr(String ipaddr, SecurityLoginUser user);

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @param user     用户信息
     * @return 在线用户信息
     */
    UserOnlineVo selectOnlineByUserName(String userName, SecurityLoginUser user);

    /**
     * 通过登录地址/用户名称查询信息
     *
     * @param ipaddr   登录地址
     * @param userName 用户名称
     * @param user     用户信息
     * @return 在线用户信息
     */
    UserOnlineVo selectOnlineByInfo(String ipaddr, String userName, SecurityLoginUser user);

    /**
     * 设置在线用户信息
     *
     * @param user 用户信息
     * @return 在线用户
     */
    UserOnlineVo loginUserToUserOnline(SecurityLoginUser user);
}
