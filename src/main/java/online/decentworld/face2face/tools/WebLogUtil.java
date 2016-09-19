package online.decentworld.face2face.tools;

import com.alibaba.fastjson.JSON;
import online.decentworld.tools.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 将各种类型转成String
 * @author Sammax
 *
 */
public class WebLogUtil extends LogUtil {
	private static Log log=LogFactory.getLog(WebLogUtil.class);
	

	public static String toLogString(ServerHttpRequest request){
		try{
			ServletServerHttpRequest sr=(ServletServerHttpRequest)request;
			HttpServletRequest r=sr.getServletRequest();
			Map<String,String[]> parameters=r.getParameterMap();
			if(parameters==null||parameters.size()==0){
				return "null";
			}else{
				return JSON.toJSONString(parameters);
			}
		}catch(Exception ex){
			log.warn(ex);
			return "error";
		}
	}
	
	public static void main(String[] args) {
		Class<?>[] classes={WebLogUtil.class};
		System.out.println(toLogString(classes));
	}
}
