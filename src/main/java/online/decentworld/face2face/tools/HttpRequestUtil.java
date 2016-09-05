package online.decentworld.face2face.tools;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * http请求工具类
 * @author Sammax
 *
 */
public class HttpRequestUtil {
	
	private static OkHttpClient client=new OkHttpClient();
	public static String Content_Type="Content-Type";
	public static String application_json="application/json";
	public static int SUCCESS=200;
	
	public enum Method{
		GET,POST;
	}
	
	public static Response GET(String url,HttpHeader... headers) throws IOException{
		Builder builder=new Request.Builder();
		builder.url(url);
		if(headers!=null){
			for(HttpHeader header:headers){
				builder.addHeader(header.getName(), header.getValue());
			}
		}
		return client.newCall(builder.build()).execute();
	}
	
	
	public static Response JSON(String url,String json,HttpHeader... headers) throws IOException{
		Builder builder=new Request.Builder();
		RequestBody body=RequestBody.create(MediaType.parse("JSON"),json);
		builder.url(url);
		if(headers!=null){
			for(HttpHeader header:headers){
				builder.addHeader(header.getName(), header.getValue());
			}
		}
		builder.post(body);
		return client.newCall(builder.build()).execute();
	}
}
