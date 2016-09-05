package online.decentworld.face2face.cache;

import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

import org.springframework.cache.Cache.ValueWrapper;

import com.alibaba.fastjson.JSON;

public class ResultBeanValueWraper implements ValueWrapper{
	
	private ResultBean value;
	
	public  ResultBeanValueWraper(String value){
		this.value=JSON.parseObject(value,MapResultBean.class);
	}
	
	@Override
	public Object get() {
		return value;
	}

}
