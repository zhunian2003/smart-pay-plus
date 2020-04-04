package com.pku.smart.modules.pay.service;

import java.util.Map;

public interface IPayChannelNotifyService {
    String handleAliPayNotify(String mchId, String channelId, Map params);

    String handleWxPayNotify(String mchId, String channelId, String pay, String xmlResult);
}
