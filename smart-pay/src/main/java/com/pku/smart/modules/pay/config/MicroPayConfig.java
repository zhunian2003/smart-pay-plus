package com.pku.smart.modules.pay.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.wxpay.config.WxPayConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties(prefix="config.wx")
public class MicroPayConfig {

    private String certRootPath;

    private String notifyUrl;

    private String sign_type;

    private Short is_sandbox = 0;// 是否沙箱环境,1:沙箱,0:正式环境

    private String pay_back_flag;//退款标志 1、运行部分退 0、不允许部分退

    public String getCertRootPath() {
        return certRootPath;
    }

    public void setCertRootPath(String certRootPath) {
        this.certRootPath = certRootPath;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public Short getIs_sandbox() {
        return is_sandbox;
    }

    public void setIs_sandbox(Short is_sandbox) {
        this.is_sandbox = is_sandbox;
    }

    public String getPay_back_flag() {
        return pay_back_flag;
    }

    public void setPay_back_flag(String pay_back_flag) {
        this.pay_back_flag = pay_back_flag;
    }

    /**
     * 获取微信支付配置
     * @param configParam
     * @param tradeType
     * @param certRootPath
     * @param notifyUrl
     * @return
     */
    public static WxPayConfig getWxPayConfig(String configParam, String tradeType, String certRootPath, String notifyUrl) {
        WxPayConfig wxPayConfig = new WxPayConfig();
        JSONObject paramObj = JSON.parseObject(configParam);
        wxPayConfig.setMchId(paramObj.getString("mchId"));
        wxPayConfig.setAppId(paramObj.getString("appId"));
        wxPayConfig.setKeyPath(certRootPath + File.separator + paramObj.getString("certLocalPath"));
        wxPayConfig.setMchKey(paramObj.getString("key"));
        wxPayConfig.setNotifyUrl(notifyUrl);
        wxPayConfig.setTradeType(tradeType);
        wxPayConfig.setUseSandboxEnv(paramObj.getShortValue("is_sandbox")==1);
        return wxPayConfig;
    }

    /**
     * 获取微信支付配置
     * @param configParam
     * @return
     */
    @Deprecated
    public static WxPayConfig getWxPayConfig(String configParam) {
        WxPayConfig wxPayConfig = new WxPayConfig();
        JSONObject paramObj = JSON.parseObject(configParam);
        wxPayConfig.setMchId(paramObj.getString("mchId"));
        wxPayConfig.setAppId(paramObj.getString("appId"));
        wxPayConfig.setMchKey(paramObj.getString("key"));
        wxPayConfig.setUseSandboxEnv(paramObj.getShortValue("is_sandbox")==1);
        return wxPayConfig;
    }
}
