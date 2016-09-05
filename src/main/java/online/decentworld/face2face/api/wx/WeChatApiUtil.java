package online.decentworld.face2face.api.wx;

import static online.decentworld.face2face.config.ConfigLoader.APIConfig.*;
import static online.decentworld.face2face.tools.HttpRequestUtil.*;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;
import online.decentworld.face2face.exception.GetWXAccessTokenError;
import online.decentworld.face2face.exception.GetWXInfoError;
import online.decentworld.face2face.tools.HttpHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 微信相關接口
 * @author Sammax
 *
 */
@Component
public class WeChatApiUtil {

	private static Logger logger=LoggerFactory.getLogger(WeChatApiUtil.class);
	
	public AccessTokenResult getAccessToken(String code) throws GetWXAccessTokenError{
		StringBuilder sb=new StringBuilder();
		sb.append(WX_ACCESS_TOKEN_URL).append("?appid=").append(WX_APP_ID).append("&secret=")
		.append(WX_APP_SECRET).append("&code=").append(code).append("&grant_type=authorization_code");
		try{
			Response response=GET(sb.toString(), new HttpHeader[]{});
			ResponseBody body=response.body();
			JSONObject result=JSON.parseObject(body.string());
			if(response.code()==SUCCESS&&isSuccess(result)){
					return JSON.toJavaObject(result,AccessTokenResult.class);
			}else{
				logger.warn("[GET_WX_ACCESS_TOKEN_ERROR] code#"+response.code());
				throw new GetWXAccessTokenError();
			}
		}catch(IOException ex){
			logger.warn("[GET_WX_ACCESS_TOKEN_ERROR]",ex);
			throw new GetWXAccessTokenError();
		}
	}
	
	public WXInfo getWXInfo(String code) throws GetWXInfoError, GetWXAccessTokenError{
		AccessTokenResult access=getAccessToken(code);
		StringBuilder sb=new StringBuilder();
		sb.append(GET_WX_USER_INFO).append("?access_token=").append(access.getAccess_token()).append("&openid=")
		.append(access.getOpenid()).append("&lang=zh_CN");
		try {
			Response response = GET(sb.toString(),new HttpHeader[]{});
			JSONObject jsonObject=JSON.parseObject(response.body().string());
			if(response.code()==SUCCESS&&isSuccess(jsonObject)){
				WXInfo info = JSON.toJavaObject(jsonObject, WXInfo.class);
				return info;
			}else{
				logger.warn("[GET_WXINFO_ERROR] code#"+response.code());
				throw new GetWXInfoError();
			}
		} catch (IOException e) {
			logger.warn("[GET_WXINFO_ERROR]",e);
			throw new GetWXInfoError();
		}
	
	}
	
	private boolean isSuccess(JSONObject json){
		if(json.getString("errcode")!=null){
			logger.warn("[GET_WX_ACCESS_TOKEN_ERROR] errcode#"+json.getString("errcode")+" errmsg#"+json.getString("errmsg"));
			return false;
		}else{
			return true;
		}
	}
	
	public static void main(String[] args) {
	}
	
}
