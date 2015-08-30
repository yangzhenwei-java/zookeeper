package com.beebank.reconnection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.beebank.ConnectWatcher;

public class ReConnection  extends ConnectWatcher{
	
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		ReConnection rc = new ReConnection();
		
		rc.connect("server1");
		ZooKeeper zk = rc.zk;
		long sessionId = zk.getSessionId();
		
		zk.create("/ReConnection", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		zk.exists("/ReConnection", new Watcher() {
			
			public void process(WatchedEvent event) {
				System.out.println("执行了=========1====="+event.getType()+"==state:"+event.getState());
			}
		});
		
//		zk.close();
		
//		TimeUnit.SECONDS.sleep(1);
		
		Watcher wather = new Watcher() {
			
			public void process(WatchedEvent event) {
				System.out.println("执行了=========2====="+event.getType()+"==state:"+event.getState());
			}
		};
		
		ZooKeeper zk1 = new ZooKeeper("server1", 5000, wather, sessionId, null);
		
		byte[] data = zk1.getData("/ReConnection", null, null);
		
		System.out.println(new String(data,"UTF-8"));
	}

}
