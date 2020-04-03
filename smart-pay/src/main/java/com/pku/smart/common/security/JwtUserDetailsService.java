package com.pku.smart.common.security;

import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.common.enums.UserStatus;
import com.pku.smart.common.exception.BaseException;
import com.pku.smart.modules.sys.entity.SysUser;
import com.pku.smart.modules.sys.service.ISysUserService;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private static final MyLog log = MyLog.getLog(JwtUserDetailsService.class);

    @Autowired
    ISysUserService userService;

    @Autowired
    SecurityPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(userName);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", userName);
            throw new UsernameNotFoundException("登录用户：" + userName + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", userName);
            throw new BaseException("对不起，您的账号：" + userName + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", userName);
            throw new BaseException("对不起，您的账号：" + userName + " 已停用");
        }
        return new SecurityLoginUser(user, permissionService.getMenuPermission(user));
    }
}
