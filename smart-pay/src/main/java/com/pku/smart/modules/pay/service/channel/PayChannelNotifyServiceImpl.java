package com.pku.smart.modules.pay.service.channel;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.service.WxPayService;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.config.AliPayConfig;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;
import com.pku.smart.modules.pay.service.IPayChannelNotifyService;
import com.pku.smart.modules.pay.service.IPayChannelService;
import com.pku.smart.modules.pay.service.IPayOrderService;
import com.pku.smart.modules.pay.service.IRefundOrderService;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PayChannelNotifyServiceImpl extends PayChannel4WxService implements IPayChannelNotifyService {

    private static final MyLog _log = MyLog.getLog(PayChannelNotifyServiceImpl.class);

    @Autowired
    AliPayConfig aliPayConfig;

    @Autowired
    IPayOrderService payOrderService;

    @Autowired
    IPayChannelService payChannelService;

    @Autowired
    IRefundOrderService refundOrderService;

    @Override
    public String handleAliPayNotify(String mchId, String channelId, Map params) {
        String logPrefix = "【处理支付宝支付回调】";
        _log.info("====== 开始处理支付宝支付回调通知 ======");

        String errorMessage;

        String out_trade_no = String.valueOf(params.get("out_trade_no"));        // 商户订单号
        String total_amount = String.valueOf(params.get("total_amount"));        // 支付金额

        if (StringUtils.isEmpty(out_trade_no)) {
            _log.error("商户订单号{}为空", out_trade_no);
            return PayConstant.RETURN_VALUE_FAIL;
        }
        if (StringUtils.isEmpty(total_amount)) {
            _log.error("支付金额{}为空", total_amount);
            return PayConstant.RETURN_VALUE_FAIL;
        }

        String payOrderId = out_trade_no;
        PayOrder payOrder = payOrderService.selectPayOrder(payOrderId);
        if (payOrder == null) {
            errorMessage = "支付订单" + payOrderId + "不存在";
            _log.error("支付宝回调验证失败：{}", errorMessage);
            return PayConstant.RETURN_VALUE_FAIL;
        }

        PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        if (payChannel == null) {
            errorMessage = "支付渠道" + channelId + "不存在";
            _log.error("支付宝回调验证失败：{}", errorMessage);
            return PayConstant.RETURN_VALUE_FAIL;
        }

        boolean verify_result = false;
        try {
            aliPayConfig.init(payChannel.getParam());
            verify_result = AlipaySignature.rsaCheckV1(params, aliPayConfig.init(payChannel.getParam()).getAlipay_public_key(), aliPayConfig.CHARSET, aliPayConfig.getSign_type());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        _log.info("验证签名");
        if (!verify_result) {
            _log.error("支付宝回调签名验证失败");
            return PayConstant.RETURN_VALUE_FAIL;
        }

        _log.info("核对金额");
        long aliPayAmt = new BigDecimal(total_amount).movePointRight(2).longValue();
        long dbPayAmt = payOrder.getAmount().longValue();
        if (dbPayAmt != aliPayAmt) {
            _log.error("支付金额{}验证失败", aliPayAmt);
            return PayConstant.RETURN_VALUE_FAIL;
        }

        _log.info("{}验证支付通知数据及签名通过", logPrefix);
        //doNotify(payOrder, "2");
        _log.info("====== 完成处理支付宝支付回调通知 ======");

        return PayConstant.RETURN_VALUE_SUCCESS;
    }

    @Override
    public String handleWxPayNotify(String mchId, String channelId, String pay, String xmlResult) {
        String logPrefix = "【处理微信回调】";

        _log.info("====== 开始处理微信回调通知 ======");
        String errorMessage = "";
        try {
            Object object = doValidWxNotify(mchId, channelId, pay, xmlResult);
            if (object instanceof String){
                return WxPayNotifyResponse.fail(object.toString());
            }

            _log.info("根据支付类型处理业务订单");
            if (object instanceof PayOrder) {
                _log.info("{}微信支付通知回调", logPrefix);
                PayOrder payOrder = (PayOrder)object;
                //doNotify(payOrder, "2");
                _log.info("====== 完成处理微信支付回调通知 ======");
            } else if (object instanceof PayRefundOrder){
                _log.info("{}微信退款通知回调", logPrefix);
                PayRefundOrder refundOrder = (PayRefundOrder)object;
                //doNotify(refundOrder, "1");
                _log.info("====== 完成处理微信退款回调通知 ======");
            }

            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            e.printStackTrace();
            _log.error("微信回调结果异常,异常原因{}", e.getMessage());
            return WxPayNotifyResponse.fail(e.getMessage());
        }
    }

    private Object doValidWxNotify(String mchId, String channelId, String pay, String xmlResult){
        String errorMessage = "";
        try {
            PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
            if (payChannel == null) {
                errorMessage = "支付渠道" + channelId + "不存在";
                _log.error("支付宝回调验证失败：{}", errorMessage);
                return errorMessage;
            }

            if (PayConstant.TRADE_TYPE_PAY.equalsIgnoreCase(pay)){
                WxPayService wxPayService = super.buildWxpayService(channelId, mchId, pay);
                WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
                String payOrderId = result.getOutTradeNo();
                _log.info("支付单号：{}", payOrderId);

                PayOrder payOrder = payOrderService.selectPayOrder(payOrderId);
                if (payOrder == null) {
                    errorMessage = "支付订单" + payOrderId + "不存在";
                    _log.error("微信回调验证失败：{}", errorMessage);
                    return errorMessage;
                }

                String tradeNo = result.getTransactionId();
                String totalFee = BaseWxPayResult.fenToYuan(result.getTotalFee());
                _log.info("回调返回订单号：{}，微信单号{}，金额：{}", payOrderId, tradeNo, totalFee);

                if (StringUtils.isNotBlank(payOrder.getChannelOrderNo()) && tradeNo.equalsIgnoreCase(payOrder.getChannelOrderNo())) {
                    errorMessage = "微信订单号" + tradeNo + "与业务系统不一致";
                    _log.error(errorMessage);
                    return errorMessage;
                }

                if (payOrder.getAmount().intValue() != result.getTotalFee()) {
                    errorMessage = "微信订单支付金额" + totalFee + "与业务系统不一致";
                    _log.error(errorMessage);
                    return errorMessage;
                }

                _log.info("验证微信通知数据及签名通过");
                return payOrder;
            } else if (PayConstant.TRADE_TYPE_REFUND.equalsIgnoreCase(pay)){
                WxPayService wxPayService = super.buildWxpayService(channelId, mchId, pay);
                WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xmlResult);
                String refundOrderId = result.getReqInfo().getOutRefundNo();
                _log.info("退款单号：{}", refundOrderId);

                PayRefundOrder refundOrder = refundOrderService.selectRefundOrder(refundOrderId);
                if (refundOrder == null) {
                    errorMessage = "退款订单" + refundOrder + "不存在";
                    _log.error("微信回调验证失败：{}", errorMessage);
                    return errorMessage;
                }

                String tradeNo = result.getReqInfo().getRefundId();
                String totalFee = BaseWxPayResult.fenToYuan(result.getReqInfo().getRefundFee());
                _log.info("回调返回订单号：{}，微信单号{}，金额：{}", refundOrderId, tradeNo, totalFee);

                _log.info("验证微信退款通知数据及签名通过");
                return refundOrder;
            } else {
                errorMessage = "未知的支付类型：" + pay;
                return errorMessage;
            }

        } catch (Exception e) {
            e.printStackTrace();
            _log.error("微信回调结果异常,异常原因{}", e.getMessage());
            errorMessage = e.getMessage();
            return errorMessage;
        }
    }
}
