package com.chxu.filelock.demo;

import java.util.concurrent.CountDownLatch;

public class BackProcee {

	public static void main(String[] args) {
		CountDownLatch countLock = new CountDownLatch(2);
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					long startTime = System.currentTimeMillis();
					while(true) {
						if ((System.currentTimeMillis()) - startTime > 5 * 60 * 1000) {
							break;
						}

						FileUtil.writeToMd5(FileUtil.BWMD5);
					}

					countLock.countDown();
				}
			}).start();

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					long startTime = System.currentTimeMillis();
					while(true) {
						if ((System.currentTimeMillis()) - startTime > 5 * 60 * 1000) {
							break;
						}

						FileUtil.readToMd5(FileUtil.BMD5);
					}

					countLock.countDown();
				}
			}).start();

		try {
			countLock.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
