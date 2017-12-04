package com.tgenie.common.util.log;

import com.ejlerp.baseinfo.enums.DictType;

/**
 * @author dzt
 * @date 17/12/2
 * Hope you know what you have done
 */
public interface INameMap {

    /**
     * 获取汉字名称
     * @return  汉字名称
     */
    String getName();

    /**
     * 获取字典类型
     * @return  字典类型
     */
    DictType getDictType();

}
