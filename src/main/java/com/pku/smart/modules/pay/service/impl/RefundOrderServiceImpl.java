package com.pku.smart.modules.pay.service.impl;

import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayMchNotify;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;
import com.pku.smart.modules.pay.mapper.PayMchNotifyMapper;
import com.pku.smart.modules.pay.mapper.PayOrderMapper;
import com.pku.smart.modules.pay.mapper.PayRefundOrderMapper;
import com.pku.smart.modules.pay.service.IRefundOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RefundOrderServiceImpl implements IRefundOrderService {

    private final MyLog _log = MyLog.getLog(RefundOrderServiceImpl.class);

    @Autowired
    PayOrderMapper payOrderMapper;

    @Autowired
    PayMchNotifyMapper mchNotifyMapper;

    @Autowired
    PayRefundOrderMapper refundOrderMapper;

    @Override
    public PayRefundOrder selectRefundOrder(String refundOrderId) {
        _log.info("查询退款订单：{}", refundOrderId);
        return refundOrderMapper.selectById(refundOrderId);
    }

    /**
     * 创建退款订单
     *
     * @param refundOrder
     * @return
     */
    @Override
    public int createRefundOrder(PayRefundOrder refundOrder) {
        String payOrderId = refundOrder.getPayOrderId();
        PayOrder payOrder = payOrderMapper.selectById(payOrderId);
        if (payOrder == null){
            _log.error("退款订单对应的支付订单{}不存在", payOrderId);
            return 0;
        }
        Long refundAmount = refundOrder.getRefundAmount();
        if (refundAmount.longValue() != payOrder.getAmount().longValue()){
            _log.warn("部分退");
        }
        String refundOrderId = refundOrder.getRefundOrderId();
        PayRefundOrder refundOrderOld = refundOrderMapper.selectById(refundOrderId);
        if (refundOrderOld != null){
            _log.error("退款订单{}已经存在", refundOrderId);
            return 0;
        }
        refundOrder.setMchRefundNo(refundOrderId);
        refundOrder.setChannelPayOrderNo(payOrder.getChannelOrderNo());
        refundOrder.setChannelId(payOrder.getChannelId());
        refundOrder.setPayAmount(payOrder.getAmount());
        refundOrder.setChannelMchId(payOrder.getChannelMchId());
        refundOrder.setUpdateTime(new Date());
        refundOrder.setCreateTime(new Date());
        return refundOrderMapper.insert(refundOrder);
    }

    /**
     * 更新通知次数
     *
     * @param refundOrderId
     * @param cnt
     * @return
     */
    @Override
    public int updateNotify4Count(String refundOrderId, Integer cnt) {
        PayRefundOrder refundOrder = this.selectRefundOrder(refundOrderId);
        if (refundOrder == null){
            _log.error("退款订单{}不存在",refundOrderId);
            return 0;
        }

        _log.info("创建或更新商户通知");
        PayMchNotify mchNotify = mchNotifyMapper.selectById(refundOrderId);
        if (mchNotify == null){
            mchNotify = new PayMchNotify();
            mchNotify.setOrderId(refundOrderId);
            mchNotify.setMchId(refundOrder.getMchId());
            mchNotify.setMchOrderNo(refundOrder.getMchRefundNo());
            mchNotify.setOrderType("3");//'订单类型:1-支付,2-转账,3-退款'
            mchNotify.setNotifyUrl(refundOrder.getNotifyUrl());
            mchNotify.setResult("通知中");//通知响应结果
            mchNotify.setStatus("1");//通知状态,1-通知中,2-通知成功,3-通知失败
            mchNotify.setCreateTime(new Date());
            return mchNotifyMapper.insert(mchNotify);
        }
        mchNotify.setStatus("2");
        mchNotify.setNotifyCount(cnt);
        mchNotify.setLastNotifyTime(new Date());
        return mchNotifyMapper.updateById(mchNotify);
    }
}
