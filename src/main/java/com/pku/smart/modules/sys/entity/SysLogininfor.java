package com.pku.smart.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.annotation.Excel;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 */
@Data
@TableName(value = "sys_logininfor")
public class SysLogininfor extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "info_id",type = IdType.AUTO)
    @Excel(name = "序号", cellType = Excel.ColumnType.NUMERIC)
    private Long infoId;

    /**
     * 用户账号
     */
    @TableField(value = "user_name")
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 登录状态 0成功 1失败
     */
    @TableField(value = "status")
    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 登录IP地址
     */
    @TableField(value = "ipaddr")
    @Excel(name = "登录地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @TableField(value = "login_location")
    @Excel(name = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @TableField(value = "browser")
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @TableField(value = "os")
    @Excel(name = "操作系统")
    private String os;

    /**
     * 提示消息
     */
    @TableField(value = "msg")
    @Excel(name = "提示消息")
    private String msg;

    /**
     * 访问时间
     */
    @TableField(value = "login_time")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    public Long getInfoId() {
        return infoId;
    }

    public void setInfoId(Long infoId) {
        this.infoId = infoId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}
