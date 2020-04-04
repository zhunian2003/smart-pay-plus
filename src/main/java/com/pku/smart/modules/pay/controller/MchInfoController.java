package com.pku.smart.modules.pay.controller;

import com.pku.smart.common.annotation.Log;
import com.pku.smart.common.base.BaseController;
import com.pku.smart.common.constant.UserConstants;
import com.pku.smart.common.domain.AjaxResult;
import com.pku.smart.common.enums.BusinessType;
import com.pku.smart.common.mylog.MyLog;
import com.pku.smart.common.page.TableDataInfo;
import com.pku.smart.modules.pay.entity.PayMchInfo;
import com.pku.smart.modules.pay.service.IMchInfoService;
import com.pku.smart.utils.DateUtils;
import com.pku.smart.utils.ExcelUtil;
import com.pku.smart.common.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pay/mchinfo")
public class MchInfoController extends BaseController {

    private final static MyLog _log = MyLog.getLog(MchInfoController.class);

    @Autowired
    IMchInfoService mchInfoService;

    /**
     * 获取商户列表
     */
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(PayMchInfo mchInfo) {
        startPage();
        List<PayMchInfo> list = mchInfoService.getMchInfoList(mchInfo);
        return getDataTable(list);
    }

    @Log(title = "商户信息", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:export')")
    @GetMapping("/export")
    public AjaxResult export(PayMchInfo mchInfo) {
        List<PayMchInfo> list = mchInfoService.getMchInfoList(mchInfo);
        ExcelUtil<PayMchInfo> util = new ExcelUtil<PayMchInfo>(PayMchInfo.class);
        return util.exportExcel(list, "岗位数据");
    }

    /**
     * 根据商户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:query')")
    @GetMapping(value = "/{mchId}")
    public AjaxResult getInfo(@PathVariable String mchId) {
        PayMchInfo mchInfo = mchInfoService.selectMchInfo(mchId);
        return AjaxResult.success(mchInfo);
    }

    /**
     * 新增商户
     */
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:add')")
    @Log(title = "商户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PayMchInfo mchInfo) {
        if (UserConstants.NOT_UNIQUE.equals(mchInfoService.checkPostNameUnique(mchInfo))) {
            return AjaxResult.error("新增商户'" + mchInfo.getName() + "'失败，商户名称已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(mchInfoService.checkPostCodeUnique(mchInfo))) {
            return AjaxResult.error("新增商户'" + mchInfo.getName() + "'失败，商户编码已存在");
        }
        mchInfo.setCreateTime(DateUtils.getNowDate());
        mchInfo.setCreateBy(SecurityUtils.getUsername());
        return toAjax(mchInfoService.addMchInfo(mchInfo));
    }

    /**
     * 修改商户
     */
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:edit')")
    @Log(title = "商户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PayMchInfo mchInfo) {
        mchInfo.setUpdateTime(DateUtils.getNowDate());
        mchInfo.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(mchInfoService.updateMchInfo(mchInfo));
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('pay:mchinfo:remove')")
    @Log(title = "商户信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mchIds}")
    public AjaxResult remove(@PathVariable String[] mchIds) {
        return toAjax(mchInfoService.deleteMchInfoByIds(mchIds));
    }

    /**
     * 获取商户选择框列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect() {
        List<PayMchInfo> posts = mchInfoService.selectMchInfotAll();
        return AjaxResult.success(posts);
    }
}
