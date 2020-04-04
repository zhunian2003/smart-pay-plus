package com.pku.smart.modules.pay.api;

import com.alibaba.fastjson.JSONObject;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.service.*;
import com.pku.smart.utils.MapUtils;
import com.pku.smart.utils.PayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ApiQueryController {
    private final MyLog _log = MyLog.getLog(ApiQueryController.class);

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
     * 查询支付订单接口:
     * 1)先验证接口参数以及签名信息
     * 2)根据参数查询订单
     * 3)返回订单数据
     * @param params
     * @return
     */
    @RequestMapping(value = "/query/create_order")
    public String queryPayOrder(@RequestParam String params) {
        _log.info("###### 开始接收商户查询支付订单请求 ######");

        String logPrefix = "【商户支付订单查询】";
        String errorMessage = "";
        try {
            JSONObject po = JSONObject.parseObject(params);
            JSONObject payContext = new JSONObject();
            _log.info("第一步：验证参数有效性。", logPrefix);
            Object object = validateParams(po, payContext);
            if (object instanceof String){
                errorMessage = ((String) object);
                _log.error(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }

            String mchId = po.getString("mchId"); 			    // 商户ID
            String mchOrderNo = po.getString("mchOrderNo"); 	// 商户订单号
            String payOrderId = po.getString("payOrderId"); 	// 支付订单号
            String executeNotify = po.getString("executeNotify");   // 是否执行回调
            String executeQuery = po.getString("executeQuery");   //是否执行查询订单支付状态并同步到本地库

            _log.info("第二步：查询支付订单。", logPrefix);
            PayOrder payOrder = payOrderService.selectPayOrder(mchId, mchOrderNo, payOrderId, executeNotify, executeQuery);
            _log.info("{}查询支付订单,结果:{}", logPrefix, payOrder);
            if (payOrder == null) {
                errorMessage = "订单不存在：" + payOrderId;
                _log.info(errorMessage);
                return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
            }

            _log.info("第三步：构建返回结果。", logPrefix);
            Map retMap = MapUtils.convertBean(payOrder);

            return PayUtils.makeRetSuccess(PayConstant.RETURN_VALUE_SUCCESS, "", retMap);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "商户支付订单查询异常:" + e.getMessage();
            _log.error("商户支付订单查询异常：{}", e.getMessage());
            return PayUtils.makeRetFail(PayConstant.RETURN_VALUE_FAIL,errorMessage);
        }
    }

    private Object validateParams(JSONObject params, JSONObject payContext) {
        // 验证请求参数,参数有问题返回错误提示
        String errorMessage;
        // 支付参数
        String mchId = params.getString("mchId"); 			    // 商户ID
        //String mchOrderNo = params.getString("mchOrderNo"); 	// 商户订单号
        //String payOrderId = params.getString("payOrderId"); 	// 支付订单号
        String channelId = params.getString("channelId"); 	    // 渠道ID
        String sign = params.getString("sign"); 				// 签名

        // 验证请求参数有效性（必选项）
        if(StringUtils.isBlank(mchId)) {
            errorMessage = "request params[mchId] error.";
            return errorMessage;
        }
        if(StringUtils.isBlank(channelId)) {
            errorMessage = "request params[channelId] error.";
            _log.warn(errorMessage);
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
        //PayChannel payChannel = payChannelService.selectPayChannel(channelId, mchId);
        //if (mchInfo == null) {
        //    errorMessage = "Can't found payChannel[mchId=" + mchId + "] record in db.";
        //    return errorMessage;
        //}

        // 验证签名数据
        boolean verifyFlag = PayUtils.verifyPaySign(params, reqKey);
        if (!verifyFlag) {
            errorMessage = "验证签名数据失败.";
            return errorMessage;
        }

        //String channelName = payChannel.getChannelName();
        //params.put("channelName", channelName);

        return params;
    }
}
