package com.beebank;

import java.util.List;

import org.apache.zookeeper.KeeperException;

public class DeleteGroup extends ConnectWatcher{

	
	public void delete(String groupName) throws KeeperException, InterruptedException{
		
		List<String> children = zk.getChildren("/"+groupName, true);
		for(String child :children){
			zk.delete("/"+groupName+"/"+child, -1);
		}
		zk.delete("/"+groupName, -1);
	}
}
