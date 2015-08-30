package com.beebank;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class CreateGroup implements Watcher {
	
	


	private static int SESSION_TIMEOUT=5000;

	
	private ZooKeeper zk = null ;
	
	//锁存器
	private CountDownLatch connectedSignal = new CountDownLatch(1);

	//连接
	public void connect(String host) throws IOException, InterruptedException{

			zk = new ZooKeeper(host,SESSION_TIMEOUT,this);
			connectedSignal.await();

	}
	
	//创建组
	
	public void createGroup(String groupName) throws KeeperException, InterruptedException{

		String path = zk.create("/"+groupName, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.err.println("createGroup返回的路径是:"+path);
	}
	
	//关闭连接
	public void closed() throws InterruptedException{
		if(zk!=null){
			zk.close();
		}
	}
	
	
	public void process(WatchedEvent event) {
		System.out.println(Contants.ATOMIC.incrementAndGet());
		System.out.println("event.getPath():"+event.getPath());
		System.out.println("event.getState():"+event.getState());
		System.out.println("event.getType():"+event.getType());
		if(event.getState()==KeeperState.SyncConnected){
			connectedSignal.countDown();
		}
		
//		if(event.getState()==KeeperState.Disconnected||event.getType()==Watcher.Event.EventType.NodeChildrenChanged){
//			try {
//				zk.exists(event.getPath(),this);
//			} catch (KeeperException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		try {
			List<String> children = getChildren(event.getPath());
			System.out.println(children);
		} catch (KeeperException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Stat stat = exists(event.getPath());
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
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Stat exists(String path) throws KeeperException, InterruptedException{
		return zk.exists("/"+path, this);
	}
	
	
	public List<String> getChildren(String path) throws KeeperException, InterruptedException{
		return zk.getChildren(path,this);
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws KeeperException	
	 */
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		CreateGroup group = new CreateGroup();
		group.connect("server1");
//		group.createGroup(Contants.CREATE_GROUP);
		group.createGroup(Contants.UPDATE_CONFIG_TEST);
//		group.exists("createGroup");
//		group.zk.getChildren("/createGroup", new GetChildrenWather(group.zk));
//		group.getChildren("/createGroup");
//		System.in.read();
		group.closed();
		
	}
}
