package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayMchNotify;

public interface IMchNotifyService {
    /**
     * 查询商户通知
     * @param orderId
     * @return
     */
    PayMchNotify selectMchNotify(String orderId);

    /**
     * 更新通知次数
     * @param orderId
     * @param cnt
     * @return
     */
    int updateNotify4Count(String orderId, Integer cnt);

    /**
     * 保存
     * @param mchNotify
     * @return
     */
    int saveMchNotify(PayMchNotify mchNotify);
}
