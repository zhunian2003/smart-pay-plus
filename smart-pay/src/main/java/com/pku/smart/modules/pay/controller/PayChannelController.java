package com.pku.smart.modules.pay.controller;

import com.pku.smart.common.annotation.Log;
import com.pku.smart.common.base.BaseController;
import com.pku.smart.common.constant.UserConstants;
import com.pku.smart.common.domain.AjaxResult;
import com.pku.smart.common.enums.BusinessType;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.common.page.TableDataInfo;
import com.pku.smart.modules.pay.entity.PayChannel;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.service.IMchInfoService;
import com.pku.smart.modules.pay.service.IPayChannelService;
import com.pku.smart.utils.DateUtils;
import com.pku.smart.utils.ExcelUtil;
import com.pku.smart.common.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pay/paychannel")
public class PayChannelController extends BaseController {

    private final static MyLog _log = MyLog.getLog(PayChannelController.class);

    @Autowired
    IPayChannelService channelService;

    @Autowired
    IMchInfoService mchInfoService;

    @PreAuthorize("@ss.hasPermi('pay:paychannel:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayChannel channel) {
        startPage();
        List<PayChannel> list = channelService.getChannelList(channel);
        return getDataTable(list);
    }

    @Log(title = "支付渠道", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('pay:paychannel:export')")
    @GetMapping("/export")
    public AjaxResult export(PayChannel channel) {
        List<PayChannel> list = channelService.getChannelList(channel);
        ExcelUtil<PayChannel> util = new ExcelUtil<PayChannel>(PayChannel.class);
        return util.exportExcel(list, "渠道数据");
    }

    @PreAuthorize("@ss.hasPermi('pay:paychannel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable String id) {
        return AjaxResult.success(channelService.selectChannel(Integer.valueOf(id)));
    }

    @PreAuthorize("@ss.hasPermi('pay:paychannel:add')")
    @Log(title = "渠道信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PayChannel channel) {
        String mchId = channel.getMchId();
        PayMchInfo mchInfo = mchInfoService.selectMchInfo(mchId);
        if (mchInfo == null){
            return AjaxResult.error("新增渠道'" + channel.getChannelName() + "'失败，对应商户" + mchId + "不存在");
        }
        if (UserConstants.NOT_UNIQUE.equals(channelService.checkChannelIdUnique(channel))) {
            return AjaxResult.error("新增渠道'" + channel.getChannelName() + "'失败，渠道编码名称" + channel.getChannelId() + "已存在");
        }
        channel.setCreateTime(DateUtils.getNowDate());
        channel.setCreateBy(SecurityUtils.getUsername());
        return toAjax(channelService.addChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('pay:paychannel:edit')")
    @Log(title = "渠道信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PayChannel channel) {
        channel.setUpdateTime(DateUtils.getNowDate());
        channel.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(channelService.updateChannel(channel));
    }

    @PreAuthorize("@ss.hasPermi('pay:paychannel:remove')")
    @Log(title = "渠道信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(channelService.deleteChannelByIds(ids));
    }
}
