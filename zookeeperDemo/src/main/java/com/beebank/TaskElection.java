package com.beebank;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;

public class TaskElection  {
	
	private static String SPLITSTR = "_NODE_";
	
	private static final CountDownLatch connectedSignal = new CountDownLatch(1);

	public static void main(String[] args) throws IOException, InterruptedException, ParseException, KeeperException {
		final InetAddress netAddress = InetAddress.getLocalHost();
		Thread th = new Thread(new Runnable() {
			
			public void run() {
				JoinGroup group = new JoinGroup();
				try {
					group.connect("server1");
					group.join("createGroup", SPLITSTR+netAddress.getHostAddress()+SPLITSTR);
					connectedSignal.countDown();
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		th.start();
		connectedSignal.await();
		boolean flag = true;
		int dst = new Date().getMinutes()+3;
		while(flag){
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Date parse = format.parse("2015-08-29 20:37:00");
			int minutes = new Date().getMinutes();
			if(minutes!=dst&&minutes<dst){
				continue;
			}
			System.err.println("工作开始了....  ");
			ListGroup list = new ListGroup();
			list.connect("server1");
			List<String> members = list.list("createGroup");
			Collections.sort(members);
			String num = members.get(members.size()-1);
			if(netAddress.getHostAddress().equals(num.split(SPLITSTR)[1])){
				
				System.err.println("============"+netAddress.getHostName()+"被执行了===================");
			}
			list.closed();
			flag=false;
		}
		
		System.err.println("工作任务结束了....  ");
		
	}

}
