package com.pku.smart.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pku.smart.common.constant.UserConstants;
import com.pku.smart.common.exception.CustomException;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.mapper.PayChannelMapper;
import com.pku.smart.modules.pay.mapper.PayMchInfoMapper;
import com.pku.smart.modules.pay.service.IMchInfoService;
import com.pku.smart.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MchInfoServiceImpl implements IMchInfoService {

    @Autowired
    PayMchInfoMapper mchInfoMapper;

    @Autowired
    PayChannelMapper channelMapper;

    /**
     * 分页查询
     *
     * @param mchInfo
     * @return
     */
    @Override
    public List<PayMchInfo> getMchInfoList(PayMchInfo mchInfo) {
        QueryWrapper<PayMchInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(mchInfo.getMchId())){
            queryWrapper.like("mch_id",mchInfo.getMchId());
        }
        if (StringUtils.isNotEmpty(mchInfo.getName())){
            queryWrapper.like("name",mchInfo.getName());
        }
        if (mchInfo.getStatus() != null){
            queryWrapper.eq("status",mchInfo.getStatus());
        }
        queryWrapper.orderByAsc("mch_id");
        List<PayMchInfo> list = mchInfoMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 按照商户号查询
     *
     * @param mchId
     * @return
     */
    @Override
    public PayMchInfo selectMchInfo(String mchId) {
        return mchInfoMapper.selectById(mchId);
    }

    /**
     * 增加
     *
     * @param mchInfo
     * @return
     */
    @Override
    public int addMchInfo(PayMchInfo mchInfo) {
        if (StringUtils.isNotEmpty(mchInfo.getMchId())){
            return mchInfoMapper.updateById(mchInfo);
        }
        QueryWrapper<PayMchInfo> queryWrapper = new QueryWrapper<>();
        String mchId = String.valueOf(mchInfoMapper.selectCount(queryWrapper) + 10000000);
        mchInfo.setMchId(mchId);
        return mchInfoMapper.insert(mchInfo);
    }

    /**
     * 更新
     *
     * @param mchInfo
     * @return
     */
    @Override
    public int updateMchInfo(PayMchInfo mchInfo) {
        return mchInfoMapper.updateById(mchInfo);
    }

    /**
     * @param mchIds
     */
    @Override
    public int deleteMchInfoByIds(String[] mchIds) {
        for (String mchId : mchIds) {
            QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();//检查是否被使用了 不允许删除
            queryWrapper.eq("mch_id",mchId);
            int count = channelMapper.selectCount(queryWrapper);
            if (count > 0){
                throw new CustomException(String.format("%1$s已分配,不能删除", mchId));
            }
        }
        List<String> listIds = Arrays.stream(mchIds).collect(Collectors.toList());
        return mchInfoMapper.deleteBatchIds(listIds);
    }

    /**
     * @return
     */
    @Override
    public List<PayMchInfo> selectMchInfotAll() {
        QueryWrapper<PayMchInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("mch_id");
        List<PayMchInfo> list = mchInfoMapper.selectList(queryWrapper);
        return list;
    }

    /**
     * 检查名称是否唯一
     *
     * @param mchInfo
     * @return
     */
    @Override
    public String checkPostNameUnique(PayMchInfo mchInfo) {
        return UserConstants.UNIQUE;
    }

    /**
     * 检查编码是否唯一
     *
     * @param mchInfo
     * @return
     */
    @Override
    public String checkPostCodeUnique(PayMchInfo mchInfo) {
        QueryWrapper<PayMchInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mch_id",mchInfo.getMchId());
        int count = mchInfoMapper.selectCount(queryWrapper);
        return count > 0 ? UserConstants.NOT_UNIQUE : UserConstants.UNIQUE;
    }
}
