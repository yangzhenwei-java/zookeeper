package com.beebank;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class DeleteTest {
	
	@Test
	public void delete() throws IOException, InterruptedException, KeeperException{
		DeleteGroup delet = new DeleteGroup();
		delet.connect("server1");
		delet.delete("createGroup");
		delet.closed();
//		System.in.read();
	}

	
	@Test
	public void modify() throws IOException, InterruptedException, KeeperException{
		DeleteGroup delet = new DeleteGroup();
		delet.connect("server1");
		ZooKeeper zk = delet.getZk();
		Stat stat = zk.exists("/createGroup", false);
		zk.setData("/createGroup", "你好".getBytes("utf-8"), stat.getVersion());
		delet.closed();
//		System.in.read();
	}
	
	@Test
	public void lisnter() throws IOException, InterruptedException, KeeperException{
		DeleteGroup delet = new DeleteGroup();
		delet.connect("server1");
//		delet.getZk().exists("/createGroup", true);
//		if(stat==null){
//			return;
//		}
//		System.out.println(stat.getAversion());
//		System.out.println(stat.getCtime());
//		System.out.println(stat.getCversion());
//		System.out.println(stat.getCzxid());
//		System.out.println(stat.getDataLength());
//		System.out.println(stat.getEphemeralOwner());
//		System.out.println(stat.getMtime());
//		System.out.println(stat.getMzxid());
//		System.out.println(stat.getNumChildren());
//		System.out.println(stat.getPzxid());
//		System.out.println(stat.getVersion());
//		delet.delete("createGroup");
//		delet.closed();
		System.in.read();
	}
}
