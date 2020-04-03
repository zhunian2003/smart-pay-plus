package com.pku.smart.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_pay_order")
public class PayOrder extends BaseEntity {
    /**
     * 支付订单号
     *
     * @mbggenerated
     */
    @TableId(value = "pay_order_id",type = IdType.INPUT)
    private String payOrderId;

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
     * 渠道ID
     *
     * @mbggenerated
     */
    @TableField(value = "channel_id")
    private String channelId;

    /**
     * 支付金额,单位分
     *
     * @mbggenerated
     */
    @TableField(value = "amount")
    private Long amount;

    /**
     * 支付场景
     * 条码支付，取值：bar_code
     * 声波支付，取值：wave_code
     */
    @TableField(value = "scene")
    private String scene;

    /**
     * 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
     */
    @TableField(value = "auth_code")
    private String authCode;

    /**
     * 三位货币代码,人民币:cny
     *
     * @mbggenerated
     */
    @TableField(value = "currency")
    private String currency;

    /**
     * 支付状态,0-订单生成,1-支付中(目前未使用),2-支付成功,3-业务处理完成
     *
     * @mbggenerated
     */
    @TableField(value = "status")
    private String status;

    /**
     * 客户端IP
     *
     * @mbggenerated
     */
    @TableField(value = "client_ip")
    private String clientIp;

    /**
     * 设备
     *
     * @mbggenerated
     */
    @TableField(value = "device")
    private String device;

    /**
     * 商品标题
     *
     * @mbggenerated
     */
    @TableField(value = "subject")
    private String subject;

    /**
     * 商品描述信息
     *
     * @mbggenerated
     */
    @TableField(value = "body")
    private String body;

    /**
     * 特定渠道发起时额外参数
     *
     * @mbggenerated
     */
    @TableField(value = "extra")
    private String extra;

    /**
     * 渠道商户ID
     *
     * @mbggenerated
     */
    @TableField(value = "channel_mch_id")
    private String channelMchId;

    /**
     * 渠道订单号
     *
     * @mbggenerated
     */
    @TableField(value = "channel_order_no")
    private String channelOrderNo;

    /**
     * 渠道支付错误码
     *
     * @mbggenerated
     */
    @TableField(value = "err_code")
    private String errCode;

    /**
     * 渠道支付错误描述
     *
     * @mbggenerated
     */
    @TableField(value = "err_msg")
    private String errMsg;

    /**
     * 扩展参数1
     *
     * @mbggenerated
     */
    @TableField(value = "param1")
    private String param1;

    /**
     * 扩展参数2
     *
     * @mbggenerated
     */
    @TableField(value = "param2")
    private String param2;

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
     * 最后一次通知时间
     *
     * @mbggenerated
     */
    @TableField(value = "last_notify_time")
    private Long lastNotifyTime;

    /**
     * 订单失效时间
     *
     * @mbggenerated
     */
    @TableField(value = "expire_time")
    private Long expireTime;

    /**
     * 订单支付成功时间
     *
     * @mbggenerated
     */
    @TableField(value = "pay_succ_time")
    private Long paySuccTime;
}
