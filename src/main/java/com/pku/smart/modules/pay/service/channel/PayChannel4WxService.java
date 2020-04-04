package com.pku.smart.modules.pay.service.channel;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.*;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.config.MicroPayConfig;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.service.IPayChannelService;
import com.pku.smart.modules.pay.vopackage.VoTradeResult;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class PayChannel4WxService {
    private final MyLog _log = MyLog.getLog(PayChannel4WxService.class);

    @Autowired
    MicroPayConfig microPayConfig;

    @Autowired
    IPayChannelService payChannelService;

    /**
     * 构建微信服务
     *
     * @param channelId
     * @param mchId
     * @return
     */
    public WxPayService buildWxpayService(String channelId, String mchId, String payType) {
        _log.info("============开始构建微信服务============");
        String tradeType = "";//目前仅支持扫码支付 JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付
        if (channelId.equals(PayConstant.PAY_CHANNEL_WX_MICROPAY)) {
            tradeType = "MICROPAY";//MICROPAY 刷卡支付
        } else if (channelId.equals(PayConstant.PAY_CHANNEL_WX_NATIVE)) {
            tradeType = "NATIVE";
        }
        _log.info("微信支付类型：{}，商户号：{}", tradeType, mchId);
        String notifyUrl = microPayConfig.getNotifyUrl();
        _log.info("域名地址：{}", notifyUrl);
        if (PayConstant.TRADE_TYPE_PAY.equalsIgnoreCase(payType)) {
            notifyUrl = String.format(notifyUrl, mchId, channelId, PayConstant.TRADE_TYPE_PAY);
            _log.info("支付");
        } else if (PayConstant.TRADE_TYPE_REFUND.equalsIgnoreCase(payType)) {
            notifyUrl = String.format(notifyUrl, mchId, channelId, PayConstant.TRADE_TYPE_REFUND);
            _log.info("退费");
        } else {
            notifyUrl = "";
            _log.warn("未知的类型：{}", payType);
        }
        _log.info("构建支付回调通知地址：{}", notifyUrl);

        PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        WxPayConfig wxPayConfig = MicroPayConfig.getWxPayConfig(payChannel.getParam(), tradeType, microPayConfig.getCertRootPath(), notifyUrl);
        _log.info("是否使用沙箱模拟：{}", wxPayConfig.isUseSandboxEnv());
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(wxPayConfig);
        if (wxPayConfig.isUseSandboxEnv()) {
            //try {
            //    String key = wxPayService.getSandboxSignKey();
            //    _log.info("沙箱密钥：{}", key);
            //} catch (WxPayException e) {
            //    e.printStackTrace();
            //}
            _log.warn("沙箱测试");
        }
        _log.info("============构建微信服务结束============");

        return wxPayService;
    }

    public VoTradeResult payMicropay(WxPayService wxPayService, WxPayMicropayRequest request) {
        _log.info("============微信条码支付开始============");

        WxPayMicropayResult micropayResult = new WxPayMicropayResult();
        String payOrderId = request.getOutTradeNo();
        String channelOrderNo = "";

        VoTradeResult result = new VoTradeResult();
        try {
            micropayResult = wxPayService.micropay(request);//系统自动判断了return_code 和 result_code如果失败直接抛的异常
            result = toTradeResult(micropayResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信条码支付失败");

            if ("Read timed out".equalsIgnoreCase(e.getCustomErrorMsg())) {
                _log.info("超时，查询一次交易结果，如果交易失败调用撤销");
                micropayResult = checkQueryAndCancel(wxPayService, payOrderId, channelOrderNo, micropayResult);
                return toTradeResult(micropayResult);
            }

            if (!"SUCCESS".equalsIgnoreCase(e.getReturnCode())) {
                _log.warn("明确失败，直接返回");
                return toTradeResult(e);
            }

            _log.error(e, "微信支付条码支付异常");
            String errCodes = e.getErrCode();
            _log.info("返回信息错误代码：{}", errCodes);
            if ("USERPAYING".equals(errCodes)) {
                _log.info("等待用户支付，轮询查交易结果，如果超时调用交易撤销");
                micropayResult = loopQueryAndCancel(wxPayService, payOrderId, channelOrderNo, micropayResult);
            } else if ("SYSTEMERROR".equals(errCodes) || "BANKERROR".equals(errCodes)) {
                _log.info("系统错误，查询一次交易结果，如果交易失败调用撤销");
                micropayResult = checkQueryAndCancel(wxPayService, payOrderId, channelOrderNo, micropayResult);
            } else {
                _log.info("其他错误，明确失败，直接返回");
                return toTradeResult(e);
            }

            result = toTradeResult(micropayResult);
        }
        _log.info("============微信条码支付结束============");

        return result;
    }

    public VoTradeResult payUnifiedOrder(WxPayService wxPayService, WxPayUnifiedOrderRequest request) {
        _log.info("============微信扫码支付开始============");

        WxPayUnifiedOrderResult payUnifiedOrderResult = new WxPayUnifiedOrderResult();

        VoTradeResult result = new VoTradeResult();
        try {
            payUnifiedOrderResult = wxPayService.unifiedOrder(request);
            result = toTradeResult(payUnifiedOrderResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信扫码支付失败");
            result = toTradeResult(e);
        }
        _log.info("============微信扫码支付结束============");

        return result;
    }

    public VoTradeResult payOrderQuery(WxPayService wxPayService, WxPayOrderQueryRequest request) {
        _log.info("============微信订单查询开始============");

        WxPayOrderQueryResult payOrderQueryResult = new WxPayOrderQueryResult();

        VoTradeResult result = new VoTradeResult();
        try {
            payOrderQueryResult = wxPayService.queryOrder(request);
            result = toTradeResult(payOrderQueryResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信订单查询失败");
            result = toTradeResult(e);
        }
        _log.info("============微信订单查询结束============");

        return result;
    }

    public VoTradeResult payRefund(WxPayService wxPayService, WxPayRefundRequest request) {
        _log.info("============微信订单退费开始============");

        WxPayRefundResult payRefundResult = new WxPayRefundResult();

        VoTradeResult result = new VoTradeResult();
        try {
            payRefundResult = wxPayService.refund(request);
            result = toTradeResult(payRefundResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信订单退费失败");
            result = toTradeResult(e);
        }
        _log.info("============微信订单退费结束============");

        return result;
    }

    public VoTradeResult payOrderReverse(WxPayService wxPayService, WxPayOrderReverseRequest request) {
        _log.info("============微信订单撤销开始============");

        WxPayOrderReverseResult payOrderReverseResult = new WxPayOrderReverseResult();

        VoTradeResult result = new VoTradeResult();
        try {
            payOrderReverseResult = wxPayService.reverseOrder(request);
            result = toTradeResult(payOrderReverseResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信订单撤销失败");
            result = toTradeResult(e);
        }
        _log.info("============微信订单撤销结束============");

        return result;
    }

    public VoTradeResult payBill(WxPayService wxPayService, WxPayDownloadBillRequest request) {
        _log.info("============微信对账单下载开始============");

        WxPayBillResult payBillResult = new WxPayBillResult();

        VoTradeResult result = new VoTradeResult();

        try {
            payBillResult = wxPayService.downloadBill(request);
            result = toTradeResult(payBillResult);
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信对账单下载失败");
            result = toTradeResult(e);
        }
        _log.info("============微信对账单下载结束============");

        return result;
    }

    private WxPayMicropayResult checkQueryAndCancel(WxPayService wxPayService, String payOrderId, String channelOrderNo, WxPayMicropayResult micropayResult) {
        _log.info("查询微信订单{}是否成功", payOrderId);
        WxPayOrderQueryRequest queryRequest = new WxPayOrderQueryRequest();
        queryRequest.setOutTradeNo(payOrderId);
        queryRequest.setTransactionId(channelOrderNo);
        WxPayOrderQueryResult queryResult = null;
        try {
            queryResult = wxPayService.queryOrder(queryRequest);
            if ("SUCCESS".equals(queryResult.getReturnCode())) {
                if ("SUCCESS".equals(queryResult.getResultCode())) {
                    String tradeStatus = queryResult.getTradeState();
                    if (tradeStatus.equalsIgnoreCase("SUCCESS")) {
                        _log.info("支付成功,将查询转换为支付");
                        return toMicropayResult(queryResult, micropayResult);
                    }
                }
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信异常,准备撤销交易");
        }

        _log.info("调用微信撤销");
        WxPayOrderReverseRequest reverseRequest = new WxPayOrderReverseRequest();
        reverseRequest.setTransactionId(channelOrderNo);
        reverseRequest.setOutTradeNo(payOrderId);
        try {
            WxPayOrderReverseResult reverseResult = wxPayService.reverseOrder(reverseRequest);
            if ("SUCCESS".equalsIgnoreCase(reverseResult.getReturnCode())) {
                if ("SUCCESS".equalsIgnoreCase(reverseResult.getResultCode())) {
                    _log.info("调用微信撤销成功");
                }
                String recall = reverseResult.getIsRecall();
                _log.info("是否需要再次撤销：{}", recall);
                if ("Y".equals(recall)) {
                    _log.info("再次撤销，如果仍然失败再试一次");
                    reverseResult = wxPayService.reverseOrder(reverseRequest);
                    recall = reverseResult.getIsRecall();
                }
                if ("Y".equals(recall)) {
                    _log.info("第2次尝试撤销，如果仍然失败不处理，等待对账");
                    wxPayService.reverseOrder(reverseRequest);
                }
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "调用微信撤销失败：{}");
        }
        _log.info("调用微信撤销结束");

        micropayResult.setReturnCode("FAIL");
        micropayResult.setReturnMsg("失败");
        return micropayResult;
    }

    private WxPayMicropayResult loopQueryAndCancel(WxPayService wxPayService, String payOrderId, String channelOrderNo, WxPayMicropayResult micropayResult) {
        _log.info("轮询查询微信订单{}是否成功", payOrderId);
        WxPayOrderQueryRequest queryRequest = new WxPayOrderQueryRequest();
        queryRequest.setOutTradeNo(payOrderId);
        queryRequest.setTransactionId(channelOrderNo);
        WxPayOrderQueryResult queryResult = null;
        try {
            queryResult = wxPayService.queryOrder(queryRequest);
            if ("SUCCESS".equals(queryResult.getReturnCode())) {
                if ("SUCCESS".equals(queryResult.getResultCode())) {
                    String tradeStatus = queryResult.getTradeState();
                    _log.info("订单状态：{}", tradeStatus);

                    if (tradeStatus.equalsIgnoreCase("USERPAYING")) {
                        _log.info("等待买家付款，延迟5秒后再次查询");
                        TimeUnit.SECONDS.sleep(5);//秒
                        queryResult = wxPayService.queryOrder(queryRequest);
                        tradeStatus = queryResult.getTradeState();
                        _log.info("订单状态：{}", tradeStatus);
                    }

                    if (tradeStatus.equalsIgnoreCase("USERPAYING")) {
                        _log.info("等待买家付款，延迟10秒后再次查询");
                        TimeUnit.SECONDS.sleep(10);//秒
                        queryResult = wxPayService.queryOrder(queryRequest);
                        tradeStatus = queryResult.getTradeState();
                        _log.info("订单状态：{}", tradeStatus);
                    }

                    if (tradeStatus.equalsIgnoreCase("USERPAYING")) {
                        _log.info("等待买家付款，延迟15秒后再次查询");
                        TimeUnit.SECONDS.sleep(15);//秒
                        queryResult = wxPayService.queryOrder(queryRequest);
                        tradeStatus = queryResult.getTradeState();
                        _log.info("订单状态：{}", tradeStatus);
                    }

                    if (tradeStatus.equalsIgnoreCase("SUCCESS")) {
                        _log.info("支付成功,将查询转换为支付");
                        return toMicropayResult(queryResult, micropayResult);
                    }
                }
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error(e, "微信异常,准备撤销交易");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            _log.error(ex, "系统异常,准备撤销交易");
        }

        _log.info("调用微信撤销");
        WxPayOrderReverseRequest reverseRequest = new WxPayOrderReverseRequest();
        reverseRequest.setTransactionId(channelOrderNo);
        reverseRequest.setOutTradeNo(payOrderId);
        try {
            WxPayOrderReverseResult reverseResult = wxPayService.reverseOrder(reverseRequest);
            if ("SUCCESS".equalsIgnoreCase(reverseResult.getReturnCode())) {
                if ("SUCCESS".equalsIgnoreCase(reverseResult.getResultCode())) {
                    _log.info("微信撤销成功");
                }
                String recall = reverseResult.getIsRecall();
                _log.info("是否需要再次撤销：{}", recall);
                if ("Y".equals(recall)) {
                    _log.info("再次撤销，如果仍然失败再试一次");
                    reverseResult = wxPayService.reverseOrder(reverseRequest);
                    recall = reverseResult.getIsRecall();
                }
                if ("Y".equals(recall)) {
                    _log.info("第2次尝试撤销，如果仍然失败不处理，等待对账");
                    wxPayService.reverseOrder(reverseRequest);
                }
            }
        } catch (WxPayException e) {
            e.printStackTrace();
            _log.error("调用微信撤销失败：{}", e.getMessage());
        }
        _log.info("调用微信撤销结束");

        micropayResult.setReturnCode("FAIL");
        micropayResult.setReturnMsg("失败");
        return micropayResult;
    }

    private WxPayMicropayResult toMicropayResult(WxPayOrderQueryResult queryResult, WxPayMicropayResult micropayResult) {
        _log.info("将查询转为支付返回");
        micropayResult.setReturnCode(queryResult.getReturnCode());
        micropayResult.setReturnMsg(queryResult.getReturnMsg());
        micropayResult.setResultCode(queryResult.getResultCode());
        micropayResult.setErrCode(queryResult.getErrCode());
        micropayResult.setOutTradeNo(queryResult.getOutTradeNo());
        micropayResult.setTransactionId(queryResult.getTransactionId());
        micropayResult.setOpenid(queryResult.getOpenid());
        micropayResult.setIsSubscribe(queryResult.getIsSubscribe());
        micropayResult.setTradeType(queryResult.getTradeType());
        micropayResult.setBankType(queryResult.getBankType());
        micropayResult.setFeeType(queryResult.getFeeType());
        micropayResult.setTotalFee(String.valueOf(queryResult.getTotalFee()));
        micropayResult.setSettlementTotalFee(queryResult.getSettlementTotalFee());
        micropayResult.setCouponFee(queryResult.getCouponFee());
        micropayResult.setCashFeeType(queryResult.getCashFeeType());
        micropayResult.setCashFee(queryResult.getCashFee());
        micropayResult.setAttach(queryResult.getAttach());
        micropayResult.setTimeEnd(queryResult.getTimeEnd());
        micropayResult.setPromotionDetail(queryResult.getPromotionDetail());
        _log.info("转换完成：{}", JSON.toJSONString(micropayResult));
        return micropayResult;
    }

    private VoTradeResult toTradeResult(BaseWxPayResult payResult) {
        VoTradeResult result = new VoTradeResult();
        if ("SUCCESS".equalsIgnoreCase(payResult.getReturnCode()) && "SUCCESS".equalsIgnoreCase(payResult.getResultCode())) {
            result.setResultSuccess(true);
            result.setResultObject(payResult);
            return result;
        } else {
            result.setResultSuccess(false);
            result.setResultCode(payResult.getErrCode());
            result.setResultMsg(payResult.getErrCodeDes());
            result.setResultObject(payResult);
            return result;
        }
    }

    private VoTradeResult toTradeResult(WxPayBillResult billResult) {
        VoTradeResult result = new VoTradeResult();
        result.setResultSuccess(true);
        result.setResultObject(billResult);
        return result;
    }

    private VoTradeResult toTradeResult(WxPayException e) {
        VoTradeResult result = new VoTradeResult();
        result.setResultSuccess(false);
        result.setResultCode(e.getReturnCode());
        result.setResultMsg(StringUtils.isBlank(e.getReturnMsg()) ? "通信失败" : e.getReturnMsg());
        result.setResultObject(e);
        return result;
    }
}
