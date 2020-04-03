package com.pku.smart.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.annotation.Excel;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "sys_oper_log")
public class SysOperLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @TableId(value = "oper_id", type = IdType.AUTO)
    @Excel(name = "操作序号", cellType = Excel.ColumnType.NUMERIC)
    private Long operId;

    /**
     * 操作模块
     */
    @TableField(value = "title")
    @Excel(name = "操作模块")
    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @TableField(value = "business_type")
    @Excel(name = "业务类型", readConverterExp = "0=其它,1=新增,2=修改,3=删除,4=授权,5=导出,6=导入,7=强退,8=生成代码,9=清空数据")
    private Integer businessType;

    /**
     * 业务类型数组
     */
    @TableField(exist = false)
    private Integer[] businessTypes;

    /**
     * 请求方法
     */

    @TableField(value = "method")
    @Excel(name = "请求方法")
    private String method;

    /**
     * 请求方式
     */
    @TableField(value = "request_method")
    @Excel(name = "请求方式")
    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @TableField(value = "operator_type")
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /**
     * 操作人员
     */
    @TableField(value = "oper_name")
    @Excel(name = "操作人员")
    private String operName;

    /**
     * 部门名称
     */
    @TableField(value = "dept_name")
    @Excel(name = "部门名称")
    private String deptName;

    /**
     * 请求url
     */
    @TableField(value = "oper_url")
    @Excel(name = "请求地址")
    private String operUrl;

    /**
     * 操作地址
     */
    @TableField(value = "oper_ip")
    @Excel(name = "操作地址")
    private String operIp;

    /**
     * 操作地点
     */
    @TableField(value = "oper_location")
    @Excel(name = "操作地点")
    private String operLocation;

    /**
     * 请求参数
     */
    @TableField(value = "oper_param")
    @Excel(name = "请求参数")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField(value = "json_result")
    @Excel(name = "返回参数")
    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    @TableField(value = "status")
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private Integer status;

    /**
     * 错误消息
     */
    @TableField(value = "error_msg")
    @Excel(name = "错误消息")
    private String errorMsg;

    /**
     * 操作时间
     */
    @TableField(value = "oper_time")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer[] getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(Integer[] businessTypes) {
        this.businessTypes = businessTypes;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOperUrl() {
        return operUrl;
    }

    public void setOperUrl(String operUrl) {
        this.operUrl = operUrl;
    }

    public String getOperIp() {
        return operIp;
    }

    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }

    public String getOperLocation() {
        return operLocation;
    }

    public void setOperLocation(String operLocation) {
        this.operLocation = operLocation;
    }

    public String getOperParam() {
        return operParam;
    }

    public void setOperParam(String operParam) {
        this.operParam = operParam;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }
}
