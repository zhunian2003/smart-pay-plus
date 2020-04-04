package com.pku.smart.modules.pay.service.channel;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.service.WxPayService;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.config.MicroPayConfig;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;
import com.pku.smart.modules.pay.service.IPayChannel4WxService;
import com.pku.smart.modules.pay.service.IPayOrderService;
import com.pku.smart.modules.pay.vopackage.VoTradeResult;
import com.pku.smart.utils.PayUtils;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayChannel4WxServiceImpl extends PayChannel4WxService implements IPayChannel4WxService {

    private final MyLog _log = MyLog.getLog(PayChannel4WxServiceImpl.class);

    @Autowired
    MicroPayConfig microPayConfig;

    @Autowired
    IPayOrderService payOrderService;

    @Override
    public String doWxPayReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【微信支付条码支付】";

        String mchId = payOrder.getMchId();
        String payOrderId = payOrder.getPayOrderId();
        String channelOrderNo = payOrder.getChannelOrderNo();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        WxPayService wxPayService = super.buildWxpayService(channelId, mchId, PayConstant.TRADE_TYPE_PAY);
        WxPayMicropayRequest request = new WxPayMicropayRequest();
        request.setOutTradeNo(payOrder.getPayOrderId());
        request.setBody(payOrder.getBody());
        request.setTotalFee(payOrder.getAmount().intValue());
        request.setFeeType(payOrder.getCurrency());
        request.setSpbillCreateIp(payOrder.getClientIp());
        request.setAuthCode(payOrder.getAuthCode());
        request.setAttach(mchId);

        _log.info("调用微信服务");
        VoTradeResult tradeResult = super.payMicropay(wxPayService, request);
        _log.info("{}调用微信服务返回：{}", logPrefix, JSON.toJSONString(tradeResult));

        String errorMessage = "";
        if (tradeResult.getResultSuccess()) {
            WxPayMicropayResult result = (WxPayMicropayResult) tradeResult.getResultObject();
            if ("SUCCESS".equals(result.getReturnCode())) {
                if ("SUCCESS".equals(result.getResultCode())) {
                    Map<String, Object> retMap = new HashMap<>();

                    retMap.put("openid", result.getOpenid());
                    retMap.put("is_subscribe", result.getIsSubscribe());
                    retMap.put("trade_type", result.getTradeType());
                    retMap.put("bank_type", result.getBankType());
                    retMap.put("fee_type", result.getFeeType());

                    retMap.put("total_fee", result.getTotalFee());
                    retMap.put("settlement_total_fee ", result.getSettlementTotalFee());
                    retMap.put("coupon_fee", result.getCouponFee());
                    retMap.put("cash_fee_type", result.getCashFeeType());
                    retMap.put("cash_fee", result.getCashFee());

                    retMap.put("transaction_id", result.getTransactionId());
                    retMap.put("out_trade_no", result.getOutTradeNo());
                    retMap.put("attach", result.getAttach());
                    retMap.put("time_end", result.getTimeEnd());
                    retMap.put("promotion_detail", result.getPromotionDetail());

                    //以下为自定义返回
                    retMap.put("outTradeNo", result.getOutTradeNo());
                    retMap.put("mchOrderNo", payOrder.getMchOrderNo());    //平台商户单号
                    retMap.put("payOrderId", result.getOutTradeNo());      //商户支付单号
                    retMap.put("channelOrderNo", result.getTransactionId());//微信支付单号

                    //是否需要更新本地订单状态
                    return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
                } else {
                    _log.error("微信支付条码支付失败：业务失败");
                    String errCode = result.getErrCode();
                    String errCodeDes = result.getErrCodeDes();
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
                }
            } else {
                errorMessage = "微信支付条码支付失败：通信失败";
                String errCode = result.getReturnCode();
                String errCodeDes = result.getReturnMsg();
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
            }
        } else {
            String errCode = tradeResult.getResultCode();
            String errCodeDes = tradeResult.getResultMsg();
            errorMessage = "微信支付条码支付失败";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    @Override
    public String doWxPrePayReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【微信支付统一下单】";

        String mchId = payOrder.getMchId();
        String mchOrderNo = payOrder.getMchOrderNo();
        String payOrderId = payOrder.getPayOrderId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        WxPayService wxPayService = super.buildWxpayService(channelId, mchId, PayConstant.TRADE_TYPE_PAY);
        String tradeType = wxPayService.getConfig().getTradeType();
        Integer totalFee = payOrder.getAmount().intValue();// 支付金额,单位分
        String deviceInfo = payOrder.getDevice();
        String body = payOrder.getBody();
        String detail = null;
        String attach = mchId;
        String outTradeNo = payOrderId;
        String feeType = "CNY";
        String spBillCreateIP = payOrder.getClientIp();
        String timeStart = null;
        String timeExpire = null;
        String goodsTag = null;
        String notifyUrl = wxPayService.getConfig().getNotifyUrl();
        String productId = null;
        if (tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_NATIVE)) {
            productId = JSON.parseObject(payOrder.getExtra()).getString("productId");
        }
        String limitPay = null;
        String openId = null;
        if (tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_JSPAI)) {
            openId = JSON.parseObject(payOrder.getExtra()).getString("openId");
        }
        String sceneInfo = null;
        if (tradeType.equals(PayConstant.WxConstant.TRADE_TYPE_MWEB)) {
            sceneInfo = JSON.parseObject(payOrder.getExtra()).getString("sceneInfo");
        }
        // 微信统一下单请求对象
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setDeviceInfo(deviceInfo);
        request.setBody(body);
        request.setDetail(detail);
        request.setAttach(attach);
        request.setOutTradeNo(outTradeNo);
        request.setFeeType(feeType);
        request.setTotalFee(totalFee);
        request.setSpbillCreateIp(spBillCreateIP);
        request.setTimeStart(timeStart);
        request.setTimeExpire(timeExpire);
        request.setGoodsTag(goodsTag);
        request.setNotifyUrl(notifyUrl);
        request.setTradeType(tradeType);
        request.setProductId(productId);
        request.setLimitPay(limitPay);
        request.setOpenid(openId);
        request.setSceneInfo(sceneInfo);

        _log.info("调用微信服务");
        VoTradeResult tradeResult = super.payUnifiedOrder(wxPayService, request);
        _log.info("{}调用微信服务返回：{}", logPrefix, JSON.toJSONString(tradeResult));

        String errorMessage = "";
        if (tradeResult.getResultSuccess()) {
            WxPayUnifiedOrderResult result = (WxPayUnifiedOrderResult) tradeResult.getResultObject();
            if ("SUCCESS".equals(result.getReturnCode())) {
                if ("SUCCESS".equals(result.getResultCode())) {
                    Map<String, Object> retMap = new HashMap<>();

                    retMap.put("trade_type", result.getTradeType());
                    retMap.put("prepay_id", result.getPrepayId());
                    retMap.put("code_url", result.getCodeURL());
                    retMap.put("mweb_url", result.getMwebUrl());

                    //以下为自定义返回
                    retMap.put("mchOrderNo", mchOrderNo);
                    retMap.put("payOrderId", payOrderId);
                    retMap.put("channelOrderNo", result.getPrepayId());
                    int count = payOrderService.updateStatus4Ing(payOrderId);
                    if (count != 1) {
                        errorMessage = "更新订单状态失败";
                        _log.error(errorMessage);
                        return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                    }
                    switch (tradeType) {
                        case PayConstant.WxConstant.TRADE_TYPE_NATIVE: {
                            retMap.put("codeUrl", result.getCodeURL());   // 二维码支付链接
                            break;
                        }
                        case PayConstant.WxConstant.TRADE_TYPE_MWEB: {
                            retMap.put("payUrl", result.getMwebUrl());    // h5支付链接地址
                            break;
                        }
                    }

                    return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
                } else {
                    errorMessage = "微信支付统一下单：业务失败";
                    String errCode = result.getErrCode();
                    String errCodeDes = result.getErrCodeDes();
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
                }
            } else {
                errorMessage = "微信支付统一下单：通信失败";
                String errCode = result.getReturnCode();
                String errCodeDes = result.getReturnMsg();
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
            }
        } else {
            String errCode = tradeResult.getResultCode();
            String errCodeDes = tradeResult.getResultMsg();
            errorMessage = "微信支付统一下单失败";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    @Override
    public String doWxQueryReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【微信支付订单查询】";

        String mchId = payOrder.getMchId();
        String payOrderId = payOrder.getPayOrderId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        WxPayService wxPayService = super.buildWxpayService(channelId, mchId, PayConstant.TRADE_TYPE_PAY);
        WxPayOrderQueryRequest request = new WxPayOrderQueryRequest();
        request.setTransactionId(payOrder.getChannelOrderNo());
        request.setOutTradeNo(payOrder.getPayOrderId());

        _log.info("调用微信服务");
        VoTradeResult tradeResult = super.payOrderQuery(wxPayService, request);
        _log.info("{}调用微信服务返回：{}", logPrefix, JSON.toJSONString(tradeResult));

        String errorMessage = "";
        if (tradeResult.getResultSuccess()) {
            WxPayOrderQueryResult result = (WxPayOrderQueryResult) tradeResult.getResultObject();
            if ("SUCCESS".equals(result.getReturnCode())) {
                if ("SUCCESS".equals(result.getResultCode())) {
                    PayOrder order = payOrderService.selectPayOrder(payOrderId);
                    if (order == null) {
                        _log.error("支付订单{}不存在", payOrderId);
                        errorMessage = "支付订单" + payOrderId + "不存在";
                        return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                    }

                    Map<String, Object> retMap = new HashMap<>();
                    String tradeState = result.getTradeState();

                    retMap.put("device_info", result.getOutTradeNo());
                    retMap.put("openid", result.getOutTradeNo());
                    retMap.put("is_subscribe", result.getIsSubscribe());
                    retMap.put("trade_type", result.getTradeType());
                    retMap.put("trade_state", result.getTradeState());

                    retMap.put("bank_type", result.getBankType());
                    retMap.put("fee_type", result.getFeeType());
                    retMap.put("total_fee", result.getTotalFee());
                    retMap.put("settlement_total_fee", result.getSettlementTotalFee());
                    retMap.put("coupon_fee", result.getCouponFee());

                    retMap.put("cash_fee_type", result.getCashFeeType());
                    retMap.put("cash_fee", result.getCashFee());
                    retMap.put("coupon_fee ", result.getCashFee());
                    retMap.put("coupon_count", result.getCashFee());

                    retMap.put("transaction_id", result.getTransactionId());
                    retMap.put("out_trade_no", result.getOutTradeNo());
                    retMap.put("attach", result.getAttach());
                    retMap.put("time_end", result.getTimeEnd());
                    retMap.put("trade_state_desc", result.getTradeStateDesc());

                    //以下为自定义返回
                    retMap.put("outTradeNo", result.getOutTradeNo());
                    retMap.put("mchOrderNo", payOrder.getMchOrderNo());
                    retMap.put("payOrderId", payOrderId);
                    retMap.put("channelOrderNo", result.getTransactionId());
                    retMap.put("tradeStatus", tradeState);

                    //是否需要更新本地订单状态
                    String tradeStatus = result.getTradeState();
                    _log.info("订单状态：{}", tradeStatus);

                    String channelOrderNo = order.getChannelOrderNo();
                    if (StringUtils.isBlank(channelOrderNo) && tradeStatus.equalsIgnoreCase("SUCCESS")) {
                        channelOrderNo = result.getTransactionId();
                        _log.info("微信订单号：{}", channelOrderNo);
                        int count = payOrderService.updateChannelOrderNo(payOrderId, channelOrderNo);
                        if (count != 1) {
                            errorMessage = "更新微信订单号失败";
                            _log.error(errorMessage);
                            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                        }
                    }

                    if (tradeStatus.equalsIgnoreCase("SUCCESS")) {
                        _log.info("支付成功");
                        if (Integer.valueOf(order.getStatus()) < PayConstant.PAY_STATUS_SUCCESS && Integer.valueOf(order.getStatus()) >= PayConstant.PAY_STATUS_INIT) {
                            _log.info("更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_SUCCESS);
                            int count = payOrderService.updateStatus4Success(payOrderId);
                            if (count != 1) {
                                errorMessage = "更新微信订单状态失败";
                                _log.error(errorMessage);
                                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                            }
                        }
                    } else if (tradeStatus.equalsIgnoreCase("REFUND")) {
                        _log.info("转入退款");
                    } else if (tradeStatus.equalsIgnoreCase("NOTPAY")) {
                        _log.info("未支付");
                    } else if (tradeStatus.equalsIgnoreCase("CLOSED")) {
                        _log.info("已关闭");
                    } else if (tradeStatus.equalsIgnoreCase("REVOKED")) {
                        _log.info("已撤销（刷卡支付）");
                    } else if (tradeStatus.equalsIgnoreCase("USERPAYING")) {
                        _log.info("用户支付中");
                    } else if (tradeStatus.equalsIgnoreCase("PAYERROR")) {
                        _log.info("支付失败(其他原因，如银行返回失败)");
                        _log.info("未付款交易超时关闭，或支付完成后全额退款");
                        if (Integer.valueOf(order.getStatus()) < PayConstant.PAY_STATUS_SUCCESS && Integer.valueOf(order.getStatus()) >= PayConstant.PAY_STATUS_INIT) {
                            _log.info("更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_FAILED);
                            int count = payOrderService.updateStatus4Failed(payOrderId);
                            if (count != 1) {
                                errorMessage = "更新微信订单状态失败";
                                _log.error(errorMessage);
                                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                            }
                        }
                    }

                    return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
                } else {
                    errorMessage = "微信支付订单查询失败：业务失败";
                    String errCode = result.getErrCode();
                    String errCodeDes = result.getErrCodeDes();
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
                }
            } else {
                errorMessage = "微信支付订单查询失败：通信失败";
                String errCode = result.getReturnCode();
                String errCodeDes = result.getReturnMsg();
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
            }
        } else {
            String errCode = tradeResult.getResultCode();
            String errCodeDes = tradeResult.getResultMsg();
            errorMessage = "微信支付订单查询失败";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    @Override
    public String doWxRefundReq(String channelId, PayRefundOrder refundOrder) {
        String logPrefix = "【微信支付订单退款】";
        ;

        String mchId = refundOrder.getMchId();
        String payOrderId = refundOrder.getPayOrderId();
        String refundOrderId = refundOrder.getRefundOrderId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}退款单号：{}", logPrefix, refundOrderId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        WxPayService wxPayService = super.buildWxpayService(channelId, mchId, PayConstant.TRADE_TYPE_REFUND);
        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setTransactionId(refundOrder.getChannelPayOrderNo());
        request.setOutTradeNo(refundOrder.getPayOrderId());
        request.setDeviceInfo(refundOrder.getDevice());
        request.setOutRefundNo(refundOrder.getRefundOrderId());
        request.setRefundDesc(refundOrder.getRemarkInfo());
        request.setRefundFee(refundOrder.getRefundAmount().intValue());
        request.setRefundFeeType("CNY");
        request.setTotalFee(refundOrder.getPayAmount().intValue());
        request.setNotifyUrl(wxPayService.getConfig().getNotifyUrl());

        _log.info("调用微信服务");
        VoTradeResult tradeResult = super.payRefund(wxPayService, request);
        _log.info("{}调用微信服务返回：{}", logPrefix, JSON.toJSONString(tradeResult));

        String errorMessage = "";
        _log.debug("查询对应的支付单 暂不支持部分退");
        PayOrder payOrder = payOrderService.selectPayOrder(refundOrder.getPayOrderId());
        if (payOrder == null) {
            errorMessage = "退款单号" + refundOrderId + "对应的支付订单不存在";
            _log.error(errorMessage);
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }
        if (!"1".equals(microPayConfig.getPay_back_flag())) {
            if (payOrder.getAmount().compareTo(refundOrder.getRefundAmount()) != 0) {
                errorMessage = "该微信订单不允许部分退";
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
        }

        if (tradeResult.getResultSuccess()) {
            WxPayRefundResult result = (WxPayRefundResult) tradeResult.getResultObject();
            if ("SUCCESS".equals(result.getReturnCode())) {
                if ("SUCCESS".equals(result.getResultCode())) {
                    Map<String, Object> retMap = new HashMap<>();

                    retMap.put("appid", result.getAppid());
                    retMap.put("mch_id", result.getMchId());
                    retMap.put("nonce_str", result.getNonceStr());
                    retMap.put("sign", result.getSign());
                    retMap.put("transaction_id", result.getTransactionId());

                    retMap.put("out_trade_no", result.getOutTradeNo());
                    retMap.put("out_refund_no", result.getOutRefundNo());
                    retMap.put("refund_id", result.getRefundId());
                    retMap.put("refund_fee", result.getRefundFee());
                    retMap.put("settlement_refund_fee", result.getSettlementRefundFee());

                    retMap.put("total_fee", result.getTotalFee());
                    retMap.put("settlement_total_fee", result.getSettlementTotalFee());
                    retMap.put("fee_type", result.getFeeType());
                    retMap.put("cash_fee", result.getCashFee());
                    retMap.put("cash_fee_type", result.getCashFeeType());

                    retMap.put("cash_refund_fee", result.getCashRefundFee());
                    retMap.put("coupon_refund_count", result.getCouponRefundCount());

                    //以下为自定义返回
                    retMap.put("refundOrderId", refundOrderId);
                    retMap.put("channelOrderNo", result.getRefundId());
                    retMap.put("payOrderId", result.getOutTradeNo());
                    return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
                } else {
                    _log.error("微信支付订单退款失败：业务失败");
                    String errCode = result.getErrCode();
                    String errCodeDes = result.getErrCodeDes();
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
                }
            } else {
                errorMessage = "微信支付订单退款失败：通信失败";
                String errCode = result.getReturnCode();
                String errCodeDes = result.getReturnMsg();
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
            }
        } else {
            String errCode = tradeResult.getResultCode();
            String errCodeDes = tradeResult.getResultMsg();
            errorMessage = "微信支付订单退款失败";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    @Override
    public String doWxCancelReq(String channelId, PayRefundOrder refundOrder) {
        String logPrefix = "【微信支付订单撤销】";
        ;

        String mchId = refundOrder.getMchId();
        String payOrderId = refundOrder.getPayOrderId();
        String refundOrderId = refundOrder.getRefundOrderId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}撤销单号：{}", logPrefix, refundOrderId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        WxPayService wxPayService = super.buildWxpayService(channelId, mchId, PayConstant.TRADE_TYPE_REFUND);
        WxPayOrderReverseRequest request = new WxPayOrderReverseRequest();
        request.setTransactionId(refundOrder.getChannelPayOrderNo());
        request.setOutTradeNo(refundOrder.getPayOrderId());

        _log.info("调用微信服务");
        VoTradeResult tradeResult = super.payOrderReverse(wxPayService, request);
        _log.info("{}调用微信服务返回：{}", logPrefix, JSON.toJSONString(tradeResult));

        String errorMessage = "";
        PayOrder payOrder = payOrderService.selectPayOrder(refundOrder.getPayOrderId());
        if (payOrder == null) {
            errorMessage = "撤销单号" + refundOrderId + "对应的支付订单不存在";
            _log.error(errorMessage);
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }

        if (tradeResult.getResultSuccess()) {
            WxPayOrderReverseResult result = (WxPayOrderReverseResult) tradeResult.getResultObject();
            if ("SUCCESS".equals(result.getReturnCode())) {
                if ("SUCCESS".equals(result.getResultCode())) {
                    Map<String, Object> retMap = new HashMap<>();

                    retMap.put("appid", result.getAppid());
                    retMap.put("mch_id", result.getMchId());
                    retMap.put("nonce_str", result.getNonceStr());
                    retMap.put("sign", result.getSign());
                    retMap.put("recall", result.getIsRecall());

                    //以下为自定义返回
                    retMap.put("refundOrderId", refundOrderId);
                    retMap.put("channelOrderNo", refundOrder.getChannelOrderNo());
                    retMap.put("payOrderId", refundOrder.getPayOrderId());
                    return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
                } else {
                    _log.error("微信支付订单撤销失败：业务失败");
                    String errCode = result.getErrCode();
                    String errCodeDes = result.getErrCodeDes();
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
                }
            } else {
                errorMessage = "微信支付订单撤销失败：通信失败";
                String errCode = result.getReturnCode();
                String errCodeDes = result.getReturnMsg();
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
            }
        } else {
            String errCode = tradeResult.getResultCode();
            String errCodeDes = tradeResult.getResultMsg();
            errorMessage = "微信支付订单撤销失败";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }
}
