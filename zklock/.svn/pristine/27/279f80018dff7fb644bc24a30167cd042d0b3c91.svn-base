package com.yixin.js;

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
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import com.yixin.js.DistributedLock.LockException;


public class Dlock implements Lock, Watcher{
	
	private static String host;
	private static String orderId;
	private String timeStamp;
    private static ZooKeeper zk;
    private static String root = "/orderlock";
    private static int sessionTimeout = 30000;
    private String waitNode;//等待前一个锁
    private String myZnode;//当前锁
    private CountDownLatch latch = new CountDownLatch(1);;//计数器
    private static String SPLITSTR = "_lock_";
    private static int Times = 1;
    
 
	
    
    public Dlock(String host,String lockName) throws IOException, KeeperException, InterruptedException{
    		this.orderId = lockName;
            zk = new ZooKeeper(host, sessionTimeout, this);
            Stat stat = zk.exists(root, false);
            if(stat == null){
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
            }
    }
	public Dlock(String host,String orderId,ZooKeeper zk) throws IOException, KeeperException, InterruptedException{
		this.host=host;
		this.orderId = orderId;
		this.zk = zk;
	     Stat stat = zk.exists(root, false);
         if(stat == null){
             zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
         }
     	System.out.println(System.currentTimeMillis());
	}
	@Override
	public void process(WatchedEvent event) { 
	   	 if  (event.getState()  ==  KeeperState.SyncConnected) { 
	   		latch.countDown(); //  倒数-1 
	     } 
	    System.out.println("Process call !!!!" + Thread.currentThread().getId() ); 
	}
	
	@Override
	public void lock() {
		 try {
		if(this.tryLock()){
                System.out.println("Thread " + Thread.currentThread().getId() + " " + myZnode + " get lock true");
                return;
            }
            else{
               
					while(!waitForLock()){
						System.out.println(Thread.currentThread().getId() + "  waitForLock::  " + Times );
						Times++;
					};
				}
		} catch (KeeperException e) {
					System.out.println(Thread.currentThread().getId()+ "----------------");
					e.printStackTrace();
					System.out.println(Thread.currentThread().getId()+ "----------------");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}//等待锁
            System.out.println(Thread.currentThread().getId() + "   get lock over?");
        }
	
	private boolean waitForLock() throws KeeperException, InterruptedException{
		Stat stat = zk.exists(root + "/" + waitNode,this);
		System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + root + "/" + waitNode);
		//判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
		if(stat != null){
			this.latch = new CountDownLatch(1);
			this.latch.await(sessionTimeout, TimeUnit.MILLISECONDS);
			this.latch = null;
			return checkLock();
		}
		return true;
	}
	
	@Override
	public void lockInterruptibly() throws InterruptedException {
		System.out.println("INTERRUPTY");
		this.lock();
	}

	@Override
	public Condition newCondition() {
		return null;
	}
	public boolean tryLock(){
		try {
	            if(orderId.contains(SPLITSTR)){
	                throw new Exception("lockName can not contains \\u000B");
	            }
	            //创建临时子节点
	            myZnode = zk.create(root + "/" + orderId + SPLITSTR, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
	            System.out.println(Thread.currentThread().getId()  +":::::"+ myZnode + " is created ");
	            //取出所有子节点
	            if(checkLock()){
	            	return true;
	            }
		} catch (Exception e) {
			throw new LockException(e);
		}
	        return false;
	}
	private boolean checkLock() throws  KeeperException, InterruptedException{
		System.out.println(Thread.currentThread().getId() + "CHECK LOCK!!");
		 List<String> subNodes = zk.getChildren(root, false);
		 for(String s : subNodes){
			 System.out.println(Thread.currentThread().getId() + s);
		 }
         //取出所有lockName的锁
         List<String> lockObjNodes = new ArrayList<String>();
         for (String node : subNodes) {
             String _node = node.split(SPLITSTR)[0];
             if(_node.equals(orderId)){
                 lockObjNodes.add(node);
             }
         }
         Collections.sort(lockObjNodes);
         if(myZnode.equals(root+"/"+lockObjNodes.get(0))){
        	 System.out.println(Thread.currentThread().getId()  + "::CHECKRESULT::TRUE" );
             return true;
         }
         System.out.println(Thread.currentThread().getId()  + "::CHECKRESULT::FALSE" );
         String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
         waitNode = lockObjNodes.get(0);
         return false;
	}
	@Override
	public void unlock() {
        try {
            System.out.println("unlock " + myZnode);
            zk.delete(myZnode,-1);
            myZnode = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
          //  e.printStackTrace();
        }
	}
	
    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public LockException(String e){
            super(e);
        }
        public LockException(Exception e){
            super(e);
        }
    }

	@Override
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		return false;
	}
}
