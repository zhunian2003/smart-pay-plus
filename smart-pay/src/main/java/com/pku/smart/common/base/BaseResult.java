package com.pku.smart.common.base;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhunian
 * @create 2018-01-28 17:38
 **/
public class BaseResult implements Serializable {
    /**
     * SUCCESS/FAIL此字段是通信标识，非交易标识，交易是否成功需要查看resCode来判断
     */
    private String retCode;

    /**
     * 返回信息，如非空，为错误原因 签名失败 参数格式校验错误
     */
    private String retMsg;

    /**
     * SUCCESS/FAIL
     */
    private String resCode;

    /**
     * 错误码 如 SYSTEMERROR
     */
    private String errCode;

    /**
     * 结果信息描述 如 微信支付内部错误
     */
    private String errCodeDes;

    /*
     * 结果字段 数量不定 采用json
     */
    private Map resultObject;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public Map getResultObject() {
        return resultObject;
    }

    public void setResultObject(Map resultObject) {
        this.resultObject = resultObject;
    }

}
