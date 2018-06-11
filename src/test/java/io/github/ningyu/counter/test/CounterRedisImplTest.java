/*
 * Copyright (c) 2017, Tsoft SCM and/or its affiliates. All rights reserved.
 * FileName: Counter.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package io.github.ningyu.counter.test;

import io.github.ningyu.counter.Counter;
import io.github.ningyu.test.BaseJunitTestWithContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.ningyu.redis.client.IRedisClient;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CounterRedisImplTest extends BaseJunitTestWithContext {
	
	private final Logger logger = LoggerFactory.getLogger(CounterRedisImplTest.class);
	
	private static final String COUNTER_NAMESPACE = "COUNTER";
	
    @Autowired
    Counter counter;
    long count=0;
    
    @Autowired
    IRedisClient redisClient;
    
    private  synchronized void add (){
        count=count+1;
    }
    
    private  synchronized void decrease (){
        count=count-1;
    }

    private  synchronized long get (){
        return count;
    }

    @Test
    public void testIncrease() {
        final String key="TestKey8";
        logger.info("开始执行testIncrease，redis中counter值：{}", counter.getCount(key));
        logger.info("清除redis中counter值");
        redisClient.del("COUNT_"+key, COUNTER_NAMESPACE);
        int poolSize = 20;
        ExecutorService pools = Executors.newFixedThreadPool(20);
        long  begin=System.currentTimeMillis();
        for (int i = 0; i < poolSize; i++) {
            pools.execute(new Runnable() {
                @Override
                public void run() {
                    boolean result=counter.increase(key, 1l, 7l);
                    logger.info("increase返回值:{}", result);
                    if (result){
                        add();
                    }
                }
            });
        }

        pools.shutdown();
        try {
            pools.awaitTermination(1000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Assert.assertEquals(7l,get());
        long  end=System.currentTimeMillis();
        logger.info("duration:{}", (end - begin));
        logger.info("testIncrease 执行完，redis中counter值：{}", counter.getCount(key));
    }
    
    @Test
    public void testDecrease() {
        final String key="TestKey8";
        count = counter.getCount(key);
        logger.info("开始执行testDecrease，redis中counter值：{}", count);
        if(count <= 0l) {
        	logger.info("redis中counter值<=0，停止执行testDecrease测试用例");
        	return;
        }
        int poolSize = 20;
        ExecutorService pools = Executors.newFixedThreadPool(20);
        long  begin=System.currentTimeMillis();
        for (int i = 0; i < poolSize; i++) {
            pools.execute(new Runnable() {
                @Override
                public void run() {
                    boolean result=counter.decrease(key, 1l);
                    logger.info("decrease返回值:{}", result);
                    if (result){
                    	decrease();
                    }
                }
            });
        }

        pools.shutdown();
        try {
            pools.awaitTermination(1000, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Assert.assertEquals(0l,get());
        long  end=System.currentTimeMillis();
        logger.info("duration:{}", (end - begin));
        logger.info("testDecrease 执行完，redis中counter值：{}", counter.getCount(key));
    }

}
