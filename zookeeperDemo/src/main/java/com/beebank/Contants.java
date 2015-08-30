package com.beebank;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

public class Contants {

	public static AtomicInteger ATOMIC = new AtomicInteger();
	
	/**
	 *  组
	 */
	public static final String CREATE_GROUP = "createGroup";
	
	public static final int SESSION_TIMEOUT=5000;
	
	/**
	 * 配置更新设置
	 */
	public static final String UPDATE_CONFIG_TEST = "UPDATECONFIGTEST";
	
	public static final Charset CHARSET = Charset.forName("UTF-8");
	
}
