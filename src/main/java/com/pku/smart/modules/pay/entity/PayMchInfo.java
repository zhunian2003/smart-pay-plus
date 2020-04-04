package com.pku.smart.modules.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pku.smart.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_mch_info")
public class PayMchInfo extends BaseEntity {
    /**
     * 商户ID
     *
     * @mbggenerated
     */
    @TableId(value = "mch_id",type = IdType.INPUT)
    private String mchId;

    /**
     * 名称
     *
     * @mbggenerated
     */
    @TableField(value = "name")
    private String name;

    /**
     * 类型
     *
     * @mbggenerated
     */
    @TableField(value = "type")
    private String type;

    /**
     * 请求私钥
     *
     * @mbggenerated
     */
    @TableField(value = "req_key")
    private String reqKey;

    /**
     * 响应私钥
     *
     * @mbggenerated
     */
    @TableField(value = "res_key")
    private String resKey;

    /**
     * 商户状态,0-停止使用,1-使用中
     *
     * @mbggenerated
     */
    @TableField(value = "status")
    private String status;

}
