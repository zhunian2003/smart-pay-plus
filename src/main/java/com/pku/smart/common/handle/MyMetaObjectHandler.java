package com.pku.smart.common.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.pku.smart.common.mylog.MyLog;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 通过fill注解自动填充一些字段
 * 注意：填充的是class字段名而不是数据库字段名
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final MyLog _log = MyLog.getLog(MyMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        _log.info("start insert fill ....");
//        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
//        SysUser user = (SysUser)request.getSession().getAttribute("user");
//        if(user!=null) {
//            this.fillStrategy(metaObject, "createBy", user.getUserName());
//        }
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        _log.info("start update fill ....");
//        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
//        SysUser user = (SysUser)request.getSession().getAttribute("user");
//        if(user!=null) {
//            this.fillStrategy(metaObject, "updateBy", user.getUserName());
//        }
        //this.strictUpdateFill(metaObject, "updateTime", Date.class, DateUtils.dateTime());
    }
}

