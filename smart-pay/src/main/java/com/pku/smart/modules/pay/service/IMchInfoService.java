package com.pku.smart.modules.pay.service;

import com.pku.smart.modules.pay.entity.PayMchInfo;

import java.util.List;

public interface IMchInfoService {

    /**
     * 分页查询
     * @param mchInfo
     * @return
     */
    List<PayMchInfo> getMchInfoList(PayMchInfo mchInfo);

    /**
     * 按照商户号查询
     * @param mchId
     * @return
     */
    PayMchInfo selectMchInfo(String mchId);

    /**
     * 增加
     * @param mchInfo
     * @return
     */
    int addMchInfo(PayMchInfo mchInfo);

    /**
     * 更新
     * @param mchInfo
     * @return
     */
    int updateMchInfo(PayMchInfo mchInfo);

    /**
     *
     * @param mchIds
     */
    int deleteMchInfoByIds(String[] mchIds);

    /**
     *
     * @return
     */
    List<PayMchInfo> selectMchInfotAll();

    /**
     * 检查名称是否唯一
     * @param mchInfo
     * @return
     */
    String checkPostNameUnique(PayMchInfo mchInfo);

    /**
     * 检查编码是否唯一
     * @param mchInfo
     * @return
     */
    String checkPostCodeUnique(PayMchInfo mchInfo);
}
