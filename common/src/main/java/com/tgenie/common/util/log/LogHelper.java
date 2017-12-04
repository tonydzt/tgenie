package com.tgenie.common.util.log;

import com.ejlerp.baseinfo.api.TranslatorService;
import com.ejlerp.baseinfo.domain.LogBizRecord;
import com.ejlerp.baseinfo.domain.TranslateUnit;
import com.ejlerp.baseinfo.enums.DictType;
import com.ejlerp.common.vo.CallerInfo;
import com.ejlerp.dal.framework.domain.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * 可做成service微服务形式，也可做成包导入形式，
 * 由于service形式无法解决ILogType实现类的反序列化问题，且把实现类都放入baseinfo加入了不必须的业务耦合，所以暂时做成包导入形式
 *
 * @author dzt
 * @date 17/10/23
 * Hope you know what you have done
 */
public class LogHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(LogHelper.class);

    private static final String LOG_METHOD_NAME = "persistLog";
    private static final String INVOKE_METHOD_NAME = "invoke";
    private static final String PROCEED_METHOD_NAME = "proceed";
    private static final String INTERCEPT_METHOD_NAME = "intercept";

    private final ILog iLog;

    public LogHelper(ILog iLog) {
        this.iLog = iLog;
    }

    /**
     * 存储主表日志
     *
     * @param callerInfo callerInfo
     * @param ids        主表ids
     * @param iLogType   日志枚举类
     * @param args       日志参数
     */
    public void persistLog(CallerInfo callerInfo, List<Long> ids, ILogType iLogType, List<Object[]> args) {

        checkAndTranslate(callerInfo.getTenantId(), args);

        try {
            String actionPath = getPersistLongInvoker();
            List<LogBizRecord> recordList = new ArrayList<>();

            if (args == null) {
                for (int i = 0; i < ids.size(); i++) {
                    addLog(recordList, iLogType, iLogType.generateActionDesc(null)[0], actionPath, ids.size() == 1 ? ids.get(0) : ids.get(i));
                }
            } else {
                for (int i = 0; i < args.size(); i++) {
                    String[] actionDescs = iLogType.generateActionDesc(args.get(i));
                    for (String actionDesc : actionDescs) {
                        addLog(recordList, iLogType, actionDesc, actionPath, ids.size() == 1 ? ids.get(0) : ids.get(i));
                    }
                }
            }

            if (recordList.size() > 0) {
                iLog.getLogBizRecordService().saveWithoutCallInfo(callerInfo, recordList);
            }
        } catch (Exception e) {
            LOGGER.error("记录采购订单【" + ids.toString() + "】日志时发生错误：", e);
        }
    }

    /**
     * 生成日志，并加入日志集合
     *
     * @param recordList 日志集合
     * @param iLogType   日志枚举类
     * @param actionDesc 执行操作
     * @param actionPath 方法名
     * @param id         日志主表id
     */
    private void addLog(List<LogBizRecord> recordList, ILogType iLogType, String actionDesc, String actionPath, Long id) {
        LogBizRecord record = new LogBizRecord();
        record.setLogSysRecordId(0L);
        record.setLogType(iLogType.getLogType());
        record.setLogBizType(iLogType.getLogBizType());
        record.setActionDesc(actionDesc);
        record.setIsException(0);
        record.setEntityTableName(iLogType.getEntityTableName());
        record.setEntityIdName(iLogType.getEntityIdName());
        record.setActionPath(actionPath);
        record.setEntityId(id);
        record.setResultDesc("成功");
        record.setIsFailed(0);
        recordList.add(record);
    }

    /**
     * 存储明细日志
     *
     * @param callerInfo callerInfo
     * @param parentId   主表id
     * @param iLogType   日志枚举类
     * @param args       日志参数
     */
    public void persistLogForDetail(CallerInfo callerInfo, Long parentId, ILogType iLogType, List<Object[]> args) {
        persistLog(callerInfo, Collections.singletonList(parentId), iLogType, args);
    }

    /**
     * 记录更新日志（多字段更新，多条记录）
     *
     * @param callerInfo     callerInfo
     * @param newEntity      更新实体类
     * @param iLogType       日志类型
     * @param nameTranslator 字段名翻译器
     */
    public void persistLogForUpdate(CallerInfo callerInfo, BaseEntity newEntity, ILogType iLogType, NameTranslator nameTranslator) {
        if (!(iLog instanceof IExtendLog)) {
            LOGGER.error("使用该方法，需要实现IExtendLog接口！");
            return;
        }
        BaseEntity originEntity = ((IExtendLog)iLog).getBaseDao().findOne(newEntity.getId());
        persistLogForUpdate(callerInfo, originEntity, newEntity, iLogType, nameTranslator);
    }

    /**
     * 记录更新日志（多字段更新，多条记录）
     *
     * @param callerInfo     callerInfo
     * @param originEntity   原实体类
     * @param newEntity      更新实体类
     * @param iLogType       日志类型
     * @param nameTranslator 字段名翻译器
     */
    public void persistLogForUpdate(CallerInfo callerInfo, BaseEntity originEntity, BaseEntity newEntity, ILogType iLogType, NameTranslator nameTranslator) {
        List<UpdateUnit> updateUnits = VoUtil.checkUpdatedColumn(originEntity, newEntity, nameTranslator);
        List<Object[]> args = getUpdateColumnStringList(callerInfo, updateUnits);
        persistLogForDetail(callerInfo, originEntity.getId(), iLogType, args);
    }

    /**
     * 主表更新
     * 批量记录更新日志（多字段更新，一条记录，一条记录一条日志）
     *
     * @param callerInfo     callerInfo
     * @param newEntities    更新实体类集合
     * @param iLogType       日志类型
     * @param nameTranslator 字段名翻译器
     */
    public void persistLogForBatchUpdate(CallerInfo callerInfo, List<? extends BaseEntity> newEntities, IComplexLogType iLogType, NameTranslator nameTranslator) {
        if (!(iLog instanceof IExtendLog)) {
            LOGGER.error("使用该方法，需要实现IExtendLog接口！");
            return;
        }
        Set<Long> ids = newEntities.stream().map(BaseEntity::getId).collect(toSet());
        Map<Long, ? extends BaseEntity> originEntities = ((IExtendLog)iLog).getBaseDao().findByIds(ids);
        persistLogForBatchUpdate(callerInfo, originEntities, newEntities, iLogType, nameTranslator);
    }

    /**
     * 主表更新
     * 批量记录更新日志（多字段更新，一条记录，一条记录一条日志）
     *
     * @param callerInfo     callerInfo
     * @param originEntities 原实体类集合
     * @param newEntities    更新实体类集合
     * @param iLogType       日志类型
     * @param nameTranslator 字段名翻译器
     */
    public void persistLogForBatchUpdate(CallerInfo callerInfo, Map<Long, ? extends BaseEntity> originEntities, List<? extends BaseEntity> newEntities, ILogType iLogType, NameTranslator nameTranslator) {
        List<Object[]> args = new ArrayList<>();
        List<Long> primaryIds = new ArrayList<>();
        for (BaseEntity baseEntity : newEntities) {
            BaseEntity originBaseEntity = originEntities.get(baseEntity.getId());
            List<UpdateUnit> updateUnits = VoUtil.checkUpdatedColumn(originBaseEntity, baseEntity, nameTranslator);
            String updateColumnString = getUpdateColumnString(callerInfo, updateUnits);
            if (!StringUtils.isEmpty(updateColumnString)) {
                primaryIds.add(baseEntity.getId());
                args.add(new Object[]{updateColumnString});
            }
        }
        persistLog(callerInfo, primaryIds, iLogType, args);
    }

    /**
     * 根据UpdateUnit集合，获取更新字段的字符串描述（多字段更新，一条记录）
     *
     * @param callerInfo  callerInfo
     * @param updateUnits 更新字段元数据
     * @return 更新字段的字符串描述
     */
    public String getUpdateColumnString(CallerInfo callerInfo, List<UpdateUnit> updateUnits) {

        //增加字典方法级缓存，防止重复查询
        Map<String, Map<String, String>> dictCache = new HashMap<>(updateUnits.size());

        StringBuilder updateColumnArg = new StringBuilder();
        for (UpdateUnit updateUnit : updateUnits) {
            if (updateUnit.getDictType() != null) {
                if (updateUnit.getOriginValue() != null) {
                    updateUnit.setOriginValue(translateAndCache(callerInfo.getTenantId(), new TranslateUnit(updateUnit.getOriginValue().toString(), updateUnit.getDictType()), dictCache));
                }
                updateUnit.setNewValue(translateAndCache(callerInfo.getTenantId(), new TranslateUnit(updateUnit.getNewValue().toString(), updateUnit.getDictType()), dictCache));
            }
            updateColumnArg.append(updateUnit.toString());
        }
        return updateColumnArg.toString();
    }

    /**
     * 根据UpdateUnit集合，获取更新字段的字符串描述（多字段更新，多条记录）
     *
     * @param callerInfo  callerInfo
     * @param updateUnits 更新字段元数据
     * @return 更新字段的字符串描述
     */
    public List<Object[]> getUpdateColumnStringList(CallerInfo callerInfo, List<UpdateUnit> updateUnits) {

        //增加字典方法级缓存，防止重复查询
        Map<String, Map<String, String>> dictCache = new HashMap<>(updateUnits.size());

        List<Object[]> updateColumnArg = new ArrayList<>();
        for (UpdateUnit updateUnit : updateUnits) {
            if (updateUnit.getDictType() != null) {
                if (updateUnit.getOriginValue() != null) {
                    updateUnit.setOriginValue(translateAndCache(callerInfo.getTenantId(), new TranslateUnit(updateUnit.getOriginValue().toString(), updateUnit.getDictType()), dictCache));
                }
                updateUnit.setNewValue(translateAndCache(callerInfo.getTenantId(), new TranslateUnit(updateUnit.getNewValue().toString(), updateUnit.getDictType()), dictCache));
            }
            updateColumnArg.add(new Object[]{updateUnit.toString()});
        }
        return updateColumnArg;
    }

    /**
     * 翻译(加缓存)
     *
     * @param tenantId      租户id
     * @param translateUnit 需要翻译的元数据
     * @param cacheMap      字典缓存
     * @return 翻译值
     */
    private Object translateAndCache(Long tenantId, TranslateUnit translateUnit, Map<String, Map<String, String>> cacheMap) {

        if (!(iLog instanceof ITranslator)) {
            LOGGER.error("未配置翻译器，无法进行翻译！");
            return translateUnit.getCode();
        }

        TranslatorService translatorService = ((ITranslator) iLog).getTranslatorService();

        String originValue = translateUnit.getCode();
        DictType dictType = translateUnit.getDictType();

        if (cacheMap.get(dictType.getName()) != null && !StringUtils.isEmpty(cacheMap.get(dictType.getName()).get(originValue))) {
            return cacheMap.get(dictType.getName()).get(originValue);
        }
        String translateValue = translatorService.translate(tenantId, translateUnit);

        Map<String, String> innerMap;
        if (cacheMap.get(dictType.getName()) == null) {
            innerMap = new HashMap<>(16);
            cacheMap.put(dictType.getName(), innerMap);
        } else {
            innerMap = cacheMap.get(dictType.getName());
        }
        innerMap.put(originValue, translateValue);
        return translateValue;
    }

    /**
     * 翻译日志参数
     *
     * @param tenantId 租户id
     * @param args     日志参数集合
     */
    private void checkAndTranslate(Long tenantId, List<Object[]> args) {

        if (args == null) {
            return;
        }

        //增加字典方法级缓存，防止重复查询
        Map<String, Map<String, String>> dictCache = new HashMap<>(args.size());

        for (Object[] arg : args) {
            for (int i = 0; i < arg.length; i++) {
                if (arg[i] != null && arg[i] instanceof TranslateUnit) {
                    arg[i] = translateAndCache(tenantId, (TranslateUnit) arg[i], dictCache);
                }
            }
        }
    }

    private String getPersistLongInvoker() {

        //获取调用方法名,如果上一级是persistLog,再向上
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
        int upperMethodIndex = 2;
        String actionPath = eles[2].getMethodName();
        while (actionPath.toLowerCase().contains(LOG_METHOD_NAME.toLowerCase())
                || actionPath.toLowerCase().contains(INVOKE_METHOD_NAME.toLowerCase())
                || actionPath.toLowerCase().contains(PROCEED_METHOD_NAME.toLowerCase())
                || actionPath.toLowerCase().contains(INTERCEPT_METHOD_NAME.toLowerCase())) {
            upperMethodIndex++;
            actionPath = eles[upperMethodIndex].getMethodName();
        }
        return actionPath;
    }

}
