package com.pku.smart.modules.sys.vopackage;

import com.pku.smart.utils.ArithUtils;
import lombok.Data;

@Data
public class SysCpuVo {
    /**
     * 核心数
     */
    private int cpuNum;

    /**
     * CPU总的使用率
     */
    private double total;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double used;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    public double getTotal()
    {
        return ArithUtils.round(ArithUtils.mul(total, 100), 2);
    }

    public double getSys()
    {
        return ArithUtils.round(ArithUtils.mul(sys / total, 100), 2);
    }

    public double getUsed()
    {
        return ArithUtils.round(ArithUtils.mul(used / total, 100), 2);
    }

    public double getWait()
    {
        return ArithUtils.round(ArithUtils.mul(wait / total, 100), 2);
    }

    public double getFree()
    {
        return ArithUtils.round(ArithUtils.mul(free / total, 100), 2);
    }
}
