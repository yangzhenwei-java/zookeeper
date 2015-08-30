package com.yixin.js;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:DLockTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年4月14日 下午6:58:08 <br/>
 * @author debiao.dong
 * @version
 * @since 1.0
 * @see
 */
public class DLockTest {
    public static ArrayBlockingQueue<Runnable> arrayBlockQueue = new ArrayBlockingQueue<Runnable>(2000);
    private static ExecutorService sqlPool = new ThreadPoolExecutor(5, 2000, 15, TimeUnit.SECONDS, arrayBlockQueue);

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            sqlPool.submit(new Runnable(){
                public void run() {
                    DistributedLock lock = null;
                    try{
                        lock = new DistributedLock("10.100.142.30:2181", "1234567890");
                        lock.lock();
                        Thread.sleep(2000);
                        System.out.println("线程   " + Thread.currentThread().getId() + "正在运行！！");
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally{
                        lock.unlock();
                    }
                }
                
            });
        }
    }
}

