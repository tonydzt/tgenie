package com.tgenie.common.util.log;

import com.ejlerp.dal.framework.dao.BaseDao;
import com.ejlerp.dal.framework.domain.BaseEntity;

/**
 * @author dzt
 * @date 17/11/23
 * Hope you know what you have done
 */
public interface IExtendLog extends ILog {

    /**
     * 返回BaseDao
     * @return BaseDao
     */
    BaseDao<? extends BaseEntity> getBaseDao();
}
