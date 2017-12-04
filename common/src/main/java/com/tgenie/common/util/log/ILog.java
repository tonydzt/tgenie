package com.tgenie.common.util.log;

import com.ejlerp.baseinfo.api.LogBizRecordService;

/**
 * @author dzt
 * @date 17/11/22
 * Hope you know what you have done
 */
public interface ILog {

    /**
     * 返回日志持久化类
     * @return LogBizRecordService
     */
    LogBizRecordService getLogBizRecordService();

}
