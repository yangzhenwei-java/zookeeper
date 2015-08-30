package com.beebank;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.junit.Test;

public class JoinGroupTest {

	
	private String HOST = "server1,server2,server3";
	
	@Test
	public void join1Test() throws IOException, InterruptedException, KeeperException{
		JoinGroup member = new JoinGroup();
		member.connect(HOST);
		member.join("createGroup", "JoinGroup1");
		System.in.read();
	}
	
	
	@Test
	public void join2Test() throws IOException, InterruptedException, KeeperException{
		JoinGroup member = new JoinGroup();
		member.connect(HOST);
		member.join("createGroup", "JoinGroup2");
		System.in.read();
	}
	
	@Test
	public void join3Test() throws IOException, InterruptedException, KeeperException{
		JoinGroup member = new JoinGroup();
		member.connect(HOST);
		member.join("createGroup", "JoinGroup3");
		System.in.read();
	}
	
	@Test
	public void listMumberTest() throws IOException, InterruptedException, KeeperException{
		ListGroup group = new ListGroup();
		
		group.connect("server1");
		
		List<String> list = group.list("createGroup");
		for(String path:list){
			System.out.println(path);
		}
//		System.in.read();
		group.closed();
		
	}
}
