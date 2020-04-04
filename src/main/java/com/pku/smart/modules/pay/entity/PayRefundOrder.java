package com.pku.smart.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_refund_order")
public class PayRefundOrder extends BaseEntity {
    /**
     * 退款订单号
     *
     * @mbggenerated
     */
    @TableId(value = "refund_order_id",type = IdType.INPUT)
    private String refundOrderId;

    /**
     * 支付订单号
     *
     * @mbggenerated
     */
    @TableField(value = "pay_order_id")
    private String payOrderId;

    /**
     * 渠道支付单号
     *
     * @mbggenerated
     */
    @TableField(value = "channel_pay_order_no")
    private String channelPayOrderNo;

    /**
     * 商户ID
     *
     * @mbggenerated
     */
    @TableField(value = "mch_id")
    private String mchId;

    /**
     * 商户退款单号
     *
     * @mbggenerated
     */
    @TableField(value = "mch_refund_no")
    private String mchRefundNo;

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
    @TableField(value = "pay_amount")
    private Long payAmount;

    /**
     * 退款金额,单位分
     *
     * @mbggenerated
     */
    @TableField(value = "refund_amount")
    private Long refundAmount;

    /**
     * 三位货币代码,人民币:cny
     *
     * @mbggenerated
     */
    @TableField(value = "currency")
    private String currency;

    /**
     * 退款状态:0-订单生成,1-退款中,2-退款成功,3-退款失败,4-业务处理完成
     *
     * @mbggenerated
     */
    @TableField(value = "status")
    private String status;

    /**
     * 退款结果:0-不确认结果,1-等待手动处理,2-确认成功,3-确认失败
     *
     * @mbggenerated
     */
    @TableField(value = "result")
    private String result;

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
     * 备注
     *
     * @mbggenerated
     */
    @TableField(value = "remark_info")
    private String remarkInfo;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     *
     * @mbggenerated
     */
    @TableField(value = "channel_user")
    private String channelUser;

    /**
     * 用户姓名
     *
     * @mbggenerated
     */
    @TableField(value = "user_name")
    private String userName;

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
     * 渠道错误码
     *
     * @mbggenerated
     */
    @TableField(value = "channel_err_code")
    private String channelErrCode;

    /**
     * 渠道错误描述
     *
     * @mbggenerated
     */
    @TableField(value = "channel_err_msg")
    private String channelErrMsg;

    /**
     * 特定渠道发起时额外参数
     *
     * @mbggenerated
     */
    @TableField(value = "extra")
    private String extra;

    /**
     * 通知地址
     *
     * @mbggenerated
     */
    @TableField(value = "notify_url")
    private String notifyUrl;

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
     * 订单失效时间
     *
     * @mbggenerated
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 订单退款成功时间
     *
     * @mbggenerated
     */
    @TableField(value = "refund_succ_time")
    private Date refundSuccTime;
}
