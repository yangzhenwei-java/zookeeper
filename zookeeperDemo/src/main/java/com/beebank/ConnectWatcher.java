package com.beebank;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooKeeper;

public class ConnectWatcher implements Watcher {

	
	
	private static int SESSION_TIMEOUT=5000;
	//锁存器
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	
	public ZooKeeper zk = null;
	

	
	//连接
	public void connect(String host) throws IOException, InterruptedException{

			zk = new ZooKeeper(host,SESSION_TIMEOUT,this);
			connectedSignal.await();

	}
	
	public void process(WatchedEvent event) {
		System.out.println("事件执行了:"+Contants.ATOMIC.incrementAndGet());
		System.out.println("event.getPath():"+event.getPath());
		System.out.println("event.getState():"+event.getState());
		System.out.println("event.getType():"+event.getType());
		if(event.getState()==KeeperState.SyncConnected){
			connectedSignal.countDown();
		}
		Stat stat=null;
		try {
			stat = zk.exists("/createGroup", true);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(stat!=null){
			System.out.println(stat.getAversion());
			System.out.println(stat.getCtime());
			System.out.println(stat.getCversion());
			System.out.println(stat.getCzxid());
			System.out.println(stat.getDataLength());
			System.out.println(stat.getEphemeralOwner());
			System.out.println(stat.getMtime());
			System.out.println(stat.getMzxid());
			System.out.println(stat.getNumChildren());
			System.out.println(stat.getPzxid());
			System.out.println(stat.getVersion());
		}

	}
	
	public  void closed() throws InterruptedException{
		if(zk!=null){
			zk.close();
		}
	}

	public ZooKeeper getZk() {
		return zk;
	}

	
	
}
