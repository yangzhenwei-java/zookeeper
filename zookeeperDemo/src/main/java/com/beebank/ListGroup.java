package com.beebank;

import java.util.List;

import org.apache.zookeeper.KeeperException;

public class ListGroup extends ConnectWatcher {
	
	public List<String> list(String groupName) throws KeeperException, InterruptedException{
		List<String> children = zk.getChildren("/"+groupName, true);
		return  children;
	}

}
