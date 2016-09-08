package online.decentworld.face2face.cache;

import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;

/**
 * 緩存鍵管理
 * @author Sammax
 *
 */
public class CacheKey {

	public static String SEPARATOR=":";

	/**
	 * 全部等待用户，field为用户dwID，value为用户的等待队列索引
	 */
	public static String WAITING_TABLE="WAITING:TABLE";
	
	/**
	 * 被举报次数记录,HSET,field为用户ID,value为被举报次数
	 */
	public static String REPORT_TABLE="REPORT:TABLE";
	
	/**
	 * 用户身家缓存,HSET,field为用户ID,value为用户身家
	 */
	public static String WEALTH="WEALTH";

	/**
	 * 用户AES的key缓存，hest,field为用户ID,value为用户key
	 */
	public static String AES="AES";

	
	
	
	/**
	 * 根据手机号码获取注册验证码
	 * @param phoneNum
	 * @return
	 */
	public static String TOKEN_KEY(String dwID,TokenType type){
		return type.toString()+SEPARATOR+dwID;
	}
	/**
	 * 根据手机号码获取注册验证码
	 * @param phoneNum
	 * @return
	 */
	public static String PHONECODE_KEY(String phoneNum,PhoneCodeType type){
		return type.toString()+SEPARATOR+phoneNum;
	}
	/**
	 * 根据索引值获取等待队列key
	 * @param index
	 * @return
	 */
	public static String MATCH_QUEUE_KEY(int index){
		return "MATCH:QUEUE:"+index;
	}
}
