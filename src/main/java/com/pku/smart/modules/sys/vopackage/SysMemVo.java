package com.pku.smart.modules.sys.vopackage;

import com.pku.smart.utils.ArithUtils;
import lombok.Data;

@Data
public class SysMemVo {
    /**
     * 内存总量
     */
    private double total;

    /**
     * 已用内存
     */
    private double used;

    /**
     * 剩余内存
     */
    private double free;

    private double usage;

    public double getTotal()
    {
        return ArithUtils.div(total, (1024 * 1024 * 1024), 2);
    }

    public double getUsed()
    {
        return ArithUtils.div(used, (1024 * 1024 * 1024), 2);
    }

    public double getFree()
    {
        return ArithUtils.div(free, (1024 * 1024 * 1024), 2);
    }

    public double getUsage()
    {
        return ArithUtils.mul(ArithUtils.div(used, total, 4), 100);
    }
}
