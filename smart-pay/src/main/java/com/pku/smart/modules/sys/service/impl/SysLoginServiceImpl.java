package com.pku.smart.modules.sys.service.impl;

import com.pku.smart.common.async.AnyscRecordLogininfor;
import com.pku.smart.common.constant.Constants;
import com.pku.smart.common.security.JwtTokenService;
import com.pku.smart.common.security.SecurityLoginUser;
import com.pku.smart.common.exception.CustomException;
import com.pku.smart.common.exception.UserPasswordNotMatchException;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.sys.service.ISysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SysLoginServiceImpl implements ISysLoginService {

    private static final MyLog log = MyLog.getLog(SysLoginServiceImpl.class);

    @Autowired
    JwtTokenService tokenService;

    @Autowired
    AnyscRecordLogininfor recordLogininfor;

    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param uuid     唯一标识
     * @return 结果
     */
    @Override
    public String login(String username, String password, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        log.debug(verifyKey);
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                recordLogininfor.recordLogininfor(username, Constants.LOGIN_FAIL, "用户不存在/密码错误");
                throw new UserPasswordNotMatchException();
            } else {
                recordLogininfor.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage());
                throw new CustomException(e.getMessage());
            }
        }
        recordLogininfor.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        SecurityLoginUser loginUser = (SecurityLoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }
}
