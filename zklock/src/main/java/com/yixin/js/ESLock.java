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
 * ClassName:ESLock <br/>
 * Function: 报销单向ES里插入时的zookeeper锁. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年4月16日 下午4:44:20 <br/>
 * @author debiaodong
 * @version
 * @since 1.0
 * @see
 */
public class ESLock implements Lock,Watcher{
    private  String orderId;//报销单id
    private String timeStamp;
    private  ZooKeeper zk;
    private  String root = "/eslock";
    private  int sessionTimeout = 30000;
    private String waitNode;//等待的前一个锁
    private String myZnode;//当前的锁
    private CountDownLatch latch = new CountDownLatch(1);;//������
    private  String SPLITSTR = "_eslock_";
   
    public ESLock(String orderId,String timeStamp,ZooKeeper zk) throws IOException, KeeperException, InterruptedException{
        this.orderId = orderId;
        this.timeStamp = timeStamp;
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
       this.lockEsc();
    }
    
    public void lockEsc(){
    	 try{
             if(orderId.contains(SPLITSTR)){
                 try {
                    throw new Exception("lockName can not contains \\u000B");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
             }
             //创建临时子节点
             myZnode = zk.create(root + "/" + orderId + SPLITSTR+timeStamp, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
             int type = getLock();
             if(type == 0 ){
                 return ;
             }else if(type == 1){
                while(!waitForEsLock()){
                };
                return ;
             }else if(type == -1){
            	 throw new LockException(new Exception("New Big TimeStamp Node Coming!"));
             }
    	 }catch  (KeeperException | InterruptedException|LockException e) {
    		 throw new LockException(new Exception("New Big TimeStamp Node Coming!"));
         }
    	
    }
    
    
    
    private boolean waitForLock() throws KeeperException, InterruptedException{
        Stat stat = zk.exists(root + "/" + waitNode,this);
      //判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
        if(stat != null){
            this.latch = new CountDownLatch(1);
            this.latch.await(sessionTimeout, TimeUnit.MILLISECONDS);
            this.latch = null;
            return checkLock();
        }
        return true;
    }
    
    private boolean waitForEsLock()throws KeeperException, InterruptedException{
        Stat stat = zk.exists(root + "/" + waitNode,this);
        if(stat != null){
            this.latch = new CountDownLatch(1);
            this.latch.await(sessionTimeout, TimeUnit.MILLISECONDS);
            this.latch = null;
            if(getLock()==0){
            	return true;
            }else if(getLock()==1){
            	return false;
            }else if(getLock()==-1){
            	throw new LockException(new Exception("New Big TimeStamp Node Coming!"));
            }
        }
        return true;
    }
    
    //返回检查锁的结果
    private boolean checkLock() throws KeeperException, InterruptedException{
        System.out.println(Thread.currentThread().getId() + "检查ES锁!!");
        List<String> subNodes = zk.getChildren(root, false);
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
            return true;
        }
      //如果不是最小的节点，找到比自己小1的节点
        String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
        waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        System.out.println("这个锁被打扰");
        this.lock();
        
    }

    @Override
    public boolean tryLock() {
        try{
            if(orderId.contains(SPLITSTR)){
                throw new Exception("lockName can not contains \\u000B");
            }
            //创建临时子节点
            myZnode = zk.create(root + "/" + orderId + SPLITSTR+timeStamp, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
            if(checkLock()){
                return true;
            }
        }catch(Exception e){
            throw new LockException(e);
        }
        return false;
        
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
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
    public Condition newCondition() {
        return null;
    }
    
    /**
     * 
    * getLock:此方法用来判断获取锁的条件,如果传入的时间戳刚好是最大的,那么需要等待比它小的节点执行完之后;如果传入的时间戳不是最大的,那么需要删除这个临时节点;
    * 如果只有一个节点,那么直接执行.. <br/>
    * TODO(这里描述这个方法适用条件 – 可选).<br/>
    * TODO(这里描述这个方法的执行流程 – 可选).<br/>
    * TODO(这里描述这个方法的使用方法 – 可选).<br/>
    * TODO(这里描述这个方法的注意事项 – 可选).<br/>
    *
    * @author debiao.dong
    * @param orderId
    * @param timeStamp
    * @return
    * @throws KeeperException
    * @throws InterruptedException
    * @since 1.0
     */
    int getLock() throws KeeperException, InterruptedException{
        int result = 10;
        List<String> subNodes = zk.getChildren(root, false);
        //取出所有lockName的锁
        List<String> lockObjNodes = new ArrayList<String>();
        for (String node : subNodes) {
            String _node = node.split(SPLITSTR)[0];
            if(_node.equals(orderId)){
                lockObjNodes.add(node);
            }
        }
        Collections.sort(lockObjNodes);
        //找到当前最大的节点
        String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
        Long subTimeStamp = Long.valueOf(myZnode.substring(myZnode.lastIndexOf("_")+1));
        Long tStamp = Long.valueOf(timeStamp);
        String maxNode = lockObjNodes.get(lockObjNodes.size()-1);
        if(lockObjNodes.size()==1){
            result = 0;
        } else if(maxNode.equals(subMyZnode)){
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
            result = 1;
        }else if(subTimeStamp.compareTo(tStamp)>0){
            zk.delete(myZnode, -1);
            result = -1;
        }
        return result;
    }
    
}

