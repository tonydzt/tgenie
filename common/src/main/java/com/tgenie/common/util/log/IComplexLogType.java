package com.tgenie.common.util.log;

/**
 * @author dzt
 * @date 17/11/22
 * Hope you know what you have done
 */
public interface IComplexLogType extends ILogType {

    /**
     * 获取日志头
     * @return     日志头
     */
    String getActionHead();

    /**
     * 获取日志参数个数
     * @return     日志参数个数
     */
    Integer getParamNum();


    /**
     * 通过模板 + 参数生成操作描述，500个字符为一条（MAX_ACTION_DESC_SIZE）
     * @param args 日志参数
     * @return     操作描述数组
     */
    default String[] generateComplexActionDesc(Object[] args) {
        if (args == null || args.length < 1 || getParamNum() < 1) {
            return new String[]{getActionHead()};
        }
        int startIndex = 0;
        String[] result = new String[1];
        StringBuilder actionDesc = new StringBuilder();
        while (startIndex < args.length) {
            Object[] segmentParam = new Object[getParamNum()];
            System.arraycopy(args, startIndex, segmentParam, 0, args.length - startIndex < getParamNum() ? args.length - startIndex : getParamNum());
            String actionDescTmp = String.format(getActionDescBodyFormat(), segmentParam) + "\n";
            if (actionDesc.length() + actionDescTmp.length() > MAX_ACTION_DESC_SIZE) {
                result[result.length - 1] = actionDesc.toString();
                result = increaseArrayLength(result);
                actionDesc.delete(0, actionDesc.length());
            }
            actionDesc.append(actionDescTmp);
            startIndex = startIndex + getParamNum();
        }
        result[result.length - 1] = actionDesc.toString();
        return result;
    }
}
