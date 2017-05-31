package com.chxu.filelock.demo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FileUtil {
    public static final String FMD5 = "fmd5";
    
    public static final String FWMD5 = "fwmd5";

    public static final String BMD5= "bmd5";

    public static final String BWMD5= "bwmd5";
    
    private static final Object sFileLock = new Object();
    
    public static void writeMd5List(String content, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file, true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
        bw.write(content+ "\n");
        bw.close();
        fos.close();
    }

    public static void write(String filepath, String content) throws Exception {
        FileChannel channel = null;
        FileLock lock = null;
        FileOutputStream fos = null;
        synchronized (sFileLock) {
            try {
                File file = new File(filepath);
                fos = new FileOutputStream(file);
                channel = fos.getChannel();
                lock = channel.lock();
                Charset charset = Charset.forName("UTF-8");
                channel.write(charset.encode(content));
            } catch (FileNotFoundException e) {
                throw e;
            } catch (IOException e) {
                throw e;
            } catch (RuntimeException e) { //  防止同进程中多线程获取文件锁抛异常
                throw e;
            } catch (Exception e) {
                throw e;
            } finally {
                try {
                    if (lock != null) {
                        lock.release();
                    }

                    if (channel != null) {
                        channel.close();
                    }
                    
                    if (fos != null) {
                    	fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String read(String filepath) throws Exception {
    	String result = "";
        synchronized (sFileLock) {
            FileChannel channel = null;
            FileLock lock = null;
            RandomAccessFile raf = null;

            try {
                File file = new File(filepath);
                raf = new RandomAccessFile(file, "rw");
                channel = raf.getChannel();
                lock = channel.lock();
                Charset charset = Charset.forName("UTF-8");
                long size = channel.size();
                ByteBuffer buffer = ByteBuffer.allocate((int)size);
                channel.read(buffer);
                buffer.flip();
                result = charset.decode(buffer).toString();
            } catch (Exception e) {
                throw e;
            } finally {
                try {
                    if (lock != null) {
                        lock.release();
                    }

                    if (channel != null) {
                        channel.close();
                    }
                    
                    if (raf != null) {
                    	raf.close();
                    }
                } catch (final IOException e) {
                    throw e;
                }

                return result;
            }
        }
    }
    
    public static void writeToMd5(String filepath) {
            Random random = new Random();
            final int length = random.nextInt(20) + 1;
            StringBuilder sb = new StringBuilder(length);
            for (int k = 0; k < length; k++) {
                sb.append((char) (ThreadLocalRandom.current().nextInt(33, 128)));
            }

            MD5Util md5Util = new MD5Util();
            String content = sb.toString();
            String md5Str = md5Util.getMD5ofStr(content);
            try {
                write("ack-test", new String(content));
                writeMd5List(md5Str, filepath);
            } catch (Exception e) {
            	e.printStackTrace();
                return;
            }
    }
    
    public static void readToMd5(String filepath) {
            try {
                final String result = read("ack-test");
                final String md5 = MD5Util.sGetMD5OfStr(result);
                writeMd5List(md5, filepath);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
    }
}
