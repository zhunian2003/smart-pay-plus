package com.pku.smart.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum WxPayEnum {
    RET_CODE_10000("00000", "未枚举的错误"),
    RET_CODE_10001("NOAUTH", "商户无此接口权限"),
    RET_CODE_10002("NOTENOUGH", "余额不足"),
    RET_CODE_10003("ORDERPAID", "商户订单已支付"),
    RET_CODE_10004("ORDERCLOSED", "订单已关闭"),
    RET_CODE_10005("SYSTEMERROR", "系统错误"),
    RET_CODE_10006("APPID_NOT_EXIST", "APPID不存在"),
    RET_CODE_10007("MCHID_NOT_EXIST", "MCHID不存在"),
    RET_CODE_10008("APPID_MCHID_NOT_MATC", "appid和mch_id不匹配"),
    RET_CODE_10009("LACK_PARAMS", "缺少参数"),
    RET_CODE_10010("OUT_TRADE_NO_USED", "商户订单号重复"),
    RET_CODE_10011("SIGNERROR", "签名错误"),
    RET_CODE_10012("XML_FORMAT_ERROR", "XML格式错误"),
    RET_CODE_10013("REQUIRE_POST_METHOD", "请使用post方法"),
    RET_CODE_10014("POST_DATA_EMPTY", "post数据为空"),
    RET_CODE_10015("NOT_UTF8", "编码格式错误"),
    RET_CODE_10016("BIZERR_NEED_RETRY", "退款业务流程错误，需要商户触发重试来解决"),
    RET_CODE_10017("TRADE_OVERDUE", "订单已经超过退款期限"),
    RET_CODE_10018("USER_ACCOUNT_ABNORMAL", "退款请求失败,用户帐号注销"),
    RET_CODE_10019("INVALID_REQ_TOO_MUCH", "无效请求过多"),
    RET_CODE_10020("INVALID_TRANSACTIONID", "无效transaction_id"),
    RET_CODE_10021("FREQUENCY_LIMITED", "频率限制"),
    RET_CODE_10022("AUTHCODEEXPIRE", "二维码已过期，请用户在微信上刷新后再试"),
    RET_CODE_10023("NOTSUPORTCARD", "不支持卡类型"),
    RET_CODE_10024("USERPAYING", "用户支付中，需要输入密码"),
    RET_CODE_10025("ORDERNOTEXIST", "查询系统中不存在此交易订单号");

    private String code;
    private String message;

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    WxPayEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WxPayEnum getObject(String code) {
        for (WxPayEnum wxPayEnum : WxPayEnum.values()) {
            if (StringUtils.equals(code, wxPayEnum.getCode())) {
                return wxPayEnum;
            }
        }
        return RET_CODE_10000;
    }
}
