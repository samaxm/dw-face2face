package online.decentworld.face2face.api.easemob;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Response;
import okhttp3.ResponseBody;
import online.decentworld.face2face.exception.EasemobRegisterFailed;
import online.decentworld.face2face.exception.GetEasemobTokenException;
import online.decentworld.tools.HttpHeader;
import online.decentworld.tools.HttpRequestUtil;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static online.decentworld.face2face.config.ConfigLoader.APIConfig.*;
import static online.decentworld.tools.HttpRequestUtil.Content_Type;
import static online.decentworld.tools.HttpRequestUtil.application_json;
/**
 * 環信API工具類
 * @author Sammax
 *
 */
@Component
public class EasemobApiUtil {
	/**
	 * 授權token
	 */
	private static String token="";
	private static String baseURL="https://a1.easemob.com/";
	private static String prefix=baseURL+GROUP_NAME+"/"+APP_NAME;
	private static String getTokenURL=prefix+"/token";
	private static String registerUser=prefix+"/users";
	
	private static Logger logger=LoggerFactory.getLogger(EasemobApiUtil.class);
	
	
	private String getToken() throws GetEasemobTokenException{
		JSONObject object=new JSONObject();
		object.put("grant_type", "client_credentials");
		object.put("client_id", APP_CLIENT_ID);
		object.put("client_secret", APP_CLIENT_SECRET);
		try{
			Response response= HttpRequestUtil.JSON(getTokenURL, object.toJSONString(), new HttpHeader(Content_Type, application_json));
			if(response.code()==200){
				ResponseBody body=response.body();
				token= JSON.parseObject(body.string()).getString("access_token");
				return token;
			}else{
				logger.warn("[GET_TOKEN_ERROR] response#"+response.code());
				throw new GetEasemobTokenException();
			}
		}catch(IOException e){
			logger.warn("[GET_TOKEN_ERROR]",e);
			throw new GetEasemobTokenException();
		}
	}
	
	public String registerEasemobUser(String userName,String password) throws EasemobRegisterFailed, GetEasemobTokenException{
		JSONObject json=new JSONObject();
		json.put("username",userName);
		json.put("password", password);
		int fail=0;
		try {
			Response response=HttpRequestUtil.JSON(registerUser, json.toJSONString(), new HttpHeader("Authorization", "Bearer " + token));
			if(response.code()==401&&fail<3){
				fail++;
				logger.debug("[TOKEN_EXPIRE]");
				getToken();
				return registerEasemobUser(userName,password);
			}else if(response.code()==400){
				//可能是ID重复
				userName= IDUtil.getDWID();
				return registerEasemobUser(userName, password);
			}else if(response.code()==200){
				return userName;
			}else{
				throw new EasemobRegisterFailed(response.code());
			}
		} catch (IOException e) {
			logger.warn("[REGISTER_EASEMOB_ERROR]",e);
			throw new EasemobRegisterFailed(EasemobRegisterFailed.IO_ERROR);
		}
	}

	
	
}
