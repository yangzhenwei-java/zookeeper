package com.yixin.js;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ThreadTEST{

	public static ArrayBlockingQueue<Runnable> arrayBlockQueue = new ArrayBlockingQueue<Runnable>(100);
	private static ExecutorService lockPool = new ThreadPoolExecutor(5, 20, 15, TimeUnit.SECONDS, arrayBlockQueue);
	private static  ZooKeeper zk;
	private static  void initZk() throws IOException{
		zk = new ZooKeeper("10.100.142.30:2181",30000,null);
	}
	
	public static void main(String[] args) {
		try {
			initZk();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i =0;i<4;i++){
			lockPool.execute(new Runnable(){
				@Override
				public void run() {
					OrderLock ol = null;

				try {
					ol = new OrderLock( "10.100.142.30:2181","123",zk);
				//		Dlock ol = new Dlock( "10.100.142.30:2181","123");
						//DistributedLock ol = new DistributedLock( "10.100.142.30:2181","123");
						ol.lock();
						System.out.println( Thread.currentThread().getId()+"GET LOCK TIME::" + System.currentTimeMillis()/1000);
						Thread.sleep(2000);
						
						System.out.println( Thread.currentThread().getId() +"UNLOCK TIME::" + System.currentTimeMillis()/1000);
					} catch (InterruptedException | IOException |KeeperException e) {
//						 | IOException | KeeperException 
						e.printStackTrace();
					}finally{
						ol.unlock();
					}
			}
			});
		}
	}
	
}
