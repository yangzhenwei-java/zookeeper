package com.beebank;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class JoinGroup extends ConnectWatcher {
	
	// 创建加入组
	public void join(String groupName, String memberName) throws KeeperException, InterruptedException{
		
		String path = "/" + groupName + "/" + memberName;
		
		String createPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		
		System.err.println("zk.create:"+createPath);
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		for(int i= 0;i<=10;i++){
			JoinGroup member = new JoinGroup();
			member.connect("server1");
			member.join("createGroup", i+"JoinGroup");
		}
	}

}
