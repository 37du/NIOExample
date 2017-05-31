// $Id$
package com.chxu.ibm.demo.nio;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class CopyFile
{
  static public void main( String args[] ) throws Exception {
    if (args.length<2) {
      System.err.println( "Usage: java CopyFile infile outfile" );
      System.exit( 1 );
    }

    String infile = args[0];
    String outfile = args[1];
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                copyByChannel(infile, outfile + "_chanel");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }).start();
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                copyByChannelTransfer(infile, outfile + "_chaneltransfer");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }).start();
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                copyByIO(infile, outfile + "_io");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }).start();
  }

  private static void ensureNewOutFile(String outFile) throws Exception {
      File file = new File(outFile);
      if (file.exists()) {
          file.delete();
      }

      file.createNewFile();
  }

  private static void copyByChannelTransfer(String inFile, String outFile) throws Exception {
      long startTime = System.currentTimeMillis();
      ensureNewOutFile(outFile);
      FileInputStream fin = new FileInputStream(inFile);
      FileOutputStream fout = new FileOutputStream(outFile);

      FileChannel fcin = fin.getChannel();
      FileChannel fcout = fout.getChannel();
      fcin.transferTo(0, fcin.size(), fcout);

      System.out.println("copyByChannelTransfer inFile=" + inFile + ", outFile=" + outFile + ", time=" + (System.currentTimeMillis() - startTime));
  }

  private static void copyByChannel(String inFile, String outFile) throws Exception {
      long startTime = System.currentTimeMillis();
      ensureNewOutFile(outFile);
      FileInputStream fin = new FileInputStream(inFile);
      FileOutputStream fout = new FileOutputStream(outFile);

      FileChannel fcin = fin.getChannel();
      FileChannel fcout = fout.getChannel();

      ByteBuffer buffer = ByteBuffer.allocate( 1024 );

      while (true) {
          buffer.clear();

          int r = fcin.read( buffer );

          if (r==-1) {
              break;
          }

          buffer.flip();

          fcout.write( buffer );
      }

      System.out.println("copyByChannel inFile=" + inFile + ", outFile=" + outFile + ", time=" + (System.currentTimeMillis() - startTime));
  }

  private static void copyByIO(String inFile, String outFile) throws Exception {
      long startTime = System.currentTimeMillis();
      ensureNewOutFile(outFile);
      FileInputStream fis = null;
      FileOutputStream fos = null;
      try {
          fis = new FileInputStream(inFile);
          fos = new FileOutputStream(outFile);
          byte[] buffer = new byte[1024];
          int len = -1;
          while ( (len = fis.read(buffer) ) != -1) {
              fos.write(buffer, 0, len);
          }

          fos.flush();
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          if (fis != null) {
              try {
                  fis.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

          if (fos != null) {
              try {
                  fos.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }

      System.out.println("copyByIO inFile=" + inFile + ", outFile=" + outFile + ", time=" + (System.currentTimeMillis() - startTime));
  }
}

//  参考： https://www.ibm.com/developerworks/cn/education/java/j-nio/j-nio.html
//  实验：/Users/apple/Desktop/桌面/Java性能权威指南.pdf   /Users/apple/Desktop/桌面/Java性能权威指南    需要复制的文件大小为66M
//   copyByChannelTransfer inFile=/Users/apple/Desktop/桌面/Java性能权威指南.pdf, outFile=/Users/apple/Desktop/桌面/Java性能权威指南_chaneltransfer, time=636
//   copyByIO inFile=/Users/apple/Desktop/桌面/Java性能权威指南.pdf, outFile=/Users/apple/Desktop/桌面/Java性能权威指南_io, time=1106
//   copyByChannel inFile=/Users/apple/Desktop/桌面/Java性能权威指南.pdf, outFile=/Users/apple/Desktop/桌面/Java性能权威指南_chanel, time=1129
//  结论：
//   可以看到  FileChannel 和 File IO流， 使用1024字节的buffer的性能差不多， 但是使用 FileChannel.tranfer 会节省一半的时间

//  另外：
//     使用DirectBuffer, 性能更高， 见FastCopyFile.java
