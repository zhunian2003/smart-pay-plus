package com.pku.smart.modules.pay.service.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.*;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.entity.PayRefundOrder;
import com.pku.smart.modules.pay.service.IPayChannel4AliService;
import com.pku.smart.modules.pay.service.IPayOrderService;
import com.pku.smart.modules.pay.service.IRefundOrderService;
import com.pku.smart.modules.pay.vopackage.VoTradeResult;
import com.pku.smart.utils.AmountUtil;
import com.pku.smart.utils.PayUtils;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayChannel4AliServiceImpl extends PayChannel4AliService implements IPayChannel4AliService {

    private final MyLog _log = MyLog.getLog(PayChannel4AliServiceImpl.class);

    @Autowired
    IRefundOrderService refundOrderService;

    @Autowired
    IPayOrderService payOrderService;

    /**
     * 条码支付 直接扣款
     *
     * @param channelId
     * @param payOrder
     * @return
     */
    @Override
    public String doAliPayReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【支付宝当面付之条码支付】";

        String mchOrderNo = payOrder.getMchOrderNo();
        String payOrderId = payOrder.getPayOrderId();
        String mchId = payOrder.getMchId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        String errorMessage = "";
        _log.info("查询支付订单{}是否存在", payOrderId);
        PayOrder order = payOrderService.selectPayOrder(payOrderId);
        if (order == null) {
            _log.error("支付订单{}不存在", payOrderId);
            errorMessage = "支付订单" + payOrderId + "不存在";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }

        _log.info("订单存在，更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_PAYING);
        int count = payOrderService.updateStatus4Ing(payOrderId);
        if (count != 1) {
            errorMessage = "更新订单状态失败";
            _log.error(errorMessage);
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }

        AlipayClient client = super.buildAlipayClient(channelId, mchId);
        AlipayTradePayRequest alipay_request = new AlipayTradePayRequest();
        AlipayTradePayModel model = new AlipayTradePayModel();
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(PayConstant.PAY_CHANNEL_ALIPAY_PROVIDER_ID);
        model.setOutTradeNo(payOrder.getPayOrderId());
        model.setScene(payOrder.getScene());//条码支付，取值：bar_code 声波支付，取值：wave_code
        model.setAuthCode(payOrder.getAuthCode());//授权码
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        model.setTerminalId(payOrder.getDevice());
        model.setOperatorId(payOrder.getMchId());//商户操作员编号 这里传支付平台商户号
        model.setExtendParams(extendParams);
        alipay_request.setBizModel(model);

        _log.info("{}开始调用支付宝请求", logPrefix);
        VoTradeResult result = super.tradePay(client, model);
        _log.info("{}调用支付宝请求返回：{}", logPrefix, JSON.toJSONString(result));

        if (result.getResultSuccess()){
            _log.debug("构建成功返回参数");
            Map<String, Object> retMap = new HashMap<>();

            AlipayTradePayResponse response = (AlipayTradePayResponse) result.getResultObject();
            retMap.put("trade_no", response.getTradeNo());
            retMap.put("out_trade_no", response.getOutTradeNo());
            retMap.put("buyer_logon_id", response.getBuyerLogonId());
            retMap.put("pay_amount", response.getPayAmount());
            retMap.put("total_amount", response.getTotalAmount());

            retMap.put("payOrderId", payOrderId);
            retMap.put("mchOrderNo", mchOrderNo);
            retMap.put("prepayId", response.getTradeNo());

            String channelOrderNo = response.getTradeNo();
            _log.info("更新支付宝订单号：{}", channelOrderNo);
            count = payOrderService.updateChannelOrderNo(payOrderId, channelOrderNo);
            if (count != 1) {
                errorMessage = "更新支付宝订单号失败";
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }

            _log.info("更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_SUCCESS);
            count = payOrderService.updateStatus4Success(payOrderId);
            if (count != 1) {
                errorMessage = "更新订单状态失败,已支付订单将在3个工作日内退款";
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }

            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } else {
            errorMessage = "支付宝当面付之条码支付";
            _log.error("###### 支付宝当面付之条码支付处理失败 ######");
            String errCode = result.getResultCode();
            String errCodeDes = result.getResultMsg();
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    /**
     * 扫码支付 预下单
     *
     * @param channelId
     * @param payOrder
     * @return
     */
    @Override
    public String doAliPrePayReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【支付宝当面付之扫码支付下单】";

        String mchOrderNo = payOrder.getMchOrderNo();
        String payOrderId = payOrder.getPayOrderId();
        String mchId = payOrder.getMchId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        AlipayClient client = super.buildAlipayClient(channelId, mchId);
        _log.info("封装请求支付信息");
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(PayConstant.PAY_CHANNEL_ALIPAY_PROVIDER_ID);
        model.setOutTradeNo(payOrderId);
        model.setSubject(payOrder.getSubject());
        model.setTotalAmount(AmountUtil.convertCent2Dollar(payOrder.getAmount().toString()));
        model.setBody(payOrder.getBody());
        model.setTerminalId(payOrder.getDevice());
        model.setOperatorId(payOrder.getMchId());//商户操作员编号 这里传支付平台商户号
        model.setExtendParams(extendParams);
        _log.info("获取objParams参数");
        String objParams = payOrder.getExtra();
        String errorMessage = "";
        if (StringUtils.isNotEmpty(objParams)) {
            try {
                JSONObject objParamsJson = JSON.parseObject(objParams);
                if (StringUtils.isNotBlank(objParamsJson.getString("discountable_amount"))) {
                    _log.info("可打折金额");
                    model.setDiscountableAmount(objParamsJson.getString("discountable_amount"));
                }
                if (StringUtils.isNotBlank(objParamsJson.getString("undiscountable_amount"))) {
                    _log.info("不可打折金额");
                    model.setUndiscountableAmount(objParamsJson.getString("undiscountable_amount"));
                }
            } catch (Exception e) {
                errorMessage = "附加参数格式错误";
                _log.error("{}objParams参数格式错误！", logPrefix);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
        }

        _log.info("开始调用支付宝请求");
        VoTradeResult result = super.tradePrecreate(client, model);
        _log.info("{}调用支付宝请求返回：{}", logPrefix, JSON.toJSONString(result));

        if (result.getResultSuccess()){
            AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) result.getResultObject();

            _log.debug("构建成功返回参数");
            Map<String, Object> retMap = new HashMap<>();
            retMap.put("payOrderId", payOrderId);
            retMap.put("mchOrderNo", mchOrderNo);
            retMap.put("prepayId", response.getOutTradeNo());
            retMap.put("codeUrl", response.getQrCode());
            String payUrl = response.getBody();
            _log.info("{}生成跳转路径：payUrl={}", logPrefix, payUrl);

            _log.debug("更新订单状态");
            int count = payOrderService.updateStatus4Ing(payOrderId);
            if (count != 1) {
                errorMessage = "更新订单状态失败";
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } else {
            errorMessage = "支付宝当面付之扫码支付";
            _log.error("###### 支付宝当面付之扫码支付处理失败 ######");
            String errCode = result.getResultCode();
            String errCodeDes = result.getResultMsg();
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    /**
     * 支付宝订单查询
     *
     * @param channelId
     * @param payOrder
     * @return
     */
    @Override
    public String doAliQueryReq(String channelId, PayOrder payOrder) {
        String logPrefix = "【支付宝订单查询】";

        String mchOrderNo = payOrder.getMchOrderNo();
        String payOrderId = payOrder.getPayOrderId();
        String mchId = payOrder.getMchId();
        String channelOrderNo = payOrder.getChannelOrderNo();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}支付单号：{}", logPrefix, payOrderId);

        AlipayClient client = super.buildAlipayClient(channelId, mchId);
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(payOrderId);//商户订单号
        model.setTradeNo(channelOrderNo);//支付宝订单号

        String errorMessage = "";
        PayOrder order = payOrderService.selectPayOrder(payOrderId);
        if (order == null) {
            _log.error("支付订单{}不存在", payOrderId);
            errorMessage = "支付订单" + payOrderId + "不存在";
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }

        _log.info("开始调用支付宝请求");
        VoTradeResult result = super.tradeQuery(client, model);
        _log.info("{}调用支付宝请求返回：{}", logPrefix, JSON.toJSONString(result));

        if (result.getResultSuccess()){
            AlipayTradeQueryResponse response = (AlipayTradeQueryResponse) result.getResultObject();

            Map<String, Object> retMap = new HashMap<>();
            retMap.put("mchOrderNo", mchOrderNo);
            retMap.put("payOrderId", payOrderId);
            retMap.put("clientIp", "");
            retMap.put("device", "");
            retMap.put("buyerLogonId", response.getBuyerLogonId());//买家支付宝账号

            retMap.put("tradeStatus", response.getTradeStatus());//交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
            retMap.put("totalAmount", response.getTotalAmount());
            retMap.put("receiptAmount", response.getReceiptAmount());
            retMap.put("buyerPayAmount", response.getBuyerPayAmount());
            retMap.put("pointAmount", response.getPointAmount());

            retMap.put("invoiceAmount", response.getInvoiceAmount());
            retMap.put("sendPayDate", response.getSendPayDate());
            retMap.put("storeId", response.getStoreId());
            retMap.put("terminalId", response.getTerminalId());
            retMap.put("storeName", response.getStoreName());

            retMap.put("buyerUserId", response.getBuyerUserId());
            retMap.put("fundBillList", response.getFundBillList());
            retMap.put("currency", "cny");

            String tradeStatus = response.getTradeStatus();
            _log.info("订单状态：{}", tradeStatus);

            if (StringUtils.isBlank(channelOrderNo) && tradeStatus.equalsIgnoreCase("TRADE_SUCCESS")) {
                channelOrderNo = response.getTradeNo();
                _log.info("支付宝订单号：{}", channelOrderNo);
                int count = payOrderService.updateChannelOrderNo(payOrderId, channelOrderNo);
                if (count != 1) {
                    errorMessage = "更新支付宝订单号失败";
                    _log.error(errorMessage);
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                }
            }

            if (tradeStatus.equalsIgnoreCase("TRADE_SUCCESS")) {
                _log.info("交易支付成功");
                if (Integer.valueOf(order.getStatus()) < PayConstant.PAY_STATUS_SUCCESS && Integer.valueOf(order.getStatus()) >= PayConstant.PAY_STATUS_INIT) {
                    _log.info("更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_SUCCESS);
                    int count = payOrderService.updateStatus4Success(payOrderId);
                    if (count != 1) {
                        errorMessage = "更新订单状态失败";
                        _log.error(errorMessage);
                        return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                    }
                }
            } else if (tradeStatus.equalsIgnoreCase("TRADE_FINISHED")) {
                _log.info("交易结束，不可退款");
            } else if (tradeStatus.equalsIgnoreCase("TRADE_CLOSED")) {
                _log.info("未付款交易超时关闭，或支付完成后全额退款");
                if (Integer.valueOf(order.getStatus()) < PayConstant.PAY_STATUS_SUCCESS && Integer.valueOf(order.getStatus()) >= PayConstant.PAY_STATUS_INIT) {
                    _log.info("更新本地订单状态：{}为：{}", order.getStatus(), PayConstant.PAY_STATUS_EXPIRED);
                    int count = payOrderService.updateStatus4Closed(payOrderId);
                    if (count != 1) {
                        errorMessage = "更新订单状态失败";
                        _log.error(errorMessage);
                        return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
                    }
                }
            }

            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } else {
            errorMessage = "支付宝订单查询处理失败";
            _log.error("###### 支付宝订单查询处理失败 ######");
            String errCode = result.getResultCode();
            String errCodeDes = result.getResultMsg();
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    /**
     * 支付宝退款
     *
     * @param channelId
     * @param refundOrder
     * @return
     */
    @Override
    public String doAliRefundReq(String channelId, PayRefundOrder refundOrder) {
        String logPrefix = "【支付宝退款】";

        String refundOrderId = refundOrder.getRefundOrderId();
        String mchId = refundOrder.getMchId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}退款单号：{}", logPrefix, refundOrderId);

        AlipayClient client = super.buildAlipayClient(channelId, mchId);
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(refundOrder.getPayOrderId());
        model.setTradeNo(refundOrder.getChannelPayOrderNo());
        model.setOutRequestNo(refundOrderId);
        model.setRefundAmount(AmountUtil.convertCent2Dollar(refundOrder.getRefundAmount().toString()));
        model.setRefundReason("正常退款");
        model.setTerminalId(refundOrder.getDevice());
        model.setOperatorId(refundOrder.getMchId());//商户操作员编号 这里传支付平台商户号

        String errorMessage = "";
        _log.debug("查询对应的支付单 暂不支持部分退");
        PayOrder payOrder = payOrderService.selectPayOrder(refundOrder.getPayOrderId());
        if (payOrder == null) {
            errorMessage = "退款单号" + refundOrderId + "对应的支付订单不存在";
            _log.error(errorMessage);
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }
        if (!"1".equals(aliPayConfig.getPay_back_flag())) {
            if (payOrder.getAmount().compareTo(refundOrder.getRefundAmount()) != 0) {
                errorMessage = "该支付宝订单不允许部分退";
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
        }

        _log.info("开始调用支付宝请求");
        VoTradeResult result = super.tradeRefund(client, model);
        _log.info("{}调用支付宝请求返回：{}", logPrefix, JSON.toJSONString(result));

        if (result.getResultSuccess()){
            AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) result.getResultObject();

            Map<String, Object> retMap = new HashMap<>();
            retMap.put("refundOrderId", refundOrderId);
            retMap.put("isSuccess", true);
            retMap.put("channelOrderNo", response.getTradeNo());
            retMap.put("payOrderId", response.getOutTradeNo());

            _log.info("【支付宝退款】response.getCode():" + response.getCode());
            _log.info("【支付宝退款】response.getMsg():" + response.getMsg());
            _log.info("【支付宝退款】response.getSubCode():" + response.getSubCode());
            _log.info("【支付宝退款】response.getSubMsg():" + response.getSubMsg());
            _log.info("【支付宝退款】response.isSuccess():" + response.isSuccess());
            _log.info("【支付宝退款】response.getOutTradeNo():" + response.getOutTradeNo());
            _log.info("【支付宝退款】response.getTradeNo():" + response.getTradeNo());

            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } else {
            errorMessage = "支付宝当面付之支付宝退款失败";
            _log.error("###### 支付宝当面付之支付宝退款处理失败 ######");
            String errCode = result.getResultCode();
            String errCodeDes = result.getResultMsg();
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

    @Override
    public String doAliCancelReq(String channelId, PayRefundOrder refundOrder) {
        String logPrefix = "【支付宝撤销】";

        String refundOrderId = refundOrder.getRefundOrderId();
        String mchId = refundOrder.getMchId();
        _log.info("{}商户号：{}", logPrefix, mchId);
        _log.info("{}渠道ID：{}", logPrefix, channelId);
        _log.info("{}撤销单号：{}", logPrefix, refundOrderId);

        AlipayClient client = super.buildAlipayClient(channelId, mchId);
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo(refundOrder.getPayOrderId());
        model.setTradeNo(refundOrder.getChannelPayOrderNo());

        String errorMessage = "";

        PayOrder payOrder = payOrderService.selectPayOrder(refundOrder.getPayOrderId());
        if (payOrder == null) {
            errorMessage = "撤销单号" + refundOrderId + "对应的支付订单不存在";
            _log.error(errorMessage);
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }

        _log.info("开始调用支付宝请求");
        VoTradeResult result = super.tradeCancel(client, model);
        _log.info("{}调用支付宝请求返回：{}", logPrefix, JSON.toJSONString(result));

        if (result.getResultSuccess()){
            AlipayTradeCancelResponse response = (AlipayTradeCancelResponse) result.getResultObject();

            Map<String, Object> retMap = new HashMap<>();
            retMap.put("refundOrderId", refundOrderId);
            retMap.put("isSuccess", true);
            retMap.put("channelOrderNo", response.getTradeNo());
            retMap.put("payOrderId", response.getOutTradeNo());

            _log.info("【支付宝撤销】response.getCode():" + response.getCode());
            _log.info("【支付宝撤销】response.getMsg():" + response.getMsg());
            _log.info("【支付宝撤销】response.getSubCode():" + response.getSubCode());
            _log.info("【支付宝撤销】response.getSubMsg():" + response.getSubMsg());
            _log.info("【支付宝撤销】response.isSuccess():" + response.isSuccess());
            _log.info("【支付宝撤销】response.getOutTradeNo():" + response.getOutTradeNo());
            _log.info("【支付宝撤销】response.getTradeNo():" + response.getTradeNo());

            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } else {
            errorMessage = "支付宝当面付之支付宝撤销失败";
            _log.error("###### 支付宝当面付之支付宝撤销处理失败 ######");
            String errCode = result.getResultCode();
            String errCodeDes = result.getResultMsg();
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_SUCCESS, errorMessage, PayConstant.RETURN_VALUE_FAIL, errCode, errCodeDes);
        }
    }

}
