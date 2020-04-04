package com.pku.smart.common.async;

import com.pku.smart.common.constant.Constants;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.sys.entity.SysLogininfor;
import com.pku.smart.utils.AddressUtils;
import com.pku.smart.utils.IpUtils;
import com.pku.smart.utils.LogUtils;
import com.pku.smart.utils.ServletUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 异步记录登陆记录
 */
@Service
public class AnyscRecordLogininfor {
    private final MyLog _log = MyLog.getLog(AnyscRecordLogininfor.class);

    public Future<Object> recordLogininfor(final String username, final String status, final String message,
                                           final Object... args) {
        _log.info("开始记录登陆登出日志");
        SysLogininfor logininfor = new SysLogininfor();
        try {
            final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
            final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            String address = AddressUtils.getRealAddressByIP(ip);
            StringBuilder s = new StringBuilder();
            s.append(LogUtils.getBlock(ip));
            s.append(address);
            s.append(LogUtils.getBlock(username));
            s.append(LogUtils.getBlock(status));
            s.append(LogUtils.getBlock(message));
            // 打印信息到日志
            _log.info(s.toString(), args);
            // 获取客户端操作系统
            String os = userAgent.getOperatingSystem().getName();
            // 获取客户端浏览器
            String browser = userAgent.getBrowser().getName();
            // 封装对象
            logininfor.setUserName(username);
            logininfor.setIpaddr(ip);
            logininfor.setLoginLocation(address);
            logininfor.setBrowser(browser);
            logininfor.setOs(os);
            logininfor.setMsg(message);
            // 日志状态
            if (Constants.LOGIN_SUCCESS.equals(status) || Constants.LOGOUT.equals(status)) {
                logininfor.setStatus(Constants.SUCCESS);
            } else if (Constants.LOGIN_FAIL.equals(status)) {
                logininfor.setStatus(Constants.FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.error(e.getMessage());
            return new AsyncResult<>(e.getMessage());
        }
        return new AsyncResult<>(logininfor);
    }
}
