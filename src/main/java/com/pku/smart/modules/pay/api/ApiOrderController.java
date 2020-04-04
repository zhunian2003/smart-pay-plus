package com.pku.smart.modules.pay.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.service.*;
import com.pku.smart.utils.MySeqUtils;
import com.pku.smart.utils.PayUtils;
import com.pku.smart.utils.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/api")
public class ApiOrderController {

    private final MyLog _log = MyLog.getLog(ApiOrderController.class);

    @Autowired
    IMchInfoService mchInfoService;

    @Autowired
    IPayOrderService payOrderService;

    @Autowired
    IPayChannelService payChannelService;

    @Autowired
    IPayChannel4WxService payChannel4WxService;

    @Autowired
    IPayChannel4AliService payChannel4AliService;

    /**
     * 条码支付
     * @param params
     * @return
     */
    @RequestMapping(value = "/pay/pay_order")
    public String payOrder(@RequestParam String params) {
        _log.info("###### 开始接收商户统一支付请求 ######");

        String logPrefix = "【商户统一支付】";
        String errorMessage = "";
        try {
            JSONObject po = JSONObject.parseObject(params);
            JSONObject payContext = new JSONObject();

            _log.info("{}第一步：验证参数有效性。", logPrefix);
            Object object = validateParams(po, payContext);
            if (object instanceof String) {
                errorMessage = "参数校验不通过:" + object;
                _log.error("{}参数校验不通过:{}", logPrefix, object);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }
            PayOrder payOrder = (PayOrder) object;

            _log.info("{}第二步：创建支付订单。", logPrefix);
            int result = payOrderService.createPayOrder(payOrder);
            if (result != 1){
                errorMessage = "创建支付订单失败";
                _log.error("{}创建支付订单失败", logPrefix);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }

            _log.info("{}第三步：根据渠道ID生成订单二维码信息", logPrefix);
            String channelId = payOrder.getChannelId();
            switch (channelId) {
                case PayConstant.PAY_CHANNEL_WX_MICROPAY:
                    return payChannel4WxService.doWxPayReq(channelId, payOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_BR:
                    return payChannel4AliService.doAliPayReq(channelId, payOrder);
                default:
                    errorMessage = "不支持的支付渠道类型[channelId=" + channelId + "]";
                    _log.error(errorMessage);
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "统一支付异常:" + e.getMessage();
            _log.error("统一支付异常：{}", e.getMessage());
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
        }
    }

    /**
     * 扫码支付 预下单
     * @param params
     * @return
     */
    @RequestMapping(value = "/pay/create_order")
    public String createOrder(@RequestParam String params) {
        _log.info("###### 开始接收商户统一下单请求 ######");

        String logPrefix = "【商户统一下单】";
        String errorMessage = "";
        try {
            JSONObject po = JSONObject.parseObject(params);
            JSONObject payContext = new JSONObject();

            _log.info("第一步：验证参数有效性。", logPrefix);
            Object object = validateParams(po, payContext);
            if (object instanceof String) {
                errorMessage = "参数校验不通过:" + object;
                _log.error("{}参数校验不通过:{}", logPrefix, object);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }
            PayOrder payOrder = (PayOrder) object;

            _log.info("{}第二步：创建支付订单。", logPrefix);
            int result = payOrderService.createPayOrder(payOrder);
            if (result != 1){
                errorMessage = "创建支付订单失败";
                _log.error("{}创建支付订单失败", logPrefix);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }

            _log.info("{}第三步：根据渠道ID条码支付", logPrefix);
            String channelId = payOrder.getChannelId();
            switch (channelId) {
                case PayConstant.PAY_CHANNEL_WX_NATIVE:
                    return payChannel4WxService.doWxPrePayReq(channelId, payOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                    return payChannel4AliService.doAliPrePayReq(channelId, payOrder);
                default:
                    errorMessage = "不支持的支付渠道类型[channelId=" + channelId + "]";
                    _log.error(errorMessage);
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "统一下单异常:" + e.getMessage();
            _log.error("统一下单异常：{}", e.getMessage());
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
        }
    }

    private Object validateParams(JSONObject params, JSONObject payContext) {
        String errorMessage;

        // 支付参数
        String mchId = params.getString("mchId");                // 商户ID
        String mchOrderNo = params.getString("mchOrderNo");      // 商户订单号
        String payOrderType = params.getString("payOrderType");  //支付订单号是否采用商户订单号 1、是 0、否
        String channelId = params.getString("channelId");        // 渠道ID
        String amount = params.getString("amount");              // 支付金额（单位分）
        String currency = params.getString("currency");          // 币种
        String clientIp = params.getString("clientIp");          // 客户端IP
        String device = params.getString("device");              // 设备
        String extra = params.getString("extra");                // 特定渠道发起时额外参数
        String param1 = params.getString("param1");              // 扩展参数1
        String param2 = params.getString("param2");              // 扩展参数2
        String notifyUrl = params.getString("notifyUrl");        // 支付结果回调URL
        String sign = params.getString("sign");                  // 签名
        String subject = params.getString("subject");            // 商品主题
        String body = params.getString("body");                  // 商品描述信息
        String scene = params.getString("scene");                //支付场景
        String authCode = params.getString("authCode");          //授权码 仅条码支付用

        // 验证请求参数有效性（必选项）
        if (StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(mchOrderNo)) {
            errorMessage = "request params[mchOrderNo] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(channelId)) {
            errorMessage = "request params[channelId] error.";
            return errorMessage;
        }
        if (!NumberUtils.isCreatable(amount)) {//判断是否数字
            errorMessage = "request params[amount] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(currency)) {
            errorMessage = "request params[currency] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(notifyUrl)) {
            errorMessage = "request params[notifyUrl] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(subject)) {
            errorMessage = "request params[subject] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(body)) {
            errorMessage = "request params[body] error.";
            return errorMessage;
        }
        // 根据不同渠道,判断extra参数
        if (PayConstant.PAY_CHANNEL_WX_NATIVE.equalsIgnoreCase(channelId)) {
            if (StringUtils.isEmpty(extra)) {
                errorMessage = "request params[extra] error.";
                return errorMessage;
            }
            JSONObject extraObject = JSON.parseObject(extra);
            String productId = extraObject.getString("productId");
            if (StringUtils.isBlank(productId)) {
                errorMessage = "request params[extra.productId] error.";
                return errorMessage;
            }
        }

        if (PayConstant.PAY_CHANNEL_ALIPAY_BR.equalsIgnoreCase(channelId)){
            _log.info("支付宝条码支付需要判断支付场景");
            if (StringUtils.isBlank(scene)){
                errorMessage = "request params[scene] error.";
                return errorMessage;
            }
        }

        if (PayConstant.PAY_CHANNEL_WX_MICROPAY.equalsIgnoreCase(channelId) || PayConstant.PAY_CHANNEL_ALIPAY_BR.equalsIgnoreCase(channelId)){
            _log.info("条码支付授权码判断");
            if (StringUtils.isBlank(authCode)){
                errorMessage = "request params[authCode] error.";
                return errorMessage;
            }
        }

        // 签名信息
        if (StringUtils.isEmpty(sign)) {
            errorMessage = "request params[sign] error.";
            return errorMessage;
        }

        // 查询商户信息
        PayMchInfo mchInfo = mchInfoService.selectMchInfo(mchId);
        if (mchInfo == null) {
            errorMessage = "Can't found mchInfo[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (!"0".equalsIgnoreCase(mchInfo.getStatus())) {
            errorMessage = "mchInfo not available [mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        String reqKey = mchInfo.getReqKey();
        if (StringUtils.isBlank(reqKey)) {
            errorMessage = "reqKey is null[mchId=" + mchId + "] record in db.";
            return errorMessage;
        }

        PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        if (payChannel == null) {
            errorMessage = "Can't found payChannel[channelId=" + channelId + ",mchId=" + mchId + "] record in db.";
            return errorMessage;
        }
        if (!"0".equalsIgnoreCase(payChannel.getStatus())) {
            errorMessage = "channel not available [channelId=" + channelId + ",mchId=" + mchId + "]";
            return errorMessage;
        }

        String channelMchId = payChannel.getChannelMchId();
        if (StringUtils.isBlank(channelMchId)) {
            errorMessage = "request params[channelMchId] error.";
            return errorMessage;
        }

        // 验证签名数据
        boolean verifyFlag = PayUtils.verifyPaySign(params, reqKey);
        if (!verifyFlag) {
            errorMessage = "验证签名数据失败.";
            return errorMessage;
        }

        if (StringUtils.isEmpty(payOrderType)) {
            payOrderType = "1";
            param2 = param2 + "|支付订单号为空，取默认值1。";
        }

        String payOrderId = "1".equals(payOrderType) ? mchOrderNo : MySeqUtils.getPay();
        if ("1".equals(payOrderType)) {
            param2 = param2 + "|支付订单号采用商户订单号。";
        }

        PayOrder payOrder = new PayOrder();
        payOrder.setMchId(mchId);
        payOrder.setMchOrderNo(mchOrderNo);
        payOrder.setChannelId(channelId);
        payOrder.setPayOrderId(payOrderId);
        payOrder.setAmount(Long.parseLong(amount));
        payOrder.setScene(scene);
        payOrder.setAuthCode(authCode);
        payOrder.setCurrency(currency);
        payOrder.setClientIp(clientIp);
        payOrder.setDevice(device);
        payOrder.setSubject(subject);
        payOrder.setBody(body);
        payOrder.setExtra(extra);
        payOrder.setChannelMchId(channelMchId);
        payOrder.setParam1(param1);
        payOrder.setParam2(param2);
        payOrder.setNotifyUrl(notifyUrl);
        payOrder.setStatus("0");
        payOrder.setCreateTime(new Date());

        return payOrder;
    }
}
