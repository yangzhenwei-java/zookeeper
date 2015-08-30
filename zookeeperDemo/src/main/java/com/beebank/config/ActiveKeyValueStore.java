package com.beebank.config;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import com.beebank.ConnectWatcher;
import com.beebank.Contants;

public class ActiveKeyValueStore extends ConnectWatcher {
	
	public void write(String key,String value) throws KeeperException, InterruptedException{
		Stat stat = zk.exists("/"+Contants.UPDATE_CONFIG_TEST+"/"+key, null);
		if(stat==null){
			zk.create("/"+Contants.UPDATE_CONFIG_TEST+"/"+key, value.getBytes(Contants.CHARSET), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
		}else{
			zk.setData("/"+Contants.UPDATE_CONFIG_TEST+"/"+key, value.getBytes(Contants.CHARSET), -1);
		}
		
	}
	

	public String read(String key,Watcher watcher) throws KeeperException, InterruptedException{
		byte[] data = zk.getData("/"+Contants.UPDATE_CONFIG_TEST+"/"+key,watcher, null);
		
		return new String(data,Contants.CHARSET);
	}
}
