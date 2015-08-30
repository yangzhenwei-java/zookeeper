package com.yixin.js;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class SessionWatcher implements Watcher{

	 private String host = "10.100.142.30:2181";
	 private ZooKeeper zookeeper;
	 private CountDownLatch latch;
	 
	@Override
	public void process(WatchedEvent event) {
		 if (event.getState() == KeeperState.SyncConnected) {
	            if (latch != null) {
	                latch.countDown();
	            }
	        } else if (event.getState() == KeeperState.Expired) {
	            System.out.println("[SUC-CORE] session expired. now rebuilding");
	            //session expired, may be never happending.
	            //close old client and rebuild new client
	            close();
	            getZooKeeper();
	        }
	}
	
	public  ZooKeeper getZooKeeper() {
		  if (zookeeper == null) {
		    synchronized (ZooKeeper.class) {
		      if (zookeeper == null) {
		        latch = new CountDownLatch(1);
		        zookeeper = buildClient();
		        try {
		           latch.await(30, TimeUnit.SECONDS);
		        } catch (InterruptedException e) {
		           e.printStackTrace();
		        } finally {
		          latch = null;
		        }
		      }
		    }
		  }
		  return zookeeper;
		}
	
	private  ZooKeeper buildClient() {
	    try {
	        return new ZooKeeper(host, 30000, this);
	    } catch (IOException e) {
	        throw new RuntimeException("init zookeeper fail.", e);
	    }
	}
	public  void close() {
	    System.out.println("[SUC-CORE] close");
	    if (zookeeper != null) {
	        try {
	            zookeeper.close();
	            zookeeper = null;
	        } catch (InterruptedException e) {
	            //ignore exception
	        }
	    }
	}
	public String getZip() {
		return host;
	}

	public void setZip(String zip) {
		this.host = zip;
	}
	public static void main(String[] args) throws Exception {
		SessionWatcher sw = new SessionWatcher();
		  ZooKeeper zk = new  ZooKeeper("10.100.142.30:2181", 30000, sw);
		  long sessionId = zk.getSessionId();
		  //
		  System.out.println(sessionId);
		  zk.close();
		  Thread.sleep(10000L);
		  ZooKeeper zk1 = sw.getZooKeeper();
		  System.out.println(zk1.getSessionId());
		  //
		  
		  // rebuild a new session
		  // check the new session

		  // close the client
		}

    public ZooKeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(ZooKeeper zookeeper) {
        this.zookeeper = zookeeper;
    }
}
