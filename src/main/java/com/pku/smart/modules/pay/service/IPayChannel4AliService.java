package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;

public interface IPayChannel4AliService {
    /**
     * 条码支付
     * @param channelId
     * @param payOrder
     * @return
     */
    String doAliPayReq(String channelId, PayOrder payOrder);

    /**
     * 扫码支付
     * @param channelId
     * @param payOrder
     * @return
     */
    String doAliPrePayReq(String channelId, PayOrder payOrder);

    /**
     * 订单查询
     * @param channelId
     * @param payOrder
     * @return
     */
    String doAliQueryReq(String channelId, PayOrder payOrder);

    /**
     * 订单退费
     * @param channelId
     * @param refundOrder
     * @return
     */
    String doAliRefundReq(String channelId, PayRefundOrder refundOrder);

    /**
     * 订单撤销
     * @param channelId
     * @param refundOrder
     * @return
     */
    String doAliCancelReq(String channelId, PayRefundOrder refundOrder);
}
