package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;

public interface IPayChannel4WxService {
    /**
     * 条码支付
     * @param channelId
     * @param payOrder
     * @return
     */
    String doWxPayReq(String channelId, PayOrder payOrder);

    /**
     * 扫码支付
     * @param channelId
     * @param payOrder
     * @return
     */
    String doWxPrePayReq(String channelId, PayOrder payOrder);

    /**
     * 订单查询
     * @param channelId
     * @param payOrder
     * @return
     */
    String doWxQueryReq(String channelId, PayOrder payOrder);

    /**
     * 订单撤销
     * @param channelId
     * @param refundOrder
     * @return
     */
    String doWxRefundReq(String channelId, PayRefundOrder refundOrder);

    /**
     * 订单撤销
     * @param channelId
     * @param refundOrder
     * @return
     */
    String doWxCancelReq(String channelId, PayRefundOrder refundOrder);
}
