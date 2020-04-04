package com.pku.smart.modules.pay.api;

import com.alibaba.fastjson.JSON;
import com.pku.smart.common.constant.PayConstant;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.modules.pay.service.IPayChannelNotifyService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ApiNotifyController {

    private static final MyLog _log = MyLog.getLog(ApiNotifyController.class);

    @Autowired
    IPayChannelNotifyService payNotifyService;

    /**
     * 微信支付回调通知
     * 适用范围：微信支付包括支付通知和退费通知
     * @param mchId 支付系统商户号
     * @param channelId 渠道号
     * @param pay 支付类型(收费SF、退费TF)
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/{mchId}/{channelId}/notify/{pay}/wxPayNotifyRes.htm")
    @ResponseBody
    public String wxPayNotifyRes(@PathVariable("mchId") String mchId, @PathVariable("channelId") String channelId, @PathVariable("pay") String pay, HttpServletRequest request) throws IOException {
        _log.info("====== 开始接收微信支付回调通知 ======");
        _log.info("打印商户号：{}", mchId);
        _log.info("打印支付渠道：{}", channelId);
        _log.info("打印支付类型(收费SF、退费TF)：{}", pay);
        String xml = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
        _log.info("通知请求数据:{}", xml);
        String notifyRes = doWxPayRes(mchId, channelId, pay, xml);
        _log.info("响应给微信:{}", notifyRes);
        _log.info("====== 完成接收微信支付回调通知 ======");
        return notifyRes;
    }

    /**
     * 支付宝支付回调通知
     * 注意：支付宝退费没有回调(与微信不同)
     * @param mchId
     * @param channelId
     * @param request
     * @return
     */
    @RequestMapping("/{mchId}/{channelId}/notify/pay/aliPayNotifyRes.htm")
    @ResponseBody
    public String aliPayNotifyRes(@PathVariable("mchId") String mchId, @PathVariable("channelId") String channelId, HttpServletRequest request) {
        _log.info("====== 开始接收支付宝支付回调通知 ======");
        _log.info("打印商户号：{}", mchId);
        _log.info("打印支付渠道：{}", channelId);
        String notifyRes = doAliPayRes(mchId, channelId, request);
        _log.info("响应给支付宝:{}", notifyRes);
        _log.info("====== 完成接收支付宝支付回调通知 ======");
        return notifyRes;
    }

    private String doWxPayRes(String mchId, String channelId, String pay, String xml) {
        return payNotifyService.handleWxPayNotify(mchId, channelId, pay, xml);
    }

    private String doAliPayRes(String mchId, String channelId, HttpServletRequest request) {
        String logPrefix = "【支付宝支付回调通知】";
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        _log.info("获取支付宝POST过来反馈信息");
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        _log.info("{}通知请求数据:{}", logPrefix, JSON.toJSONString(params));
        if (params.isEmpty()) {
            _log.error("{}请求参数为空", logPrefix);
            return PayConstant.RETURN_VALUE_FAIL;
        }
        return payNotifyService.handleAliPayNotify(mchId, channelId, params);
    }
}
