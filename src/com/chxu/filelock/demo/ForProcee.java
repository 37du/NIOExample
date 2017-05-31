package com.chxu.filelock.demo;

import java.util.concurrent.CountDownLatch;

public class ForProcee {
	
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
					
					FileUtil.writeToMd5(FileUtil.FWMD5);
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

					FileUtil.readToMd5(FileUtil.FMD5);
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

		// 此处程序， 前台只读， 后台读写， 使用FileLock, 不会读取到脏数据，并且同步安全。
		//  sort fmd5 | uniq > fmd5_s
		//  sort bmd5 | uniq > bmd5_s
		//  sort bwmd5 | uniq > bwmd5_s
		//  comm -23 fmd5_s bwmd5_s
		
		//  d41d8cd98f00b204e9800998ecf8427e   为 空文件到 md5
		//  touch empty && md5 empty
		
		//  分析统计文件，发现没有脏数据， 有时只会出现读取到空数据， 因为还没开始写数据


		//  每个进程要么读， 要么写， 只能有一个线程工作， 这样不管多少个进程， 都不会读取到脏数据
		//  以上结论， 已在 macOs  和 ubuntu 上压测过。


        // 存在问题： 进程A 同时两个线程读写， 进程B也是同时两个线程读写， 会读取到脏数据， 添加 sleep 缓解激烈到cpu竞争， 也会读取到脏数据
 	}

}
