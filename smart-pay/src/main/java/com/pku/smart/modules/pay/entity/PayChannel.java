package com.pku.smart.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_pay_channel")
public class PayChannel extends BaseEntity {

    /**
     * 渠道主键ID
     *
     * @mbggenerated
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 渠道ID
     *
     * @mbggenerated
     */
    @TableField(value = "channel_id")
    private String channelId;

    /**
     * 渠道名称,如:alipay,wechat
     *
     * @mbggenerated
     */
    @TableField(value = "channel_name")
    private String channelName;

    /**
     * 渠道商户ID
     *
     * @mbggenerated
     */
    @TableField(value = "channel_mch_id")
    private String channelMchId;

    /**
     * 商户ID
     *
     * @mbggenerated
     */
    @TableField(value = "mch_id")
    private String mchId;

    /**
     * 渠道状态,0-停止使用,1-使用中
     *
     * @mbggenerated
     */
    @TableField(value = "status")
    private String status;

    /**
     * 配置参数,json字符串
     *
     * @mbggenerated
     */
    @TableField(value = "param")
    private String param;

    /**
     * 备注
     *
     * @mbggenerated
     */
    @TableField(value = "remark")
    private String remark;
}
