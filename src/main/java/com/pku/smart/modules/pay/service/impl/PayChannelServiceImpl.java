package com.pku.smart.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pku.smart.common.constant.UserConstants;
import com.pku.smart.common.exception.CustomException;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayOrder;
import com.pku.smart.modules.pay.mapper.PayChannelMapper;
import com.pku.smart.modules.pay.mapper.PayOrderMapper;
import com.pku.smart.modules.pay.service.IPayChannelService;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayChannelServiceImpl implements IPayChannelService {

    @Autowired
    PayChannelMapper channelMapper;

    @Autowired
    PayOrderMapper payOrderMapper;

    @Override
    public List<PayChannel> getChannelList(PayChannel channel) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(channel.getChannelId())) {
            queryWrapper.like("channel_id", channel.getChannelId());
        }
        if (StringUtils.isNotEmpty(channel.getChannelName())) {
            queryWrapper.like("channel_name", channel.getChannelName());
        }
        if (channel.getStatus() != null) {
            queryWrapper.eq("status", channel.getStatus());
        }
        return channelMapper.selectList(queryWrapper);
    }

    @Override
    public PayChannel selectChannel(Integer id) {
        return channelMapper.selectById(id);
    }

    @Override
    public int addChannel(PayChannel channel) {
        return channelMapper.insert(channel);
    }

    @Override
    public int updateChannel(PayChannel channel) {
        return channelMapper.updateById(channel);
    }

    @Override
    public int deleteChannelByIds(Integer[] ids) {
        for (Integer mchId : ids) {
            QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();//检查是否被使用了 不允许删除
            queryWrapper.eq("mch_id", mchId);
            int count = payOrderMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new CustomException(String.format("%1$s已分配,不能删除", mchId));
            }
        }
        List<Integer> listIds = Arrays.stream(ids).collect(Collectors.toList());
        return channelMapper.deleteBatchIds(listIds);
    }

    /**
     * 检查同商户下渠道编码是否重复
     *
     * @param channel
     * @return
     */
    @Override
    public String checkChannelIdUnique(PayChannel channel) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mch_id", channel.getMchId());
        queryWrapper.eq("channel_id", channel.getChannelId());
        int count = channelMapper.selectCount(queryWrapper);
        return count > 0 ? UserConstants.NOT_UNIQUE : UserConstants.UNIQUE;
    }

    /**
     * 根据商户号渠道编码查询
     *
     * @param channelId
     * @param mchId
     * @return
     */
    @Override
    public PayChannel selectPayChannel(String channelId, String mchId) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mch_id", mchId);
        queryWrapper.eq("channel_id", channelId);
        return channelMapper.selectOne(queryWrapper);
    }
}
