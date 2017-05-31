package com.chxu.filelock.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.CountDownLatch;

public class BackProcee {

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

                        FileUtil.writeToMd5(FileUtil.BWMD5, channel);
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

                        FileUtil.readToMd5(FileUtil.BMD5, channel);
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
	}
}
