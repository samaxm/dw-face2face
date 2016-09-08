package online.decentworld.face2face.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class MD5 {
	
	
	private static Random random = new Random();     

    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public MD5() {
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

    // 返回形式只为数字
    @SuppressWarnings("unused")
	private static String byteToNum(byte bByte) {
        int iRet = bByte;
        System.out.println("iRet1=" + iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        return String.valueOf(iRet);
    }
    
    @SuppressWarnings("unused")
	private static String getSalt(){
    	//salt length
		int length=10;
	    String base = "abcdefghijklmnopqrstuvwxyz0123456789";     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < length; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	}
    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String GetMD5Code(String password,String salt) {
        try {
        	String str=password+salt;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            return byteToString(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
        	System.out.println("MD5加密错误");
            ex.printStackTrace();
            return null;
        }
    }
    public static String GetMD5Code(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            return byteToString(md.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
        	System.out.println("MD5加密错误");
            ex.printStackTrace();
            return null;
        }
    }

    public static String GetMD5Code(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            return byteToString(md.digest(data));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("MD5加密错误");
            ex.printStackTrace();
            return null;
        }
    }

//    public static String getSaltedMD5Code(String str){
//    		return GetMD5Code(str+getSalt());
//    }
//    
//    //测试
//    public static void main(String[] args) {
//        MD5 getMD5 = new MD5();
//        System.out.println(getMD5.GetMD5Code("123"));
//    }
}