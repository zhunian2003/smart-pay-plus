package com.pku.smart.modules.pay.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@ConfigurationProperties(prefix="config.ali")
public class AliPayConfig {

    // 商户appid
    private String app_id;

    // 私钥 pkcs8格式的
    private String rsa_private_key;

    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    private String return_url;

    // 请求网关地址
    private String url = "https://openapi.alipay.com/gateway.do";

    //密钥类型
    private String sign_type;

    // 支付宝公钥
    private String alipay_public_key;

    // 是否沙箱环境,1:沙箱,0:正式环境
    private Short is_sandbox = 0;

    //对账单下载目录
    private String bill_path;

    //退款标志 1、运行部分退 0、不允许部分退
    private String pay_back_flag;

    // 编码
    public static String CHARSET = "UTF-8";

    // 返回格式
    public static String FORMAT = "json";

    // RSA2
    public static String SIGNTYPE = "RSA2";

    public AliPayConfig init(String configParam) {
        Assert.notNull(configParam, "init alipay config error");
        JSONObject paramObj = JSON.parseObject(configParam);
        this.setApp_id(paramObj.getString("appid"));
        this.setRsa_private_key(paramObj.getString("private_key"));
        this.setAlipay_public_key(paramObj.getString("alipay_public_key"));
        this.setIsSandbox(paramObj.getShortValue("is_sandbox"));
        this.setSign_type(paramObj.getString("sign_type"));
        if(this.getIsSandbox() == 1) {
            this.setUrl("https://openapi.alipaydev.com/gateway.do");
        } else {
            this.setUrl("https://openapi.alipay.com/gateway.do");
        }
        if (StringUtils.isBlank(this.sign_type)){
            this.sign_type = SIGNTYPE;
        }
        return this;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getRsa_private_key() {
        return rsa_private_key;
    }

    public void setRsa_private_key(String rsa_private_key) {
        this.rsa_private_key = rsa_private_key;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }

    public Short getIsSandbox() {
        return is_sandbox;
    }

    public void setIsSandbox(Short is_sandbox) {
        this.is_sandbox = is_sandbox;
    }

    public String getBill_path() {
        return bill_path;
    }

    public void setBill_path(String bill_path) {
        this.bill_path = bill_path;
    }

    public String getPay_back_flag() {
        return pay_back_flag;
    }

    public void setPay_back_flag(String pay_back_flag) {
        this.pay_back_flag = pay_back_flag;
    }
}
