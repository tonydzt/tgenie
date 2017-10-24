package com.tgenie.dal.annotation;

import java.lang.annotation.*;

/**
 * 分布式锁
 *
 * @author Tony
 * @date 17/05/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DistributedLock {
	String prefix();
}
