package com.pku.smart.modules.pay.vopackage;

import java.io.Serializable;

public class VoTradeResult implements Serializable {

    /**
     * 是否成功
     */
    private Boolean resultSuccess;

    /**
     * 失败错误码
     */
    private String resultCode;

    /**
     * 失败错误说明
     */
    private String resultMsg;

    /**
     * 返回对象
     */
    private Object resultObject;

    public Boolean getResultSuccess() {
        return resultSuccess;
    }

    public void setResultSuccess(Boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}
