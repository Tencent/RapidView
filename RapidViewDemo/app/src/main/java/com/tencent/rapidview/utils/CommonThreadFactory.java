package com.tencent.rapidview.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.text.TextUtils;

/**
 * 通用的线程工厂类，推荐应用宝线程池用上这个类来创建，可以方便标识每个线程的名字
 * 
 * @author millionchen
 * 
 */
public class CommonThreadFactory implements ThreadFactory {
	public static final AtomicInteger poolNumber = new AtomicInteger(1);
	public final ThreadGroup group;
	public final AtomicInteger threadNumber = new AtomicInteger(1);
	public final String namePrefix;

	/**
	 * @param threadNamePrix
	 *            线程名前缀
	 */
	public CommonThreadFactory(String threadNamePrix) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "pool-" + poolNumber.getAndIncrement() + (TextUtils.isEmpty(threadNamePrix) ? "" : ("-" + threadNamePrix)) + "-thread-";
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.MIN_PRIORITY)
			t.setPriority(Thread.MIN_PRIORITY);
		return t;
	}
}
