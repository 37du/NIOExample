package com.chxu.filelock.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

public class ForProcee {
	
	public static void main(String[] args) {
        try {
            File file = new File("ack-test");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel channel = raf.getChannel();

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

                        FileUtil.writeToMd5(FileUtil.FWMD5, channel);
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

                        FileUtil.readToMd5(FileUtil.FMD5, channel);
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

            if (channel != null) {
                channel.close();
            }

            if (raf != null) {
                raf.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


		// 此处程序， 前台只读， 后台读写， 使用FileLock, 不会读取到脏数据，并且同步安全。
		//  排序去重：    sort fmd5 | uniq > fmd5_s  把每个文件都排序去重
		//              sort bmd5 | uniq > bmd5_s
		//              sort bwmd5 | uniq > bwmd5_s
        //  合并：       sort fwmd5_s bwmd5_s | uniq > wmd5_s  把两个进程写文件的md5合并并去重
		//  差集： commd -23 fmd5_s bwmd5_s
		
		//  d41d8cd98f00b204e9800998ecf8427e   为 空文件到 md5
		//  touch empty && md5 empty

		// 共享FileChannel , 多进程都会读取到脏数据

		// 与之前代码表， 区别是之前每次写入都会调用 close 写入内容到本地文件， 猜想可能是进程内每次写都没有close,
		// 而是系统缓存了，导致读取到脏数据， 尝试每次写入调用force, 发现也没有用.   要了解特定平台到系统缓存机制了

		// 以上结论， 在 macOs 和 ubuntu 上都有验证
 	}

}
