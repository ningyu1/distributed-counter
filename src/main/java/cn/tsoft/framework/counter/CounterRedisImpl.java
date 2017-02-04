/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: CounterRedisImpl.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.counter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.tsoft.framework.lock.Lock;
import cn.tsoft.framework.redis.client.RedisClient;

/**
 * 计数器 redis实现<br> 
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CounterRedisImpl implements Counter  {
    
    private final Logger logger = LoggerFactory.getLogger(CounterRedisImpl.class);
    
    private Lock lock;
     
    private RedisClient redisClient;
	
	private MergeDBCallBack mergeDBCallBack = new DefaultMergeDBCallBack();

    public void setLock(Lock lock) {
        this.lock = lock;
    }

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

	public void setMergeDBCallBack(MergeDBCallBack mergeDBCallBack) {
		this.mergeDBCallBack = mergeDBCallBack;
	}

	/* (non-Javadoc)
     * @see com.saic.ebiz.mms.common.util.counter.Counter#increase(java.lang.String, int)
     */
    @Override
    public boolean increase(final String key, final long span, final long threshold) {
		final String countKey = genereateKey(key);
		return lock.lock(countKey, 100, 30, new DefaultLockCallBack<Boolean>(false, false) {
			@Override
			public Boolean handleObtainLock() {
				String value = redisClient.get(countKey);
				// 如果为空，返回false
				if (value != null) {
					Long currentCount = Long.valueOf(value);
					if (currentCount >= threshold) {
						return false;
					}
				}
				redisClient.incrBy(countKey, span);
				mergeDBRedisCount(countKey, value, span, true);
				return true;
			}
		});
    }

    

    @Override
    public boolean decrease(final String key, final long span) {
		final String countKey = genereateKey(key);
		return lock.lock(countKey, 100, 30, new DefaultLockCallBack<Boolean>(false, false) {
			@Override
			public Boolean handleObtainLock() {
				String value = redisClient.get(countKey);
				// 如果为空，返回false
				if (value == null) {
					return false;
				}
				Long currentCount = Long.valueOf(value);
				if (currentCount - span < 0) {
					return false;
				} else {
					redisClient.decrBy(countKey, span);
					mergeDBRedisCount(countKey, value, span, false);
					return true;
				}
			}
		});
    }
    private String genereateKey(String key){
		return "COUNT_" + key;
    }

    /* (non-Javadoc)
     * @see com.saic.ebiz.mms.common.util.counter.Counter#getCount(java.lang.String)
     */
    @Override
    public long getCount(String key) {
		final String countKey = genereateKey(key);
		String value = redisClient.get(countKey);
		Long currentCount = 0l;
		try {
			// 如果为空，返回false
			if (value == null) {
				Long dbRecord = getDBRedisCount(countKey);
				if (null == dbRecord) {
					return 0l;
				} else {
					currentCount = dbRecord.longValue();
				}
			} else {
				currentCount = Long.valueOf(value);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.info("CounterRedisImpl.getCount, Error key ={},value={}",
					countKey, value);
		}

		return currentCount;
    }

    private Long getDBRedisCount(String key){
		Long dbRecord = mergeDBCallBack.get(key);
		if (null == dbRecord) {
			return null;
		} else {
			return dbRecord;
		}
    }
    
	private boolean mergeDBRedisCount(String key, String value, long span, boolean isIncrease) {
		Long dbRecord = mergeDBCallBack.get(key);
		Long currentCount = 0L;
		if (!StringUtils.isEmpty(value)) {
			currentCount = Long.valueOf(value);
		}
		if (null == dbRecord) {// 新增
			if (isIncrease) {
				long newValue = currentCount + span;
				return mergeDBCallBack.insert(key, newValue);
			} else {
				long newValue = currentCount - span;
				return mergeDBCallBack.insert(key, newValue);
			}
		} else {// 修改
			if (isIncrease) {
				long newValue = currentCount + span;
				return mergeDBCallBack.update(key, newValue);
			} else {
				long newValue = currentCount - span;
				return mergeDBCallBack.update(key, newValue);
			}
		}
    }
}
