package org.chuck.util;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

public class ThreadPool {
	private static Executor executor;
	
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
	
    private static BlockingQueue<Runnable> poolWorkQueue=new LinkedBlockingQueue<>(64);
	private static final ThreadFactory threadFactory=new ThreadFactory(){
		private final AtomicInteger tCount = new AtomicInteger(1);
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r,"Chuck_ThreadPool #"+tCount.getAndIncrement());
		}		
	};
    
    private static final Executor THREAD_POOL_EXECUTOR=new ThreadPoolExecutor(CORE_POOL_SIZE, 
    		MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
    
    public static Executor getExecutor(){
		if(executor==null){
			executor=THREAD_POOL_EXECUTOR;
		}
		return executor;
	}

    public static Executor getExecutor(int priority){
        if(executor==null){
            executor=THREAD_POOL_EXECUTOR;
        }
        Process.setThreadPriority(priority);
        return executor;
    }

    public static Executor getSerialExecutor(){
        if(executor==null){
            executor=new SerialExecutor();
        }
        return executor;
    }
    public static Executor getSerialExecutor(int priority){
		if(executor==null){
			executor=new SerialExecutor();
		}
		Process.setThreadPriority(priority);
		return executor;
	}
	
//	public static Executor getExecutor(){
//		if(executor==null){
//			executor=new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 
//					KEEP_ALIVE, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
//		}
//		return executor;
//	}
//	
//	public static Executor getExecutor(int priority){
//		if(executor==null){
//			executor=new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 
//					KEEP_ALIVE, TimeUnit.SECONDS, poolWorkQueue, threadFactory);
//		}
//		Process.setThreadPriority(priority);
//		return executor;
//	}
	
	
	
	
	
	
	
	
	private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;
        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }
        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }
}
