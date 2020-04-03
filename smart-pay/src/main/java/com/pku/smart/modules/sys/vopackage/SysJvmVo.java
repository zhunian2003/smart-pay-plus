package com.pku.smart.modules.sys.vopackage;

import com.pku.smart.utils.ArithUtils;
import lombok.Data;

@Data
public class SysJvmVo {
    /**
     * 当前JVM占用的内存总数(M)
     */
    private double total;

    /**
     * JVM最大可用内存总数(M)
     */
    private double max;

    /**
     * JVM空闲内存(M)
     */
    private double free;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    private double used;

    private double usage;

    private String name;

    private String startTime;

    private String runTime;

    public double getTotal()
    {
        return ArithUtils.div(total, (1024 * 1024), 2);
    }

    public double getMax()
    {
        return ArithUtils.div(max, (1024 * 1024), 2);
    }

    public double getFree()
    {
        return ArithUtils.div(free, (1024 * 1024), 2);
    }

    public double getUsed()
    {
        return ArithUtils.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage()
    {
        return ArithUtils.mul(ArithUtils.div(total - free, total, 4), 100);
    }
}
