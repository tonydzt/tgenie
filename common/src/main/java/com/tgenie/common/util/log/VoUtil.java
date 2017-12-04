package com.tgenie.common.util.log;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 对比，找出修改的字段
 * 1、实体类对比
 * 2、把更新Map转化为UpdateUnit
 *
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public class VoUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(VoUtil.class);

    private static final List<String> IGNORE_COLUMN = Lists.newArrayList("creator", "createdAt", "lastUpdater", "lastUpdated", "isUsable", "tenantId", "version");
    public static final String UNDERLINE = "_";

    public static <T> List<UpdateUnit> checkUpdatedColumn(T originVo, T newVo, NameTranslator nameTranslator) {

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

    public static List<UpdateUnit> transferToUpdateUnit(Map<String, Object> updateMap, NameTranslator nameTranslator) {
        if (updateMap == null || updateMap.size() < 1) {
            return null;
        }
        List<UpdateUnit> updateUnits = new ArrayList<>();
        for (Map.Entry<String, Object> entry : updateMap.entrySet()) {
            updateUnits.add(new UpdateUnit(nameTranslator.getName(entry.getKey()), null, entry.getValue(), nameTranslator.getDictType(entry.getKey())));
        }
        return updateUnits;
    }

    /**
     * 从ResultSet中获取记录并填充到Entity.
     *
     * @param originVo 原VO
     * @param newVo    新VO
     * @param field    属性对象
     * @return 两个VO的指定Field是否相等
     */
    private static <T> UpdateUnit isEqual(T originVo, T newVo, Field field, NameTranslator nameTranslator) {

        if (IGNORE_COLUMN.contains(field.getName())) {
            return null;
        }
        //根据field的类型,调用ResultSet对应的get方法
        field.setAccessible(true);
        Boolean isEqual;
        try {
            if (field.getType().equals(Date.class) && field.get(originVo) != null && field.get(newVo) != null) {
                isEqual = ((Date) field.get(originVo)).getTime() == ((Date) field.get(newVo)).getTime();
            } else {
                isEqual = Objects.deepEquals(field.get(originVo), field.get(newVo));
            }

            return isEqual ? null : new UpdateUnit(nameTranslator.getName(field.getName()), field.get(originVo), field.get(newVo), nameTranslator.getDictType(field.getName()));
        } catch (IllegalAccessException e) {
            LOGGER.error("记录日志，获取属性时发生错误：", e);
            return null;
        }
    }

}
