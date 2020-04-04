package com.pku.smart.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PayMchInfoMapper extends BaseMapper<PayMchInfo> {
}
