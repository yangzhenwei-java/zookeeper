package com.beebank.config;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;

import com.beebank.Contants;

public class ConfigUpdater{
	
	
	public static final String CONFIG = "config";

	private ActiveKeyValueStore store;
	
	private Random  random = new Random();
	public ConfigUpdater(String host) throws IOException, InterruptedException{
		store = new ActiveKeyValueStore();
		store.connect(host);
	}
	
	public void run(){
		while(true){
			String value = random.nextInt(100)+"";
			try {
				store.write(ConfigUpdater.CONFIG, value);
				System.out.printf("存入值：%s to %s\n",ConfigUpdater.CONFIG,value);
				
				TimeUnit.SECONDS.sleep(random.nextInt(10));
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
