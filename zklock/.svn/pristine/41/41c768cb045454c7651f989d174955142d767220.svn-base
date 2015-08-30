package com.yixin.js;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.ConnectionBean;
 
 
public class Test implements Watcher {
    private static final int SESSION_TIMEOUT = 5000;
     
    private static ZooKeeper zk;
    private static CountDownLatch connectedSignal = new CountDownLatch(1);
    Childwatcher childwatcher = new Childwatcher();
    private static Integer mutex;
	private String host;
	private String orderId;
	private static String root = "/root";
    
    private void getthreadname(String funname){
        Thread current = Thread.currentThread(); 
        System.out.println(funname + " is call in  " + current.getName());
    }
    
    
    public Test(String host,String orderId) throws Exception{
    	this.host=host;
		this.orderId = orderId;
		zk = new ZooKeeper(host, 30000, this); 
		 Stat stat = zk.exists(root + "/" + orderId, false);
         if(stat == null){
             zk.create(root + "/" + orderId, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL); 
         }
         System.out.println("Success");
    }
    
    
    
    
    
     
    @Override
    // 运行在另外一个线程 main-EventThread
    public void process(WatchedEvent event) { 
   	 if  (event.getState()  ==  KeeperState.SyncConnected) { 
         connectedSignal.countDown(); //  倒数-1 
         System.out.println("caicai");
     } 
    System.out.println("已经触发了" + event.getType() + "事件111！"); 
    } 

    
    synchronized   public   void  process1(WatchedEvent event) { 
        //  此处设立在Watch中会在状态变化后触发事件 
        if  (event.getState()  ==  KeeperState.SyncConnected) { 
           connectedSignal.countDown(); //  倒数-1 
       } 
  
            synchronized  (mutex) { 
                // System.out.println("Process: " + event.getType()); 
               mutex.notify(); 
           } 
   }
     
