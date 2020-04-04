package com.pku.smart.modules.sys.service;

public interface ISysLoginService {

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param uuid 唯一标识
     * @return 结果
     */
    String login(String username, String password, String uuid);
}
