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

/**
 * ClassName:DistributedLock <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015�?�?4�?下午5:47:18 <br/>
 * @author debiao.dong
 * @version
 * @since 1.0
 * @see
 */

public class DistributedLock implements Lock,Watcher{
    private ZooKeeper zk;
    private String root = "/locks";//�?
    private String orderId;//竞争资源的标�?
    private String waitNode;//等待前一个锁
    private String myZnode;//当前�?
    private CountDownLatch latch;//计数�?
    private int sessionTimeout = 30000;
    private static String SPLITSTR = "_lock_";
    
    public DistributedLock(String host,String orderId,ZooKeeper zk) throws IOException, KeeperException, InterruptedException{
		this.orderId = orderId;
		this.zk = zk;
	     Stat stat = zk.exists(root, false);
         if(stat == null){
             zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
         }
     	System.out.println(System.currentTimeMillis());
	}
    
    public DistributedLock(String host,String lockName) throws IOException, KeeperException, InterruptedException{
        this.orderId = lockName;
            zk = new ZooKeeper(host, sessionTimeout, this);
            Stat stat = zk.exists(root, false);
            if(stat == null){
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
            }
    }

    public void process(WatchedEvent arg0) {
    	 if  (arg0.getState()  ==  KeeperState.SyncConnected) { 
 	   		latch.countDown(); //  倒数-1 
 	     } 
    }

    public void lock() {
        try{
            if(this.tryLock()){
                System.out.println("+++++++++++++++++++");
                System.out.println("线程  "+Thread.currentThread().getId()+" "+ myZnode + "得到�?!");
                return;
            }else{
                while(!waitForLock()){
					System.out.println(Thread.currentThread().getId() + "  waitForLock::  ");
				};
            }
        }catch(KeeperException e){
            throw new LockException(e);
        }catch(InterruptedException e){
            throw new LockException(e);
        }
    }
    private boolean waitForLock() throws InterruptedException,KeeperException{
        Stat stat = zk.exists(root+"/"+waitNode, true);
        //判断比自己小�?��数的节点是否存在,如果不存在则无需等待�?同时注册监听
        if(stat!=null){
            System.out.println("线程  " + Thread.currentThread().getId() + "等待" + root + "/" +waitNode);
            this.latch = new CountDownLatch(1);
            this.latch.await(sessionTimeout, TimeUnit.MILLISECONDS);
            this.latch = null;
            return checkLock(); 
        }
        return true;
    }
    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    public boolean tryLock() {
        try{
            String splitStr = "_lock_";
            if(orderId.contains(splitStr)){
                throw new LockException("lockName can not contains \\u000B");
            }
            //创建临时子节�?
            myZnode = zk.create(root + "/" + orderId + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(myZnode + "被创建出�?");
            if(checkLock()){
            	return true;
            }
        }catch(KeeperException e){
            throw new LockException(e);
        }catch(InterruptedException e){
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
         //取出�?��lockName的锁
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
         waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
         return false;
	}
//    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
//        try{
//            if(this.tryLock()){
//                return true;
//            }
//            return waitForLock(waitNode, time);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return false;
//        
//    }

    public void unlock() {
        try{
            System.out.println("解锁 " + myZnode);
            zk.delete(myZnode, -1);
            myZnode = null;
            zk.close();
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(KeeperException e){
            e.printStackTrace();
        }
        
    }

    public Condition newCondition() {
        return null;
    }
    

    
    public class LockException extends RuntimeException{
        public static final long serialVersionUID = 1L;
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
	
	 public static void main(String[] args) {
	        String test = "/eslocks/099888_eslock_00099";
	        String result = test.substring(test.lastIndexOf("_")+1);
	        System.out.println(result);
	    }
	    
}

