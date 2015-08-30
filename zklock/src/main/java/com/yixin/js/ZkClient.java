package com.yixin.js;

import org.apache.zookeeper.ZooKeeper;

public class ZkClient {
	private SessionWatcher sessionWatcher;
	public SessionWatcher getSessionWatcher() {
		return sessionWatcher;
	}
	public void setSessionWatcher(SessionWatcher sessionWatcher) {
		this.sessionWatcher = sessionWatcher;
	}
	public ZooKeeper getZookeeper() {
		return zookeeper;
	}
	public void setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
	private ZooKeeper zookeeper;
	
	public ZkClient(SessionWatcher sessionWatcher,ZooKeeper zookeeper){
		this.sessionWatcher=sessionWatcher;
		this.zookeeper=zookeeper;
		sessionWatcher.setZookeeper(zookeeper);
	}
	public ZooKeeper getZk(){
		return this.getSessionWatcher().getZooKeeper();
	}
}
