package com.beebank.config;

import java.io.IOException;

import org.junit.Test;

public class ConfigTest {
	
	/**
	 * 取配置信息
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void updaterTest() throws IOException, InterruptedException{
		ConfigUpdater updater = new ConfigUpdater("server1");
		
		updater.run();
	}
	
	
	/**
	 * 
	 * 读配置信息
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void watcherTest() throws IOException, InterruptedException{
		ConfigWatcher watcher = new ConfigWatcher("server1");
		
		watcher.displayConfig();
		
		Thread.sleep(Long.MAX_VALUE);
	}

}
