package com.pku.smart.modules.pay.api;

import com.alibaba.fastjson.JSONObject;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.entity.PayRefundOrder;
import com.pku.smart.modules.pay.service.*;
import com.pku.smart.utils.MySeqUtils;
import com.pku.smart.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class ApiRefundController {
    private final MyLog _log = MyLog.getLog(ApiRefundController.class);

    @Autowired
    IMchInfoService mchInfoService;

    @Autowired
    IRefundOrderService refundOrderService;

    @Autowired
    IPayChannelService payChannelService;

    @Autowired
    IPayChannel4WxService payChannel4WxService;

    @Autowired
    IPayChannel4AliService payChannel4AliService;

    /**
     * 订单撤销
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/cancel/create_order")
    public String cancelOrder(@RequestParam String params) {
        _log.info("###### 开始接收商户统一撤销请求 ######");

        String logPrefix = "【商户统一撤销】";
        String errorMessage = "";
        try {
            JSONObject po = JSONObject.parseObject(params);
            JSONObject payContext = new JSONObject();

            _log.info("{}第一步：验证参数有效性。", logPrefix);
            Object object = validateParams(po, payContext);
            if (object instanceof String) {
                errorMessage = "参数校验不通过:" + object;
                _log.error("{}参数校验不通过:{}", logPrefix, object);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
            PayRefundOrder refundOrder = (PayRefundOrder) object;

            _log.info("{}第二步：根据支付订单创建退款订单", logPrefix);
            int result = refundOrderService.createRefundOrder(refundOrder);
            if (result != 1) {
                errorMessage = "创建撤销订单失败";
                _log.error("{}创建撤销订单失败", logPrefix);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }

            _log.info("{}第三步：调用服务进行撤销", logPrefix);
            String channelId = refundOrder.getChannelId();
            switch (channelId) {
                case PayConstant.PAY_CHANNEL_WX_NATIVE:
                    return payChannel4WxService.doWxCancelReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_WX_MICROPAY:
                    return payChannel4WxService.doWxCancelReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                    return payChannel4AliService.doAliCancelReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_BR:
                    return payChannel4AliService.doAliCancelReq(channelId, refundOrder);
                default:
                    errorMessage = "不支持的支付渠道类型[channelId=" + channelId + "]";
                    _log.error(errorMessage);
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "商户统一撤销异常:" + e.getMessage();
            _log.error("商户统一撤销异常：{}", e.getMessage());
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }
    }

    /**
     * 订单退费
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/api/refund/create_order")
    public String refundOrder(@RequestParam String params) {
        _log.info("###### 开始接收商户统一退款请求 ######");

        String logPrefix = "【商户统一退款】";
        String errorMessage = "";
        try {
            JSONObject po = JSONObject.parseObject(params);
            JSONObject payContext = new JSONObject();

            _log.info("{}第一步：验证参数有效性。", logPrefix);
            Object object = validateParams(po, payContext);
            if (object instanceof String) {
                errorMessage = "参数校验不通过:" + object;
                _log.error("{}参数校验不通过:{}", logPrefix, object);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
            PayRefundOrder refundOrder = (PayRefundOrder) object;

            _log.info("{}第二步：根据支付订单创建退款订单", logPrefix);
            int result = refundOrderService.createRefundOrder(refundOrder);
            if (result != 1) {
                errorMessage = "创建退款订单失败";
                _log.error("{}创建退款订单失败", logPrefix);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }

            _log.info("{}第三步：调用服务进行退款", logPrefix);
            String channelId = refundOrder.getChannelId();
            switch (channelId) {
                case PayConstant.PAY_CHANNEL_WX_NATIVE:
                    return payChannel4WxService.doWxRefundReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_WX_MICROPAY://微信条码支付有撤销接口
                    return payChannel4WxService.doWxRefundReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                    return payChannel4AliService.doAliRefundReq(channelId, refundOrder);
                case PayConstant.PAY_CHANNEL_ALIPAY_BR:
                    return payChannel4AliService.doAliRefundReq(channelId, refundOrder);
                default:
                    errorMessage = "不支持的支付渠道类型[channelId=" + channelId + "]";
                    _log.error(errorMessage);
                    return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "商户统一退款异常:" + e.getMessage();
            _log.error("商户统一退款异常：{}", e.getMessage());
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL, errorMessage);
        }
    }

    /**
     * 参数验证
     *
     * @param params
     * @param refundContext
     * @return
     */
    private Object validateParams(JSONObject params, JSONObject refundContext) {
        String errorMessage;// 验证请求参数,参数有问题返回错误提示
        String payOrderId = params.getString("payOrderId");     // 支付订单号
        String mchOrderNo = params.getString("mchOrderNo");     // 商户支付单号
        String mchId = params.getString("mchId");                // 商户ID
        String channelId = params.getString("channelId");        // 渠道ID
        String amount = params.getString("amount");            // 退款金额（单位分）
        String currency = params.getString("currency");         // 币种
        String clientIp = params.getString("clientIp");            // 客户端IP
        String device = params.getString("device");            // 设备
        String remarkInfo = params.getString("remarkInfo");        // 备注
        String userName = params.getString("userName");            // 用户姓名
        String channelUser = params.getString("channelUser");    // 渠道用户标识,如微信openId,支付宝账号
        String extra = params.getString("extra");                // 特定渠道发起时额外参数
        String notifyUrl = params.getString("notifyUrl");        // 转账结果回调URL
        String param1 = params.getString("param1");            // 扩展参数1
        String param2 = params.getString("param2");            // 扩展参数2
        String sign = params.getString("sign");                // 签名
        String refundType = params.getString("refundType");        //退款方式 0、退款 1、撤销 默认0

        System.out.println("验证请求参数有效性（必选项）");
        if (StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(payOrderId) && StringUtils.isBlank(mchOrderNo)) {
            errorMessage = "request params[payOrderId,mchOrderNo] error.";
            return errorMessage;
        }
        if (StringUtils.isBlank(channelId)) {
            errorMessage = "request params[channelId] error.";
            return errorMessage;
        }
        if (!NumberUtils.isCreatable(amount) && refundType == "0") {
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
        _log.info("签名信息");
        if (StringUtils.isEmpty(sign)) {
            errorMessage = "request params[sign] error.";
            return errorMessage;
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

        PayRefundOrder refundOrder = new PayRefundOrder();
        refundOrder.setRefundOrderId(MySeqUtils.getRefund());
        refundOrder.setPayOrderId(payOrderId);
        refundOrder.setMchId(mchId);
        refundOrder.setChannelId(channelId);
        refundOrder.setRefundAmount(Long.parseLong(amount));
        refundOrder.setCurrency(currency);
        refundOrder.setStatus("0");
        refundOrder.setResult("0");
        refundOrder.setClientIp(clientIp);
        refundOrder.setDevice(device);
        refundOrder.setRemarkInfo(remarkInfo);
        refundOrder.setChannelUser(channelUser);
        refundOrder.setUserName(userName);
        refundOrder.setExtra(extra);
        refundOrder.setNotifyUrl(notifyUrl);
        refundOrder.setParam1(param1);
        refundOrder.setParam2(param2);

        return refundOrder;
    }
}
