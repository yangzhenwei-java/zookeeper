package com.beebank.lock;


import java.io.UnsupportedEncodingException;

import org.apache.zookeeper.KeeperException;

import com.beebank.ConnectWatcher;

public class Consumer   extends ConnectWatcher{
	

	public String consumer(String path){
		String s = "";
		
		try {
			byte[] data = zk.getData(path, null, null);
			s  = new String(data,"UTF-8");
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Thread.currentThread().getName()+"生产:" +s;	
	}
	
	public String simpleConsumer(){
		long l = Product.ATOMIC.get();
		System.out.println(Thread.currentThread().getName()+"消费:"+l);
		return l +"";
	}

}
