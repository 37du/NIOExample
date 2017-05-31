// $Id$
package com.chxu.ibm.demo.nio;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public class FastCopyFile
{
  static public void main( String args[] ) throws Exception {
    if (args.length<2) {
      System.err.println( "Usage: java FastCopyFile infile outfile" );
      System.exit( 1 );
    }

    String infile = args[0];
    String outfile = args[1];

    long startTime = System.currentTimeMillis();
    FileInputStream fin = new FileInputStream( infile );
    FileOutputStream fout = new FileOutputStream( outfile + "_direct" );

    FileChannel fcin = fin.getChannel();
    FileChannel fcout = fout.getChannel();

    ByteBuffer buffer = ByteBuffer.allocateDirect( 1024 );

    while (true) {
      buffer.clear();

      int r = fcin.read( buffer );

      if (r==-1) {
        break;
      }

      buffer.flip();

      fcout.write( buffer );
    }

    System.out.println("fastCopyByDirectBuffer inFile=" + ", outFile=" + outfile + ", time=" + (System.currentTimeMillis() - startTime));
  }
}


//  实验： /Users/apple/Desktop/桌面/Java性能权威指南.pdf   /Users/apple/Desktop/桌面/Java性能权威指南     文件大小为66M
//      fastCopyByDirectBuffer inFile=, outFile=/Users/apple/Desktop/桌面/Java性能权威指南, time=463
//  结论： 对比CopyFile.java ,  直接缓冲区的性能更快
