package com.pku.smart.common.exception;

import com.pku.smart.modules.job.entity.SysJob;
import com.pku.smart.utils.JobInvokeUtil;
import org.quartz.JobExecutionContext;

public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