    // 测试自定义监听
    public Watcher wh = new Watcher() {
        public void process(WatchedEvent event) {
            getthreadname("Watcher::process");
            System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型："
            + event.getType());
        }
    };
     
    public void connect(String hosts) throws IOException, InterruptedException{
        getthreadname("connect");
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, wh); // 最后一个参数用this会调用自身的监听  wh 代表？
        connectedSignal.await(); // 主线程挂起
    }
     
    // 加入组(可以理解成一个创建本次连接的一个组)
    public void join(String groupname, String meberName) throws KeeperException, InterruptedException{
         String path = "/" + groupname + "/" + meberName ;
         
         
        // EPHEMERAL断开将被删除
        String createdpath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("join : " + createdpath);
    }
     
    public List<String> getChilds(String path) throws KeeperException, InterruptedException { 
        // TODO Auto-generated method stub 
        if (zk != null) { 
            // return zk.getChildren(path, false); // false表示没有设置观察
            //zk.getChildren(path, true, childwatcher.processResult, null);
            zk.getChildren( path, true, new AsyncCallback.Children2Callback() {
                @Override
                public void processResult(int rc, String path, Object ctx,
                        List<String> children, Stat stat) { // stat参数代表元数据
                    // TODO Auto-generated method stub
                    System.out.println("****");
                    for (int i = 0; i < children.size() - 1; i ++){
                        System.out.println("mempath = " + children.get(i) + stat);
                    }  
                }
    }, null);
             
        } 
        return null; 
    } 
     
    public void create(String path) throws KeeperException, InterruptedException, IOException{
        // Ids.OPEN_ACL_UNSAFE 开放式ACL，允许任何客户端对znode进行读写
        // CreateMode.PERSISTENT 持久的znode，本次连接断开后还会存在，应该有持久化操作.
        // PERSISTENT_SEQUENTIAL 持久化顺序，这个由zookeeper来保证
         
        String createdpath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("crateed : " + createdpath);   
    }
     
    private void close() throws InterruptedException{
        if  (zk != null){
            zk.close();
        }
    }
     
    public static class Childwatcher implements Children2Callback{
        public ChildrenCallback processResult;
 
        @Override
        public void processResult(int rc, String path, Object ctx,
                List<String> children, Stat stat) {
            // TODO Auto-generated method stub
            System.out.println("**** path " + stat);
        }
    }
     
    public void delete(String groupname) throws InterruptedException, KeeperException{     
        zk.delete(groupname, -1);
    }
     
    public Stat isexist(String groupname) throws InterruptedException, KeeperException{    
        return zk.exists(groupname, true); // this
    }
     
     public void write(String path, String value)throws Exception{ 
         Stat stat=zk.exists(path, false); 
         if(stat==null){ 
             zk.create(path, value.getBytes("UTF-8"), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); 
         }else{ 
                
             zk.setData(path, value.getBytes("UTF-8"), -1); 
         }     
     } 
      
     public String read(String path,Watcher watch)throws Exception{ 
         byte[] data=zk.getData(path, watch, null);            
         return new String(data, "UTF-8"); 
     } 
 
    public static void main(String[] args) throws Exception {
    	 String hosts = "10.100.142.30:2181";
    	 Test t = new Test(hosts,"123");
    	 String test  = zk.create("/root/asddsA", new byte[]{'A'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
    	 System.out.println(test);
//    	 States states =  zk.getState();
    	 List<String> list = new ArrayList<String>();
    	 String myZnode1 =  zk.create(root + "/" + t.orderId , new byte[]{'A'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
//    	 String myZnode2 =  zk.create("/root/a+", new byte[]{'B'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
//    	 String myZnode3 =  zk.create("/root/a+", new byte[]{'C'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
//    	 String myZnode4 =  zk.create("/root/a+", new byte[]{'D'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
//    	 String myZnode5 =  zk.create("/root/a+", new byte[]{'E'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
//    	 String myZnode6 =  zk.create("/root/a+", new byte[]{'F'}, ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
    	 List<String>nodes= zk.getChildren("/root", false);
//    	Collections.sort(nodes);
    	for(String s : nodes){
    		System.out.println(s);
    		System.out.println(new String (zk.getData("/root/" + s, false, null)));
    	}
//    	String subMyZnode = myZnode3.substring(myZnode1.lastIndexOf("/") + 1);
//    	System.out.println(subMyZnode);
//    	int index = Collections.binarySearch(nodes, subMyZnode);
//    	System.out.println(index);
//    	String  waitNode = nodes.get(Collections.binarySearch(nodes, subMyZnode) - 1);
//    	System.out.println(waitNode);
//    
//    	zk.delete("/root", -1); 
//    	System.out.println("asddwdsasd");
////    	List<String> nodes1= zk.getChildren("/root", false);
////    	for(String s : nodes1){
////    		System.out.println(s);
////    		System.out.println(new String (zk.getData("/root/" + s, false, null)));
////    	}
////    	 System.out.println(myZnode1 + "   " + myZnode2);
	}
    public static void main2(String[] args) throws Exception {
    	String hosts = "10.100.142.30:2181";
    	 // 创建一个与服务器的连接
    	 ZooKeeper zk = new ZooKeeper(hosts,5000, new Watcher() { 
    	            // 监控所有被触发的事件
    	            public void process(WatchedEvent event) { 
    	            	 if  (event.getState()  ==  KeeperState.SyncConnected) { 
    	                     connectedSignal.countDown(); //  倒数-1 
    	                     System.out.println("caicai");
    	                 } 
    	                System.out.println("已经触发了" + event.getType() + "事件111！"); 
    	            } 
    	        }); 
    	   connectedSignal.await(); // 主线程挂起
    	 // 创建一个目录节点
    	 zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
    	   CreateMode.PERSISTENT); 
    	 // 创建一个子目录节点
    	 zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
    	   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
    	 System.out.println(new String(zk.getData("/testRootPath",false,null))); 
    	 // 取出子目录节点列表
    	 System.out.println(zk.getChildren("/testRootPath",true)); 
    	 // 修改子目录节点数据
    	 zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
    	 System.out.println("目录节点状态：["+zk.exists("/testRootPath",true)+"]"); 
    	 // 创建另外一个子目录节点
    	 zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), 
    	   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
    	 System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
    	 // 删除子目录节点
    	 zk.delete("/testRootPath/testChildPathTwo",-1); 
    	 zk.delete("/testRootPath/testChildPathOne",-1); 
    	 // 删除父目录节点
    	 zk.delete("/testRootPath",-1); 
    	 // 关闭连接
    	 zk.close();
	}
}