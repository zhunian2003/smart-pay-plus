package com.pku.smart.common.enums;

import org.apache.commons.lang3.StringUtils;

public enum TradeEnum {

    //微信支付宝交易状态
    TRADE_STATE_10000("UNKNOW","未知状态"),
    TRADE_STATE_10001("SUCCESS","支付成功"),
    TRADE_STATE_10002("REFUND","转入退款"),
    TRADE_STATE_10003("NOTPAY","未支付"),
    TRADE_STATE_10004("CLOSED","已关闭"),
    TRADE_STATE_10005("REVOKED","已撤销（刷卡支付）"),
    TRADE_STATE_10006("USERPAYING","用户支付中"),
    TRADE_STATE_10007("PAYERROR","支付失败(其他原因，如银行返回失败)"), //以下为支付宝交易状态
    TRADE_STATE_10008("WAIT_BUYER_PAY","交易创建，等待买家付款"),
    TRADE_STATE_10009("TRADE_CLOSED","未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_STATE_10010("TRADE_SUCCESS","交易支付成功"),
    TRADE_STATE_10011("TRADE_FINISHED","交易结束，不可退款");

    private String code;
    private String message;

    public String getCode()
    {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    TradeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static TradeEnum getObject(String code) {
        for(TradeEnum tradeEnum : TradeEnum.values()){
            if(StringUtils.equals(code, tradeEnum.getCode())){
                return tradeEnum;
            }
        }
        return null;
    }
}
