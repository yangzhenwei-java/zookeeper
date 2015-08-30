package com.beebank.lock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.beebank.Contants;

public class DistributedLock implements Lock ,Watcher{
	
	private ZooKeeper zk;
	
	
	//锁存器
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	
	private String childPath;
	
	private String parentPath;
	
	private Long sessionId;
	
	private static final String SEPARATOR = "_lock_";
	
	private String src;
	
	public DistributedLock(String host,String parentPath,String childPath){
		this.childPath = childPath;
		this.parentPath = parentPath;
		try {
			zk = new ZooKeeper(host,Contants.SESSION_TIMEOUT,this);
			connectedSignal.await();
			sessionId = zk.getSessionId();
			createPersistentZnode(parentPath);
			createEphemeralZnode(childPath);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		}
	}

	private void createPersistentZnode(String parentPath) throws KeeperException, InterruptedException{
		if(zk.exists(parentPath, false)!=null){
			return ;
		}
		 zk.create(parentPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	private void  createEphemeralZnode(String childPath) throws KeeperException, InterruptedException{
		  src = zk.create(parentPath+childPath+SEPARATOR+sessionId, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}
	
	
	public void lock() {
		try {
			List<String> childrens = zk.getChildren(parentPath, this);
			List<String>  samechildrens = new ArrayList<String>();
			for(String child:childrens){
				if(!childPath.equals("/"+child.split(SEPARATOR)[0])){
					continue;
				}
				samechildrens.add(child);
			}
			
			Collections.sort(samechildrens);
			if(src.equals(parentPath+"/"+samechildrens.get(0))){
//				System.out.println("成功获取锁");
			}else{
				lock();
			}
			
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public void unlock() {
		if(zk!=null){
			try {
				zk.close();
			} catch (InterruptedException e) {
				System.out.println("释放出出现异常");
				e.printStackTrace();
			}
		}
		
	}

	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	public void process(WatchedEvent event) {
		
		if(event.getState()==KeeperState.SyncConnected){
			connectedSignal.countDown();
		}
	}

	
	
}
