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


public class OrderLock implements Lock, Watcher{
	
	private  String orderId;
	private String timeStamp;
    private  ZooKeeper zk;
    private  String root = "/orderlock";
    private  int sessionTimeout = 30000;
    private String waitNode;//�ȴ�ǰһ����
    private String myZnode;//��ǰ��
    private CountDownLatch latch = new CountDownLatch(1);;//������
    private  String SPLITSTR = "_lock_";
	
	public OrderLock(String host,String orderId,ZooKeeper zk) throws IOException, KeeperException, InterruptedException{
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
	   		latch.countDown(); //  ����-1 
	     } 
	    System.out.println("Process call !!!!" + Thread.currentThread().getId() ); 
	}
	
	@Override
	public void lock() {
            if(this.tryLock()){
                System.out.println("Thread " + Thread.currentThread().getId() + " " + myZnode + " get lock true");
                return;
            }
            else{
                try {
					while(!waitForLock()){
					};
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}//�ȴ���
            }
        }
	
	private boolean waitForLock() throws KeeperException, InterruptedException{
		Stat stat = zk.exists(root + "/" + waitNode,this);
		//�жϱ��Լ�Сһ����Ľڵ��Ƿ����,������������ȴ���,ͬʱע�����
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
	            //������ʱ�ӽڵ�
	            myZnode = zk.create(root + "/" + orderId + SPLITSTR, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
	            //ȡ�������ӽڵ�
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
         //ȡ������lockName����
         List<String> lockObjNodes = new ArrayList<String>();
         for (String node : subNodes) {
             String _node = node.split(SPLITSTR)[0];
             if(_node.equals(orderId)){
                 lockObjNodes.add(node);
             }
         }
         Collections.sort(lockObjNodes);
         if(myZnode.equals(root+"/"+lockObjNodes.get(0))){
             return true;
         }
         String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
         waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
         return false;
	}
	@Override
	public void unlock() {
        try {
            System.out.println("unlock " + myZnode);
            zk.delete(myZnode,-1);
            myZnode = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
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
