package com.beebank.lock;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.zookeeper.KeeperException;

import com.beebank.ConnectWatcher;

public class Product extends ConnectWatcher {

	
	public String  product(String path){
		String s = "";
		try {
			byte[] data = zk.getData(path, null, null);
			if(data==null){
				s="10000";
				zk.setData(path, s.getBytes("UTF-8"), -1);
			}else{
				s = new String(data,"UTF-8");
				AtomicLong ac = new AtomicLong(Long.valueOf(s));
				s = String.valueOf(ac.incrementAndGet());
				zk.setData(path, s.getBytes("UTF-8"), -1);
			}
			return s;
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
		
		return  Thread.currentThread().getName()+"生产:" +s;	
	}
	
	
	public static final AtomicLong ATOMIC = new AtomicLong();
	
	
	public String  simplepProduct(){
		long pro = ATOMIC.incrementAndGet();
		System.out.println(Thread.currentThread().getName()+"生产:"+pro);
		return pro+"";	
	}

}
