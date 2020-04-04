package com.pku.smart.common.async;

import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.sys.entity.SysOperLog;
import com.pku.smart.modules.sys.mapper.SysOperLogMapper;
import com.pku.smart.utils.AddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 异步记录操作记录
 */
@Service
public class AsyncRecordOperLog {
    private final MyLog _log = MyLog.getLog(AsyncRecordOperLog.class);

    @Autowired
    SysOperLogMapper operLogMapper;

    public Future<Object> recordOper(final SysOperLog operLog){
        _log.info("开始记录登陆登出日志");
        try {
            operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
            operLogMapper.insertOperlog(operLog);
        } catch (Exception e) {
            e.printStackTrace();
            _log.error(e.getMessage());
            return new AsyncResult<>(e.getMessage());
        }
        return new AsyncResult<>(operLog);
    }
}
