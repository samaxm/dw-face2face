package online.decentworld.face2face.tools;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: IDUitl.java
 * @Description: appID唯一
 * @author: wangxingchaoDW
 * @date: 2015年7月18日 下午2:01:11
 */

@Component
public class IDUtil {

	/**
	 * @Description: 获取UUID里面的数字，截取前六位
	 * @author: wangxingchaoDW
	 * @date: 2015年7月18日 下午2:02:18
	 * @return
	 */
	public static String getDWID() {
		UUID uniqueKey = UUID.randomUUID();
		String str = uniqueKey.toString().replace("-", "");
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m = p.matcher(str);
		String fs = m.replaceAll("").trim().substring(0, 10);
		return fs;
	}



	/**
	 * 
	 * @Description: 创建指定长度为6的随机字符串 
	 * @author: wangxingchaoDW
	 * @date: 2015年7月20日 下午2:15:35
	 * @param numberFlag
	 * @param length
	 * @return
	 */
	public  static String createRandomCode(){  
		String retStr = "";  
		String strTable = "1234567890";  
		int len = strTable.length();  
		boolean bDone = true;  
		do {  
			retStr = "";  
			int count = 0;  
			for (int i = 0; i < 4; i++) {  
				double dblR = Math.random() * len;  
				int intR = (int) Math.floor(dblR);  
				char c = strTable.charAt(intR);  
				if (('0' <= c) && (c <= '9')) {  
					count++;  
				}  
				retStr += strTable.charAt(intR);  
			}  
			if (count >= 2) {  
				bDone = false;  
			}  
		} while (bDone);  

		return retStr;  
	}  
	
	/**
	 * 生成环信的id长度为6数字加字母
	 * @Description: 
	 * @author: wangxingchaoDW
	 * @date: 2015年7月23日 下午3:27:45
	 * @return
	 */
	public static String randomToken(){
		String retStr = "";  
		String strTable = "1234567890ABCDEFGHIJKLMNPQRSTUVWXYZ";  
		int len = strTable.length();  
		boolean bDone = true;  
		do {  
			retStr = "";  
			int count = 0;  
			for (int i = 0; i < 16; i++) {  
				double dblR = Math.random() * len;  
				int intR = (int) Math.floor(dblR);  
				char c = strTable.charAt(intR);  
				if (('0' <= c) && (c <= '9')) {  
					count++;  
				}  
				retStr += strTable.charAt(intR);  
			}  
			if (count >= 2) {  
				bDone = false;  
			}  
		} while (bDone);  

		return retStr;  
	}
	
	public static void main(String[] args) {
		System.out.println(randomToken());
	}
}
