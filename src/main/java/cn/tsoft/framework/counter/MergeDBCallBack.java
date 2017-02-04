/*
 * Copyright (c) 2017, Tsoft and/or its affiliates. All rights reserved.
 * FileName: MergeDBCallBack.java
 * Author:   ningyu
 * Date:     2017年1月18日
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.tsoft.framework.counter;

/**
 * 持久化回调接口 </br>
 * 主要考虑是counter在redis中丢失，因为redis大多定位为cache非持久化 </br>
 * 
 * @author ningyu
 * @date 2017年1月18日 下午5:00:44
 */
public interface MergeDBCallBack {

	/**
	 * 获取计数器值，如果没有返回null </br>
	 * 返回null时会触发 {@link #insert(String, String)}
	 * 
	 * @author ningyu
	 * @date 2017年1月18日 下午5:06:57
	 *
	 * @param key
	 * @return
	 */
	public Long get(String key);
	
	/**
	 * 新增计数器值
	 * 
	 * @author ningyu
	 * @date 2017年1月18日 下午5:12:48
	 *
	 * @param key
	 * @param newValue
	 */
	public boolean insert(String key, Long newValue);
	
	/**
	 * 修改计数器值
	 * 
	 * @author ningyu
	 * @date 2017年1月18日 下午5:13:16
	 *
	 * @param key
	 * @param newValue
	 */
	public boolean update(String key, Long newValue);
}


