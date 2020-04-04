package com.pku.smart.common.constant;

import java.io.File;

public class PayConstant {
    public final static String PAY_CHANNEL_WX_NATIVE = "WX_NATIVE";				// 微信原生扫码支付
    public final static String PAY_CHANNEL_WX_MICROPAY = "WX_MICROPAY";         // 微信条码支付
    public final static String PAY_CHANNEL_ALIPAY_QR = "ALIPAY_QR";	    		// 支付宝当面付之扫码支付
    public final static String PAY_CHANNEL_ALIPAY_BR = "ALIPAY_BR";             // 支付宝条码支付
    public final static String PAY_CHANNEL_ALIPAY_PROVIDER_ID = "2088002560413959";

    public final static String CHANNEL_NAME_WX = "WX"; 				// 渠道名称:微信
    public final static String CHANNEL_NAME_ALIPAY = "ALIPAY"; 		// 渠道名称:支付宝

    public final static Integer PAY_STATUS_EXPIRED = -2; 	// 订单过期
    public final static Integer PAY_STATUS_FAILED = -1; 	// 支付失败
    public final static Integer PAY_STATUS_INIT = 0; 		// 初始态
    public final static Integer PAY_STATUS_PAYING = 1; 	// 支付中
    public final static Integer PAY_STATUS_SUCCESS = 2; 	// 支付成功
    public final static Integer PAY_STATUS_COMPLETE = 3; 	// 业务完成

    public final static Integer REFUND_STATUS_INIT = 0; 		// 初始态
    public final static Integer REFUND_STATUS_REFUNDING = 1; 	// 转账中
    public final static Integer REFUND_STATUS_SUCCESS = 2; 	// 成功
    public final static Integer REFUND_STATUS_FAIL = 3; 		// 失败
    public final static Integer REFUND_STATUS_COMPLETE = 4; 	// 业务完成

    public static final String RETURN_VALUE_SUCCESS = "SUCCESS";
    public static final String RETURN_VALUE_FAIL = "FAIL";

    public static final String TRADE_TYPE_PAY = "SF";   //收费
    public static final String TRADE_TYPE_REFUND = "TF";//退费

    public final static String RESP_UTF8 = "UTF-8";			// 通知业务系统使用的编码

    public static class WxConstant {
        public final static String TRADE_TYPE_APP = "APP";									// APP支付
        public final static String TRADE_TYPE_JSPAI = "JSAPI";								// 公众号支付或小程序支付
        public final static String TRADE_TYPE_NATIVE = "NATIVE";							// 原生扫码支付
        public final static String TRADE_TYPE_MWEB = "MWEB";								// H5支付
    }

    public static class AlipayConstant {
        public final static String CONFIG_PATH = "alipay" + File.separator + "alipay";	// 支付宝移动支付
        public final static String TRADE_STATUS_WAIT = "WAIT_BUYER_PAY";		// 交易创建,等待买家付款
        public final static String TRADE_STATUS_CLOSED = "TRADE_CLOSED";		// 交易关闭
        public final static String TRADE_STATUS_SUCCESS = "TRADE_SUCCESS";		// 交易成功
        public final static String TRADE_STATUS_FINISHED = "TRADE_FINISHED";	// 交易成功且结束
    }
}
