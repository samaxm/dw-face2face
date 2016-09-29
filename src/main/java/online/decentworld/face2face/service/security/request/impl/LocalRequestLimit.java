package online.decentworld.face2face.service.security.request.impl;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.service.security.request.RequestLimit;
import online.decentworld.tools.IPHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static online.decentworld.face2face.config.ConfigLoader.SecurityConfig.*;

public class LocalRequestLimit extends HandlerInterceptorAdapter implements RequestLimit{

	
	private static Logger logger=LoggerFactory.getLogger(LocalRequestLimit.class);
	/**
	 * 请求次数本地缓存
	 * key为ip+请求方法名
	 * value 为请求的时间线
	 */
	private ConcurrentHashMap<String,ConcurrentLinkedQueue<Long>> REQUEST_CACHE=new ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>>();
	/**
	 * 被屏蔽IP
	 */
	private ConcurrentHashMap<String,Long> BLOCK_IP=new ConcurrentHashMap<String, Long>();
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Frequency mf=((HandlerMethod)handler).getMethodAnnotation(Frequency.class);
		if(mf==null){
			return true;
		}else{
			String methodName=((HandlerMethod)handler).getMethod().getName();
			String ip= IPHelper.getLocalIP(request);
			if(!isBlock(ip)){
				return checkFrequency(mf, ip, methodName);	
			}else{
				return false;
			}
		}
	}
	
	
	private boolean checkFrequency(Frequency rule,String ip,String methodName){
		int limit=rule.limit();
		int expire=rule.time();
		String key=ip+methodName;
		ConcurrentLinkedQueue<Long> timeline=REQUEST_CACHE.get(key);
		if(timeline==null){
			//首次请求
			REQUEST_CACHE.put(key, new ConcurrentLinkedQueue<Long>());
			if(REQUEST_CACHE.size()>REQUEST_CACHE_SIZE){
				//缓存数量过多清除缓存
				cleanCache();
			}
			return true;
		}else{
			long current=System.currentTimeMillis();
			timeline.add(current);
			//若时间线数量大于限制则进行检测
			if(timeline.size()>limit){
				//移除符合规格的请求记录
				while(current-timeline.poll()>expire){					
				}
				//不符合规格数量大于等于配置限制则屏蔽
				if(timeline.size()>=limit){
					logger.info("[INVALID_REQUEST] ip#"+ip);
					BLOCK_IP.put(ip,current);
					REQUEST_CACHE.remove(key);
					return false;
				}else{
					return true;
				}
			}else{
				return true;
			}
		}
	}
	

	
	private void cleanCache(){
		for(String key:REQUEST_CACHE.keySet()){
			ConcurrentLinkedQueue<Long> records=REQUEST_CACHE.get(key);
			if(records!=null&&records.size()<REQUEST_LIMIT/2){
				REQUEST_CACHE.remove(key);
			}
		}
	}
	
	
	private boolean isBlock(String ip){
		Long block_time=BLOCK_IP.get(ip);
		if(block_time==null||(System.currentTimeMillis()-block_time)>INVALID_IP_BLOCK_TIME){
			return false;
		}else{
			return true;
		}
	}
}
