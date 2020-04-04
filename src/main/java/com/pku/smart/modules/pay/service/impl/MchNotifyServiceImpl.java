package com.pku.smart.modules.pay.service.impl;

import com.pku.smart.modules.pay.entity.PayMchNotify;
import com.pku.smart.modules.pay.mapper.PayMchNotifyMapper;
import com.pku.smart.modules.pay.service.IMchNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MchNotifyServiceImpl implements IMchNotifyService {

    @Autowired
    PayMchNotifyMapper mchNotifyMapper;

    /**
     * 查询商户通知
     *
     * @param orderId
     * @return
     */
    @Override
    public PayMchNotify selectMchNotify(String orderId) {
        return mchNotifyMapper.selectById(orderId);
    }

    /**
     * 更新通知次数
     *
     * @param orderId
     * @param cnt
     * @return
     */
    @Override
    public int updateNotify4Count(String orderId, Integer cnt) {
        PayMchNotify mchNotify = this.selectMchNotify(orderId);
        if (mchNotify != null){
            mchNotify.setNotifyCount(cnt);
            return mchNotifyMapper.updateById(mchNotify);
        }
        return 0;
    }

    /**
     * 保存
     *
     * @param mchNotify
     * @return
     */
    @Override
    public int saveMchNotify(PayMchNotify mchNotify) {
        mchNotify.setUpdateTime(new Date());
        return mchNotifyMapper.insert(mchNotify);
    }
}
