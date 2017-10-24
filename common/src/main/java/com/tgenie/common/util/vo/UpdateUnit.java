package com.tgenie.common.util.vo;

/**
 *
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public class UpdateUnit {

    private String columnName;
    private Object originValue;
    private Object newValue;

    public UpdateUnit(String columnName, Object originValue, Object newValue) {
        this.columnName = columnName;
        this.originValue = originValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return columnName + "由[" + originValue + "]变为[" + newValue + "]";
    }
}
