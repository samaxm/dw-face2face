package online.decentworld.face2face.cache;

import online.decentworld.cache.redis.CacheKey;
import online.decentworld.face2face.common.PhoneCodeType;
import online.decentworld.face2face.common.TokenType;

/**
 * Created by Sammax on 2016/9/12.
 */
public class WebCacheKey extends CacheKey {


    /**
     * 根据手机号码获取注册验证码
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

    public static String MATCH_SET="MATCH:SET";


    public static String VIP_MATCH_SET="vip_MATCH:SET";
    public static String USER_TAG="USER_TAG";
    public static String MATCH_INFO(String dwID){return dwID+":MI";}

    public static String ACTIVITY_ANSWER_SET(int activityID){return activityID+":answer";}
    public static String ACTIVITY_ANSWER_INDEX(int activityID){return activityID+":answer:index";}
}
