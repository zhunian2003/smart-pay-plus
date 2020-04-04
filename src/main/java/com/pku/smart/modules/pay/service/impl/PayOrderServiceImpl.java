package com.pku.smart.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayMchNotify;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.mapper.PayMchNotifyMapper;
import com.pku.smart.modules.pay.mapper.PayOrderMapper;
import com.pku.smart.modules.pay.service.IPayChannel4AliService;
import com.pku.smart.modules.pay.service.IPayChannel4WxService;
import com.pku.smart.modules.pay.service.IPayOrderService;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PayOrderServiceImpl implements IPayOrderService {

    private final MyLog _log = MyLog.getLog(PayOrderServiceImpl.class);

    @Autowired
    PayOrderMapper payOrderMapper;

    @Autowired
    PayMchNotifyMapper payMchNotifyMapper;

    @Autowired
    IPayChannel4WxService payChannel4WxService;

    @Autowired
    IPayChannel4AliService payChannel4AliService;

    /**
     * 查询订单
     *
     * @param payOrderId 订单号
     * @return
     */
    @Override
    public PayOrder selectPayOrder(String payOrderId) {
        _log.info("查询支付订单：{}", payOrderId);
        return payOrderMapper.selectById(payOrderId);
    }

    /**
     * 查询支付订单
     *
     * @param mchId      商户号
     * @param mchOrderNo 商户订单号
     * @param payOrderId 支付订单号
     * @return
     */
    @Override
    public PayOrder selectPayOrder(String mchId, String mchOrderNo, String payOrderId) {
        _log.info("根据商户号：{}，商户订单号：{}，支付单号：{}查询订单", mchId, mchOrderNo, payOrderId);
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mch_id",mchId);
        if (StringUtils.isNotBlank(mchOrderNo)) {
            queryWrapper.eq("mch_order_no",mchOrderNo);
        }
        if (StringUtils.isNotBlank(payOrderId)) {
            queryWrapper.eq("pay_order_id",payOrderId);
        }
        return payOrderMapper.selectOne(queryWrapper);
    }

    /**
     * 查询支付订单
     *
     * @param mchId
     * @param mchOrderNo
     * @param payOrderId
     * @param executeNotify
     * @param executeQuery
     * @return
     */
    @Override
    public PayOrder selectPayOrder(String mchId, String mchOrderNo, String payOrderId, String executeNotify, String executeQuery) {
        PayOrder payOrder = this.selectPayOrder(mchId, mchOrderNo, payOrderId);

        if (payOrder != null) {
            boolean isQuery = Boolean.parseBoolean(executeQuery);
            _log.info("是否订单支付状态查询同步.{}", isQuery);
            if (isQuery) {
                String channelId = payOrder.getChannelId();
                if (PayConstant.PAY_CHANNEL_ALIPAY_BR.equals(channelId) || PayConstant.PAY_CHANNEL_ALIPAY_QR.equals(channelId)){
                    String result = payChannel4AliService.doAliQueryReq(channelId, payOrder);
                    _log.info("查询支付宝订单返回：{}", result);
                } else if (PayConstant.PAY_CHANNEL_WX_NATIVE.equals(channelId) || PayConstant.PAY_CHANNEL_WX_MICROPAY.equals(channelId)){
                    String result = payChannel4WxService.doWxQueryReq(channelId, payOrder);
                    _log.info("查询微信订单返回：{}", result);
                }
            }

            boolean isNotify = Boolean.parseBoolean(executeNotify);
            _log.info("是否订单再次发送业务支付通知.{}", isNotify);
            if (isNotify) {

            }
        }

        return payOrder;
    }

    /**
     * 创建支付订单
     *
     * @param payOrder
     * @return 返回受影响的行数 1、成功 0、失败
     */
    @Override
    public int createPayOrder(PayOrder payOrder) {
        String payOrderId = payOrder.getPayOrderId();
        PayOrder payOrderOld = payOrderMapper.selectById(payOrderId);
        if (payOrderOld != null){
            _log.error("订单{}已经存在",payOrderId);
            return 0;
        }
        payOrder.setNotifyCount(0);
        payOrder.setCreateTime(new Date());
        payOrder.setUpdateTime(new Date());
        return payOrderMapper.insert(payOrder);
    }

    /**
     * 更新订单状态 支付中
     *
     * @param payOrderId
     * @return
     */
    @Override
    public int updateStatus4Ing(String payOrderId) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (Integer.valueOf(payOrder.getStatus()) != PayConstant.PAY_STATUS_INIT){
            _log.error("订单{}状态不允许更新,可能已支付",payOrderId);
            return 0;
        }
        payOrder.setStatus(PayConstant.PAY_STATUS_PAYING.toString());
        payOrder.setPaySuccTime(System.currentTimeMillis());
        return payOrderMapper.updateById(payOrder);
    }

    /**
     * 更新订单状态 支付完成
     *
     * @param payOrderId
     * @return
     */
    @Override
    public int updateStatus4Success(String payOrderId) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (Integer.valueOf(payOrder.getStatus()) != PayConstant.PAY_STATUS_PAYING){
            _log.error("订单{}状态不允许更新,可能已支付",payOrderId);
            return 0;
        }
        payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS.toString());
        payOrder.setPaySuccTime(System.currentTimeMillis());
        return payOrderMapper.updateById(payOrder);
    }

    /**
     * 更新订单状态 处理完成(通知完毕)
     *
     * @param payOrderId
     * @return
     */
    @Override
    public int updateStatus4Complete(String payOrderId) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (Integer.valueOf(payOrder.getStatus()) < PayConstant.PAY_STATUS_INIT){
            _log.error("订单{}状态不允许更新,可能未支付",payOrderId);
            return 0;
        }
        payOrder.setStatus(PayConstant.PAY_STATUS_COMPLETE.toString());
        payOrder.setPaySuccTime(System.currentTimeMillis());
        return payOrderMapper.updateById(payOrder);
    }

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     *
     * @param payOrderId
     * @return
     */
    @Override
    public int updateStatus4Closed(String payOrderId) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (Integer.valueOf(payOrder.getStatus()) >= PayConstant.PAY_STATUS_SUCCESS){
            _log.error("订单{}状态不允许更新,可能已支付",payOrderId);
            return 0;
        }
        _log.info("针对支付宝设置过期");
        payOrder.setStatus(PayConstant.PAY_STATUS_EXPIRED.toString());//TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
        return payOrderMapper.updateById(payOrder);
    }

    /**
     * 更新订单状态 支付失败
     *
     * @param payOrderId
     * @return
     */
    @Override
    public int updateStatus4Failed(String payOrderId) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (Integer.valueOf(payOrder.getStatus()) >= PayConstant.PAY_STATUS_SUCCESS){
            _log.error("订单{}状态不允许更新,可能已支付",payOrderId);
            return 0;
        }
        _log.info("针对微信设置过期");
        payOrder.setStatus(PayConstant.PAY_STATUS_EXPIRED.toString());//PAYERROR--支付失败(其他原因，如银行返回失败)
        return payOrderMapper.updateById(payOrder);
    }

    /**
     * 更新通知次数
     *
     * @param payOrderId
     * @param cnt
     * @return
     */
    @Override
    public int updateNotify4Count(String payOrderId, Integer cnt) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }

        _log.info("更新订单通知次数");
        payOrder.setNotifyCount(cnt);
        payOrder.setLastNotifyTime(System.currentTimeMillis());
        payOrderMapper.updateById(payOrder);

        PayMchNotify mchNotify = payMchNotifyMapper.selectById(payOrderId);
        if (mchNotify == null){
            mchNotify = new PayMchNotify();
            mchNotify.setOrderId(payOrderId);
            mchNotify.setMchId(payOrder.getMchId());
            mchNotify.setMchOrderNo(payOrder.getMchOrderNo());
            mchNotify.setOrderType("1");//'订单类型:1-支付,2-转账,3-退款'
            mchNotify.setNotifyUrl(payOrder.getNotifyUrl());
            mchNotify.setResult("通知中");//通知响应结果
            mchNotify.setStatus("1");//通知状态,1-通知中,2-通知成功,3-通知失败
            mchNotify.setCreateTime(new Date());
            return payMchNotifyMapper.insert(mchNotify);
        }
        mchNotify.setStatus("2");
        mchNotify.setNotifyCount(cnt);
        mchNotify.setLastNotifyTime(new Date());
        return payMchNotifyMapper.updateById(mchNotify);
    }

    /**
     * 更新支付宝微信订单号
     *
     * @param payOrderId
     * @param channelOrderNo
     * @return
     */
    @Override
    public int updateChannelOrderNo(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = this.selectPayOrder(payOrderId);
        if (payOrder == null){
            _log.error("订单{}不存在",payOrderId);
            return 0;
        }
        if (StringUtils.isBlank(channelOrderNo)){
            _log.error("微信支付宝订单号{}不允许为空。", channelOrderNo);
            return 0;
        }
        payOrder.setChannelOrderNo(channelOrderNo);
        return payOrderMapper.updateById(payOrder);
    }
}
