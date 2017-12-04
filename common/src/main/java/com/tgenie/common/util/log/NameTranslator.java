package com.tgenie.common.util.log;

import com.ejlerp.baseinfo.enums.DictType;
import com.ejlerp.dal.util.Underline2Camel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字段名称翻译器: 用来在记录UPDATE日志的时候, 把字段名翻译成汉字名称
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public interface NameTranslator {

    Logger LOGGER = LoggerFactory.getLogger(NameTranslator.class);

    /**
     * 根据属性名获取汉字名称
     * @param filedName  属性名
     * @return           汉字名称
     */
    String getName(String filedName);

    /**
     * 根据属性名获取字典类型
     * @param filedName  属性名
     * @return           字典类型
     */
    DictType getDictType(String filedName);

    /**
     * 返回一个默认的字段名称翻译器
     * @return     字段名称翻译器
     */
    static NameTranslator getNameTranslatorDefault() {
        return new NameTranslator() {
            @Override
            public String getName(String filedName) {
                return filedName;
            }

            @Override
            public DictType getDictType(String filedName) {
                return null;
            }
        };
    }

    /**
     * 返回一个枚举类通用的的字段名称翻译器
     * @param enumType    枚举类型
     * @return            字段名称翻译器
     */
    static <T extends Enum<T> & INameMap> NameTranslator getNameTranslatorForEnum(Class<T> enumType) {

        if (!enumType.isEnum()) {
            LOGGER.error("本方法入参必须为枚举类型, ");
            return getNameTranslatorDefault();
        }

        return new NameTranslator() {
            @Override
            public String getName(String filedName) {
                try {
                    if (filedName.contains(VoUtil.UNDERLINE)) {
                        return Enum.valueOf(enumType, filedName.toUpperCase()).getName();
                    }
                    return Enum.valueOf(enumType, Underline2Camel.camel2Underline(filedName, false).toUpperCase()).getName();
                } catch (NullPointerException e) {
                    LOGGER.error("{}，该字段未被加入名称枚举类，导致日志字段名称无法翻译，请及时添加！", filedName);
                    return filedName;
                }
            }

            @Override
            public DictType getDictType(String filedName) {
                try {
                    if (filedName.contains(VoUtil.UNDERLINE)) {
                        return Enum.valueOf(enumType, filedName.toUpperCase()).getDictType();
                    }
                    return Enum.valueOf(enumType, Underline2Camel.camel2Underline(filedName, false).toUpperCase()).getDictType();
                } catch (NullPointerException e) {
                    LOGGER.error("{}，该字段未被加入名称枚举类，导致日志字段名称无法翻译，请及时添加！", filedName);
                    return null;
                }
            }
        };
    }

}
