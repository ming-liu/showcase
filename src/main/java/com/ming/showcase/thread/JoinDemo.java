package com.ming.showcase.thread;

public class JoinDemo {

	public static void main(String[] args) throws InterruptedException {
		simpleThreadDemo();
		simpleJoinThreadDemo();
		simpleJoin100ThreadDemo();
	}

	/**
	 * 结果thread1和main的打印先后不确定
	 */
	private static void simpleThreadDemo() {
		Thread t1 = new Thread("t1") {
			@Override
			public void run() {
				System.out.println("I'm " + this.getName());
			}
		};
		t1.start();
		System.out.println("I'm main");
	}

	/**
	 * main thread 一定会等待thread1
	 * 
	 * @throws InterruptedException
	 */
	private static void simpleJoinThreadDemo() throws InterruptedException {
		Thread t1 = new Thread("t1") {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				System.out.println("I'm " + this.getName());
			}
		};
		t1.start();
		/**
		 * 从源码看，while(true)里面判断t1线程是否alive,wait(0)。此程序是在main
		 * thread中执行。等待t1执行完毕唤醒。
		 */
		t1.join();

		System.out.println("I'm main");
	}

	private static void simpleJoin100ThreadDemo() throws InterruptedException {
		Thread t1 = new Thread("t1") {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				System.out.println("I'm " + this.getName());
			}
		};
		t1.start();
		/**
		 * 从源码看，while(true)里面判断t1线程是否alive,wait(1000)。此程序是在mainthread中执行。
		 * 等待t1执行完毕唤醒。join(timeout)和没有参数的join区别在于,最多等待timeout的时间。这是
		 * 通过调用wait(timeout)和在while(true)中不断判断时间间隔做到的。
		 * 
		 */
		t1.join(1000);
		System.out.println("I'm main");
	}
}
