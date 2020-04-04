package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayRefundOrder;

public interface IRefundOrderService {
    PayRefundOrder selectRefundOrder(String refundOrderId);

    /**
     * 创建退款订单
     * @param refundOrder
     * @return
     */
    int createRefundOrder(PayRefundOrder refundOrder);

    /**
     * 更新通知次数
     * @param refundOrderId
     * @param cnt
     * @return
     */
    int updateNotify4Count(String refundOrderId, Integer cnt);
}
