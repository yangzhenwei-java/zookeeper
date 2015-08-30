package com.beebank.config;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.beebank.Contants;

public class ConfigWatcher implements Watcher {

	
	private ActiveKeyValueStore store;
	
	public ConfigWatcher (String host) throws IOException, InterruptedException{
		store = new ActiveKeyValueStore();
		store.connect(host);
	}
	
	public void process(WatchedEvent event) {
		if(event.getType()== EventType.NodeDataChanged){
			displayConfig();
		}else{
			System.out.println("数据没有发生改变，而是"+event.getType()+":"+event.getState());
		}

	}
	
	public void displayConfig(){
		try {
			String value = store.read(ConfigUpdater.CONFIG, this);
			
			System.out.printf("取出值 %s from %s\n",ConfigUpdater.CONFIG,value);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
