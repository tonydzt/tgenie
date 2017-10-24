package com.tgenie.common.util.enums;

/**
 *
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
@FunctionalInterface
public interface NameTranslator {

    /**
     * 根据属性名获取汉字名称
     * @param filedName  属性名
     * @return           汉字名称
     */
    String getName(String filedName);
}
