package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayChannel;

import java.util.List;

public interface IPayChannelService {

    List<PayChannel> getChannelList(PayChannel channel);

    PayChannel selectChannel(Integer id);

    int addChannel(PayChannel channel);

    int updateChannel(PayChannel channel);

    int deleteChannelByIds(Integer[] ids);

    /**
     * 根据商户号渠道编码查询
     * @param channelId
     * @param mchId
     * @return
     */
    PayChannel selectPayChannel(String channelId, String mchId);

    /**
     * 检查同商户下渠道编码是否重复
     * @param channel
     * @return
     */
    String checkChannelIdUnique(PayChannel channel);
}
