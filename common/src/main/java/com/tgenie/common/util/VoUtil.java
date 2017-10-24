package com.tgenie.common.util;

import com.tgenie.common.util.enums.NameTranslator;
import com.tgenie.common.util.vo.UpdateUnit;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public class VoUtil {

    public static <T> List<UpdateUnit> checkUpdatedColumn(T originVo, T newVo, NameTranslator nameTranslator) throws IllegalAccessException {

        Field[] fields = originVo.getClass().getDeclaredFields();
        List<UpdateUnit> updateUnits = new ArrayList<>();
        for (Field field : fields) {
            UpdateUnit isEqual = isEqual(originVo, newVo, field, nameTranslator);
            if (isEqual != null) {
                updateUnits.add(isEqual);
            }
        }
        return updateUnits;
    }

    /**
     * 从ResultSet中获取记录并填充到Entity.
     *
     * @param originVo   原VO
     * @param newVo      新VO
     * @param field      属性对象
     * @return           两个VO的指定Field是否相等
     */
    private static <T> UpdateUnit isEqual(T originVo, T newVo, Field field, NameTranslator nameTranslator) throws IllegalAccessException {

        //根据field的类型,调用ResultSet对应的get方法
        field.setAccessible(true);
        Boolean isEqual = Objects.deepEquals(field.get(originVo), field.get(newVo));
        return isEqual ? null : new UpdateUnit(nameTranslator.getName(field.getName()), field.get(originVo), field.get(newVo));
    }

}
