package com.pku.smart.common.handle;

import com.alibaba.fastjson.JSON;
import com.pku.smart.common.async.AnyscRecordLogininfor;
import com.pku.smart.common.constant.Constants;
import com.pku.smart.common.constant.HttpStatus;
import com.pku.smart.common.security.JwtTokenService;
import com.pku.smart.common.security.SecurityLoginUser;
import com.pku.smart.common.domain.AjaxResult;
import com.pku.smart.utils.ServletUtils;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出处理类 返回成功
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    JwtTokenService tokenService;

    @Autowired
    AnyscRecordLogininfor recordLogininfor;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityLoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            recordLogininfor.recordLogininfor(userName, Constants.LOGOUT, "退出成功");
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
