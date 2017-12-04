package com.tgenie.common.util.log;

/**
 * @author dzt
 * @date 17/11/8
 * Hope you know what you have done
 */
public interface ILogType {

    /**
     * 操作描述最大字符数
     */
    int MAX_ACTION_DESC_SIZE = 500;

    /**
     * 获取日志类型
     * @return     日志类型
     */
    String getLogType();

    /**
     * 获取操作类型
     * @return     操作类型
     */
    String getLogBizType();

    /**
     * 获取日志主表名称
     * @return     日志主表名称
     */
    String getEntityTableName();

    /**
     * 获取日志主表ID名称
     * @return     日志主表ID名称
     */
    String getEntityIdName();

    /**
     * 获取日志体格式
     * @return     日志体格式字符串
     */
    String getActionDescBodyFormat();

    /**
     * 获取操作描述(500字节一条)
     * @param args 日志参数
     * @return     操作描述
     */
    String[] generateActionDesc(Object[] args);

    /**
     * 把数组的长度+1
     * @param originArr  原始数组
     * @return           长度+1后的数组
     */
    default String[] increaseArrayLength(String[] originArr) {
        String[] newArr = new String[originArr.length + 1];
        System.arraycopy(originArr, 0 , newArr, 0, originArr.length);
        return newArr;
    }

    /**
     * 通过模板 + 参数生成操作描述
     * @param args 日志参数
     * @return     操作描述数组
     */
    default String[] generateSimpleActionDesc(Object[] args) {
        String actionDesc = String.format(getActionDescBodyFormat(), args);
        return new String[] {actionDesc};
    }
}
