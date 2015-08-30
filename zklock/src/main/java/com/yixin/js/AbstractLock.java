package com.yixin.js;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * ClassName:AbstractLock <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年4月17日 下午2:47:28 <br/>
 * @author debiaodong
 * @version
 * @since 1.0
 * @see
 */
public abstract class AbstractLock implements Lock, Watcher{
    private String timeStamp;
    public ZooKeeper zk;
    public String root = "/orderlock";
    private int sessionTimeout = 30000;
    public String waitNode;// �ȴ�ǰһ����
    public String myZnode;// ��ǰ��
    private CountDownLatch latch = new CountDownLatch(1);;// ������
    public  String SPLITSTR = "_lock_";


    public AbstractLock(ZooKeeper zk) throws IOException, KeeperException, InterruptedException {
        this.zk = zk;
        Stat stat = zk.exists(root, false);
        if (stat == null) {
            zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        System.out.println("当前时间是" + System.currentTimeMillis());
    }
    
    public AbstractLock(){}


    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == KeeperState.SyncConnected) {
            latch.countDown(); // ����-1
        }
        System.out.println("Process call !!!!" + Thread.currentThread().getId());
    }


    @Override
    public void lock() {
        if (this.tryLock()) {
            System.out.println("Thread " + Thread.currentThread().getId() + " " + myZnode + " get lock true");
            return;
        }
        else {
            try {
                while (!waitForLock()) {
                }
                ;
            }
            catch (KeeperException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }// �ȴ���
        }
    }


    private boolean waitForLock() throws KeeperException, InterruptedException {
        Stat stat = zk.exists(root + "/" + waitNode, this);
        if (stat != null) {
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


    @Override
    public void unlock() {
        try {
            System.out.println("unlock " + myZnode);
            zk.delete(myZnode, -1);
            myZnode = null;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;


        public LockException(String e) {
            super(e);
        }


        public LockException(Exception e) {
            super(e);
        }
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    public abstract boolean tryLock();


    public abstract boolean checkLock() throws KeeperException, InterruptedException;

}

