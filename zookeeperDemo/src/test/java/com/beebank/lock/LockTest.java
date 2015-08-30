package com.beebank.lock;

import org.junit.Test;

public class LockTest {
	
//	DistributedLock lock = new DistributedLock("server1","/distributedlock","product_consumer");
//	
	Product product = new Product();
	
	Consumer consumer = new Consumer();
	
	@Test
	public void application_1(){
		while(true){
			DistributedLock lock = new DistributedLock("server1","/distributedlock","/product_consumer");
			lock.lock();
			product.simplepProduct();
			consumer.simpleConsumer();
			lock.unlock();
		}

	}
	
	@Test
	public void application_2(){
		while(true){
			DistributedLock lock = new DistributedLock("server1","/distributedlock","/product_consumer");
			lock.lock();
			product.simplepProduct();
			consumer.simpleConsumer();
			lock.unlock();
		}

	}

	@Test
	public void application_3(){
		while(true){
			DistributedLock lock = new DistributedLock("server1","/distributedlock","/product_consumer");
			lock.lock();
			product.simplepProduct();
			consumer.simpleConsumer();
			lock.unlock();
		}
	}
}
