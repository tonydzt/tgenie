package com.tgenie.common.util.log;


import com.ejlerp.baseinfo.enums.DictType;
import com.ejlerp.common.util.DateUtil;
import org.springframework.util.ObjectUtils;

import java.util.Date;

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
    private DictType dictType;

    /**
     * 价格字段，需要做特殊处理，除以10000
     */
    private static final String PRICE_COLUMN = "价";

    /**
     * 金额字段，需要做特殊处理，除以10000
     */
    private static final String AMOUNT_COLUMN = "金额";


    UpdateUnit(String columnName, Object originValue, Object newValue, DictType dictType) {
        this.columnName = columnName;
        this.originValue = originValue;
        this.newValue = newValue;
        this.dictType = dictType;
    }

    @Override
    public String toString() {
        if (ObjectUtils.isEmpty(originValue)) {
            if (columnName.toLowerCase().contains(PRICE_COLUMN) || columnName.toLowerCase().contains(AMOUNT_COLUMN)) {
                return columnName + "变为[" + (Integer.parseInt(newValue.toString()) / 10000.0d) + "];";
            }
            if (originValue instanceof Date) {
                return columnName + "变为[" + DateUtil.datetime2String((Date)newValue) + "];";
            }
            return columnName + "变为[" + newValue + "];";
        } else {
            if (columnName.toLowerCase().contains(PRICE_COLUMN) || columnName.toLowerCase().contains(AMOUNT_COLUMN)) {
                return columnName + "由[" + (Integer.parseInt(originValue.toString()) / 10000.0d) + "]变为[" + (Integer.parseInt(newValue.toString()) / 10000.0d) + "];";
            }
            if (originValue instanceof Date) {
                return columnName + "由[" + DateUtil.datetime2String((Date)originValue) + "]变为[" + DateUtil.datetime2String((Date)newValue) + "];";
            }
            return columnName + "由[" + originValue + "]变为[" + newValue + "];";
        }
    }

    public DictType getDictType() {
        return dictType;
    }

    public Object getOriginValue() {
        return originValue;
    }

    public void setOriginValue(Object originValue) {
        this.originValue = originValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
