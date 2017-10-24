package com.tgenie.dal.advise;


import com.tgenie.dal.annotation.DistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁的拦截器
 *
 * @author Tony
 * @date 17/05/11
 */
@Component
@Aspect
public class DistributedLockAdvice {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLockAdvice.class);
    
    private static final String LOCK_PREFIX = "lock_";
    
    private static final long DEFAULT_EXPIRE_TIME = 30L; 
    
    private static final long DEFAULT_METHOD_EXPIRE_TIME = 30L;  
    
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    
    @Reference(version = "0.1")
    private KVCacher kvCacher;
    
    /**
     * 定义PointCut
     */
    @Pointcut("@annotation(com.tgenie.dal.annotation.DistributedLock)")
    private void aspectjMethod() {
    }

    /**
     * 环绕实际业务方法前后的处理(与before + after类似)
     *
     * @param pjp           切面
     * @return              原方法返回值
     * @throws Throwable    任意异常
     */
	@Around(value = "aspectjMethod()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
    	
        String methodName = pjp.getSignature().getName();
        Class<?> targetClass = pjp.getTarget().getClass();
        Class<?>[] paraTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();

        Method method = targetClass.getMethod(methodName, paraTypes);
        LOGGER.info("拦截分布锁方法:{},参数列表:{}", methodName, pjp.getArgs());

        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        String prefix = annotation.prefix();

        String lock = this.getMsiLockKey(prefix, targetClass.getSimpleName(), method.getName());
        System.out.println(lock);

        try {
            if (!tryLock(lock, DEFAULT_METHOD_EXPIRE_TIME, TIME_UNIT)) {
                LOGGER.warn("获取分布锁[{}]超时", lock);
                return null;
            }
            return pjp.proceed(pjp.getArgs());
        } finally {
            //释放并发锁
            LOGGER.debug("释放并发锁[{}]", lock);
            while(!kvCacher.del(lock)) {
				Thread.sleep(500);
            }
        }
    }
    
    
    /** 
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false 
     * @author http://blog.csdn.net/java2000_wl 
     * @param key       redisKey
     * @param timeout   过期时间
     * @param unit      过期时间单位
     * @return          是否成功获取锁
     */
    private boolean tryLock(String key, long timeout, TimeUnit unit) {
		long nano = System.nanoTime();
		do {
			LOGGER.debug("try lock key: " + key);
			Boolean isGetLock = kvCacher.setIfAbsent(key, key);
			if (isGetLock) {
				kvCacher.expire(key, DEFAULT_EXPIRE_TIME, TIME_UNIT);
				LOGGER.debug("get lock, key: " + key + " , expire in " + DEFAULT_EXPIRE_TIME + " seconds.");
				return Boolean.TRUE;
			} else { // 存在锁
				if (LOGGER.isDebugEnabled()) {
					String desc = kvCacher.get(key);
					LOGGER.debug("key: " + key + " locked by another business：" + desc);
				}
			}
			if (timeout == 0) {
				break;
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				LOGGER.info("获取分布锁休眠时出现异常:{}", e);
			}
		} while ((System.nanoTime() - nano) < unit.toNanos(timeout));
		return Boolean.FALSE;
    } 
    
    private String getMsiLockKey(String prefix, String className, String invokedMethod) {
        return LOCK_PREFIX + prefix + "_" + className + "_" + invokedMethod;
    }
}
