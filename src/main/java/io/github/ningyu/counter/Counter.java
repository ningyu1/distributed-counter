/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: Counter.java
 * Author:   ningyu
 * Date:     2017年1月11日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package io.github.ningyu.counter;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author ningyu
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface Counter {
    
    /**
     * 加法
     * 
     * @author ningyu
     * @date 2017年1月18日 下午5:33:58
     *
     * @param key
     * @param span
     * @param threshold
     * @return
     */
    public boolean increase(String key, long span, long threshold);

    /**
     * 减法
     * 
     * @author ningyu
     * @date 2017年1月18日 下午5:34:03
     *
     * @param key
     * @param span
     * @return
     */
    public boolean decrease(String key, long span);
    
    /**
     * 获取值
     * 
     * @author ningyu
     * @date 2017年1月18日 下午5:34:09
     *
     * @param key
     * @return
     */
    public long getCount(String key);
    
}
