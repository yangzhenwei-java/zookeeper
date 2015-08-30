package com.beebank;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class GetChildrenWather implements Watcher {
	
	
	private ZooKeeper zk;
	public GetChildrenWather(ZooKeeper zk){
		this.zk = zk;
	}

	public void process(WatchedEvent event) {
		System.out.println(Contants.ATOMIC.incrementAndGet());
		System.out.println("event.getPath():"+event.getPath());
		System.out.println("event.getState():"+event.getState());
		System.out.println("event.getType():"+event.getType());
		try {
			zk.getChildren(event.getPath(), this);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
