package com.pku.smart.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum AliPayEnum {
    ACQ_CODE_100001("ACQ.SYSTEM_ERROR", "系统错误"),
    ACQ_CODE_100002("ACQ.INVALID_PARAMETER", "参数无效"),
    ACQ_CODE_100003("ACQ.ACCESS_FORBIDDEN", "无权限使用接口"),
    ACQ_CODE_100004("ACQ.EXIST_FORBIDDEN_WORD", "订单信息中包含违禁词"),
    ACQ_CODE_100005("ACQ.PARTNER_ERROR", "应用APP_ID填写错误"),

    ACQ_CODE_100006("ACQ.TOTAL_FEE_EXCEED", "订单金额超过限额"),
    ACQ_CODE_100007("ACQ.PAYMENT_AUTH_CODE_INVALID", "支付授权码无效"),
    ACQ_CODE_100008("ACQ.CONTEXT_INCONSISTENT", "交易信息被篡改"),
    ACQ_CODE_100009("ACQ.TRADE_HAS_SUCCESS", "交易已被支付"),
    ACQ_CODE_100010("ACQ.TRADE_HAS_CLOSE", "交易已经关闭"),

    ACQ_CODE_100011("ACQ.BUYER_BALANCE_NOT_ENOUGH", "买家余额不足"),
    ACQ_CODE_100012("ACQ.BUYER_BANKCARD_BALANCE_NOT_ENOUGH", "用户银行卡余额不足"),
    ACQ_CODE_100013("ACQ.ERROR_BALANCE_PAYMENT_DISABLE", "余额支付功能关闭"),
    ACQ_CODE_100014("ACQ.BUYER_SELLER_EQUAL", "买卖家不能相同"),
    ACQ_CODE_100015("ACQ.TRADE_BUYER_NOT_MATCH", "交易买家不匹配"),

    ACQ_CODE_100016("ACQ.BUYER_ENABLE_STATUS_FORBID", "买家状态非法"),
    ACQ_CODE_100017("ACQ.PULL_MOBILE_CASHIER_FAIL", "唤起移动收银台失败"),
    ACQ_CODE_100018("ACQ.MOBILE_PAYMENT_SWITCH_OFF", "用户的无线支付开关关闭"),
    ACQ_CODE_100019("ACQ.PAYMENT_FAIL", "支付失败"),
    ACQ_CODE_100020("ACQ.BUYER_PAYMENT_AMOUNT_DAY_LIMIT_ERROR", "买家付款日限额超限"),

    ACQ_CODE_100021("ACQ.BEYOND_PAY_RESTRICTION", "商户收款额度超限"),
    ACQ_CODE_100022("ACQ.BEYOND_PER_RECEIPT_RESTRICTION", "商户收款金额超过月限额"),
    ACQ_CODE_100023("ACQ.BUYER_PAYMENT_AMOUNT_MONTH_LIMIT_ERROR", "买家付款月额度超限"),
    ACQ_CODE_100024("ACQ.SELLER_BEEN_BLOCKED", "商家账号被冻结"),
    ACQ_CODE_100025("ACQ.ERROR_BUYER_CERTIFY_LEVEL_LIMIT", "买家未通过人行认证"),

    ACQ_CODE_100026("ACQ.PAYMENT_REQUEST_HAS_RISK", "支付有风险"),
    ACQ_CODE_100027("ACQ.NO_PAYMENT_INSTRUMENTS_AVAILABLE", "没用可用的支付工具"),
    ACQ_CODE_100028("ACQ.USER_FACE_PAYMENT_SWITCH_OFF", "用户当面付付款开关关闭"),
    ACQ_CODE_100029("ACQ.INVALID_STORE_ID", "商户门店编号无效"),
    ACQ_CODE_100030("ACQ.SUB_MERCHANT_CREATE_FAIL", "二级商户创建失败"),

    ACQ_CODE_100031("ACQ.SUB_MERCHANT_TYPE_INVALID", "二级商户类型非法"),
    ACQ_CODE_100032("ACQ.AGREEMENT_NOT_EXIST", "用户协议不存在"),
    ACQ_CODE_100033("ACQ.AGREEMENT_INVALID", "用户协议失效"),
    ACQ_CODE_100034("ACQ.AGREEMENT_STATUS_NOT_NORMAL", "用户协议状态非NORMAL"),
    ACQ_CODE_100035("ACQ.MERCHANT_AGREEMENT_NOT_EXIST", "商户协议不存在"),

    ACQ_CODE_100036("ACQ.MERCHANT_AGREEMENT_INVALID", "商户协议已失效"),
    ACQ_CODE_100037("ACQ.MERCHANT_STATUS_NOT_NORMAL", "商户协议状态非正常状态"),
    ACQ_CODE_100038("ACQ.CARD_USER_NOT_MATCH", "脱机记录用户信息不匹配"),
    ACQ_CODE_100039("ACQ.CARD_TYPE_ERROR", "卡类型错误"),
    ACQ_CODE_100040("ACQ.CERT_EXPIRED", "凭证过期"),

    ACQ_CODE_100041("ACQ.AMOUNT_OR_CURRENCY_ERROR", "订单金额或币种信息错误"),
    ACQ_CODE_100042("ACQ.CURRENCY_NOT_SUPPORT", "订单币种不支持"),
    ACQ_CODE_100043("ACQ.SELLER_BALANCE_NOT_ENOUGH", "商户的支付宝账户中无足够的资金进行撤销"),
    ACQ_CODE_100044("ACQ.REASON_TRADE_BEEN_FREEZEN", "当前交易被冻结，不允许进行撤销"),
    ACQ_CODE_100045("ACQ.REFUND_AMT_NOT_EQUAL_TOTAL", "退款金额超限"),

    ACQ_CODE_100046("ACQ.TRADE_NOT_EXIST", "交易不存在"),
    ACQ_CODE_100047("ACQ.TRADE_HAS_FINISHED", "交易已完结"),
    ACQ_CODE_100048("ACQ.TRADE_STATUS_ERROR", "交易状态非法"),
    ACQ_CODE_100049("ACQ.DISCORDANT_REPEAT_REQUEST", "不一致的请求"),
    ACQ_CODE_100050("ACQ.REASON_TRADE_REFUND_FEE_ERR", "退款金额无效"),

    ACQ_CODE_100051("ACQ.TRADE_NOT_ALLOW_REFUND", "当前交易不允许退款"),
    ACQ_CODE_100052("ACQ.REFUND_FEE_ERROR", "交易退款金额有误"),

    //网关返回码
    RET_CODE_00000("00000", "未枚举的错误"),
    RET_CODE_10000("10000", "调用成功"),
    RET_CODE_20000("20000", "服务不可用"),
    RET_CODE_20001("20001", "授权权限不足"),
    RET_CODE_40001("40001", "缺少必选参数"),
    RET_CODE_40002("40002", "非法的参数"),
    RET_CODE_40004("40004", "业务处理失败"),
    RET_CODE_40006("40006", "权限不足"),

    //公共错误码
    SUB_CODE_10001("isp.unknow-error", "权限不足"),
    SUB_CODE_10002("isp.unknow-error", "服务暂不可用（业务系统不可用）"),
    SUB_CODE_10003("aop.unknow-error", "服务暂不可用（网关自身的未知错误）"),
    SUB_CODE_10004("aop.invalid-auth-token", "无效的访问令牌"),
    SUB_CODE_10005("aop.auth-token-time-out", "访问令牌已过期"),
    SUB_CODE_10006("aop.invalid-app-auth-token", "无效的应用授权令牌"),
    SUB_CODE_10007("aop.invalid-app-auth-token-no-api", "商户未授权当前接口"),
    SUB_CODE_10008("aop.app-auth-token-time-out", "应用授权令牌已过期"),
    SUB_CODE_10009("aop.no-product-reg-by-partner", "商户未签约任何产品"),
    SUB_CODE_10010("isv.missing-method", "缺少方法名参数"),
    SUB_CODE_10011("isv.missing-signature", "缺少签名参数"),
    SUB_CODE_10012("isv.missing-signature-type", "缺少签名类型参数"),
    SUB_CODE_10013("isv.missing-signature-key", "缺少签名配置"),
    SUB_CODE_10014("isv.missing-app-id", "缺少appId参数"),
    SUB_CODE_10015("isv.missing-timestamp", "缺少时间戳参数"),
    SUB_CODE_10016("isv.missing-version", "缺少版本参数"),
    SUB_CODE_10017("isv.decryption-error-missing-encrypt-type", "解密出错, 未指定加密算法"),
    SUB_CODE_10018("isv.invalid-parameter", "参数无效"),
    SUB_CODE_10019("isv.upload-fail", "文件上传失败"),
    SUB_CODE_10020("isv.invalid-file-extension", "文件扩展名无效"),
    SUB_CODE_10021("isv.invalid-file-size", "文件大小无效"),
    SUB_CODE_10022("isv.invalid-method", "不存在的方法名"),
    SUB_CODE_10023("isv.invalid-format", "无效的数据格式"),
    SUB_CODE_10024("isv.invalid-signature-type", "无效的签名类型"),
    SUB_CODE_10025("isv.invalid-signature", "无效签名"),
    SUB_CODE_10026("isv.invalid-encrypt-type", "无效的加密类型"),
    SUB_CODE_10027("isv.invalid-encrypt", "解密异常"),
    SUB_CODE_10028("isv.invalid-app-id", "无效的appId参数"),
    SUB_CODE_10029("isv.invalid-timestamp", "非法的时间戳参数"),
    SUB_CODE_10030("isv.invalid-charset", "字符集错误"),
    SUB_CODE_10031("isv.invalid-digest", "摘要错误"),
    SUB_CODE_10032("isv.decryption-error-not-valid-encrypt-type", "解密出错，不支持的加密算法"),
    SUB_CODE_10033("isv.decryption-error-not-valid-encrypt-key", "解密出错, 未配置加密密钥或加密密钥格式错误"),
    SUB_CODE_10034("isv.decryption-error-unknown", "解密出错，未知异常"),
    SUB_CODE_10035("isv.missing-signature-config", "验签出错, 未配置对应签名算法的公钥或者证书"),
    SUB_CODE_10036("isv.not-support-app-auth", "本接口不支持第三方代理调用"),
    SUB_CODE_10037("isv.insufficient-isv-permissions", "ISV权限不足"),
    SUB_CODE_10038("isv.insufficient-user-permissions", "用户权限不足"),
    SUB_CODE_10039("aop.ACQ.SYSTEM_ERROR", "系统异常");

    private String code;
    private String message;

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    AliPayEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AliPayEnum getObject(String code) {
        for (AliPayEnum aliPayEnum : AliPayEnum.values()) {
            if (StringUtils.equals(code, aliPayEnum.getCode())) {
                return aliPayEnum;
            }
        }
        return null;
    }
}
