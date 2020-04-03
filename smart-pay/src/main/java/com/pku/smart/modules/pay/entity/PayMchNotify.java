package com.pku.smart.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_mch_notify")
public class PayMchNotify extends BaseEntity {
    /**
     * 订单ID
     *
     * @mbggenerated
     */
    @TableId(value = "order_id",type = IdType.INPUT)
    private String orderId;

    /**
     * 商户ID
     *
     * @mbggenerated
     */
    @TableField(value = "mch_id")
    private String mchId;

    /**
     * 商户订单号
     *
     * @mbggenerated
     */
    @TableField(value = "mch_order_no")
    private String mchOrderNo;

    /**
     * 订单类型:1-支付,2-转账,3-退款
     *
     * @mbggenerated
     */
    @TableField(value = "order_type")
    private String orderType;

    /**
     * 通知地址
     *
     * @mbggenerated
     */
    @TableField(value = "notify_url")
    private String notifyUrl;

    /**
     * 通知次数
     *
     * @mbggenerated
     */
    @TableField(value = "notify_count")
    private Integer notifyCount;

    /**
     * 通知响应结果
     *
     * @mbggenerated
     */
    @TableField(value = "result")
    private String result;

    /**
     * 通知状态,1-通知中,2-通知成功,3-通知失败
     *
     * @mbggenerated
     */
    @TableField(value = "status")
    private String status;

    /**
     * 最后一次通知时间
     *
     * @mbggenerated
     */
    @TableField(value = "last_notify_time")
    private Date lastNotifyTime;

}
