package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayOrder;

public interface IPayOrderService {

    /**
     * 查询订单
     * @param payOrderId 订单号
     * @return
     */
    PayOrder selectPayOrder(String payOrderId);

    /**
     * 查询支付订单
     * @param mchId 商户号
     * @param mchOrderNo 商户订单号
     * @param payOrderId 支付订单号
     * @return
     */
    PayOrder selectPayOrder(String mchId, String mchOrderNo, String payOrderId);

    /**
     * 查询支付订单
     * @return
     */
    PayOrder selectPayOrder(String mchId, String mchOrderNo, String payOrderId, String executeNotify, String executeQuery);

    /**
     * 创建支付订单
     * @param payOrder
     * @return 返回受影响的行数 1、成功 0、失败
     */
    int createPayOrder(PayOrder payOrder);

    /**
     * 更新订单状态 支付中
     * @param payOrderId
     * @return
     */
    int updateStatus4Ing(String payOrderId);

    /**
     * 更新订单状态 支付完成
     * @param payOrderId
     * @return
     */
    int updateStatus4Success(String payOrderId);

    /**
     * 更新订单状态 处理完成(通知完毕)
     * @param payOrderId
     * @return
     */
    int updateStatus4Complete(String payOrderId);

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     * @param payOrderId
     * @return
     */
    int updateStatus4Closed(String payOrderId);

    /**
     * 更新订单状态 支付失败
     * @param payOrderId
     * @return
     */
    int updateStatus4Failed(String payOrderId);

    /**
     * 更新通知次数
     * @param payOrderId
     * @param cnt
     * @return
     */
    int updateNotify4Count(String payOrderId, Integer cnt);

    /**
     * 更新支付宝微信订单号
     * @param payOrderId
     * @param channelOrderNo
     * @return
     */
    int updateChannelOrderNo(String payOrderId, String channelOrderNo);
}
