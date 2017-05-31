
package com.chxu.filelock.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*************************************************
 * md5 类实现了RSA Data Security, Inc.在提交给IETF 的RFC1321中的MD5 message-digest 算法。
 *************************************************/

public class MD5Util {
    /*
     * 下面这些S11-S44实际上是一个4*4的矩阵，在原始的C实现中是用#define 实现的， 这里把它们实现成为static
     * final是表示了只读，切能在同一个进程空间内的多个 Instance间共享
     */
    static final int S11 = 7;

    static final int S12 = 12;

    static final int S13 = 17;

    static final int S14 = 22;

    static final int S21 = 5;

    static final int S22 = 9;

    static final int S23 = 14;

    static final int S24 = 20;

    static final int S31 = 4;

    static final int S32 = 11;

    static final int S33 = 16;

    static final int S34 = 23;

    static final int S41 = 6;

    static final int S42 = 10;

    static final int S43 = 15;

    static final int S44 = 21;

    static final byte[] PADDING = {
            -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0
    };

    /*
     * 下面的三个成员是MD5计算过程中用到的3个核心数据，在原始的C实现中 被定义到MD5_CTX结构中
     */
    private long[] state = new long[4]; // state (ABCD)

    private long[] count = new long[2]; // number of bits, modulo 2^64 (lsb

    /**
     * digestHexStr是MD5的唯一一个公共成员，是最新一次计算结果的 　 16进制ASCII表示.
     */
    public String digestHexStr;

    private static char[] Digit = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    
 // 全局数组
    private final static String[] strDigits = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    };

    /**
     * b2iu是我写的一个把byte按照不考虑正负号的原则的＂升位＂程序，因为java没有unsigned运算
     */
    public static long b2iu(byte b) {
        return b < 0 ? b & 0x7F + 128 : b;
    }

    /**
     * byteHEX()，用来把一个byte类型的数转换成十六进制的ASCII表示，
     * 因为java中的byte的toString无法实现这一点，我们又没有C语言中的 sprintf(outbuf,"%02X",ib)
     */
    public static String byteHEX(byte ib) {
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

    public static String toHexString(byte[] b) {
        if (b == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(Digit[(b[i] & 0xf0) >>> 4]);
            sb.append(Digit[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    // 这是MD5这个类的标准构造函数，JavaBean要求有一个public的并且没有参数的构造函数
    public MD5Util() {
        md5Init();
    }

    /*
     * F, G, H ,I 是4个基本的MD5函数，在原始的MD5的C实现中，由于它们是
     * 简单的位运算，可能出于效率的考虑把它们实现成了宏，在java中，我们把它们 　　实现成了private方法，名字保持了原来C中的。
     */

    /*
     * Decode把byte数组按顺序合成成long数组，因为java的long类型是64bit的，
     * 只合成低32bit，高32bit清零，以适应原始C实现的用途
     */

    /*
     * Encode把long数组按顺序拆成byte数组，因为java的long类型是64bit的， 只拆低32bit，以适应原始C实现的用途
     */

    /*
     * FF,GG,HH和II将调用F,G,H,I进行近一步变换 FF, GG, HH, and II transformations for
     * rounds 1, 2, 3, and 4. Rotation is separate from addition to prevent
     * recomputation.
     */

//    public byte[] getMD5ofBytes(String inbuf) {
//        md5Init();
//        md5Update(inbuf.getBytes(), inbuf.length());
//        md5Final();
//        return digest;
//    }

    /**
     * getMD5ofStr是类MD5最主要的公共方法，入口参数是你想要进行MD5变换的字符串
     * 返回的是变换完的结果，这个结果是从公共成员digestHexStr取得的．
     */
//    public String getMD5ofStr(String inbuf) {
//        md5Init();
//        md5Update(inbuf.getBytes(), inbuf.length());
//        md5Final();
//        digestHexStr = "";
//        for (int i = 0; i < 16; i++) {
//            digestHexStr += byteHEX(digest[i]);
//        }
//        return digestHexStr.toLowerCase();
//    }
    
 // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    
 // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    public String getMD5ofStr(String strObj) {
        String resultString = "";
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    public static String sGetMD5OfStr(String strObj){
        String resultString = "";
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            return byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    public String getMD5ofStr(String strObj, String encode) {
        String resultString = "";
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes(encode)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    public String getMD5StrOfBytes(byte[] bytes) {
        String resultString = "";
        try {
//            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

//    public String getMD5ofStr(String str, String encode) {
//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//            messageDigest.update(str.getBytes(encode));
//            return toHexString(messageDigest.digest());
//        } catch (Exception e) {
//        }
//        return null;
//    }

//    public String getMD5StrOfBytes(byte[] bytes) {
//        md5Init();
//        md5Update(bytes, bytes.length);
//        md5Final();
//        digestHexStr = "";
//        for (int i = 0; i < 16; i++) {
//            digestHexStr += byteHEX(digest[i]);
//        }
//        return digestHexStr.toLowerCase();
//    }

    /*
     * md5Memcpy是一个内部使用的byte数组的块拷贝函数，从input的inpos开始把len长度的 　　　　　
     * 字节拷贝到output的outpos位置开始
     */

    /*
     * md5Final整理和填写输出结果
     */

    /* md5Init是一个初始化函数，初始化核心变量，装入标准的幻数 */
    private void md5Init() {
        count[0] = 0L;
        count[1] = 0L;
        // /* Load magic initialization constants.

        state[0] = 0x67452301L;
        state[1] = 0xefcdab89L;
        state[2] = 0x98badcfeL;
        state[3] = 0x10325476L;

        return;
    }

    /*
     * 获取文件md5信息
     */
    public synchronized static String getMd5(File file) {
        String s = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,
                    0, file.length());

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteBuffer);
            s = bytesToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return s;
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
