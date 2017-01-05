package online.decentworld.face2face.service.match.impl;

import online.decentworld.face2face.service.match.IRetrivePaddingContentService;
import online.decentworld.rdb.entity.Joke;
import online.decentworld.rdb.mapper.JokeMapper;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.tools.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static online.decentworld.face2face.common.StatusCode.SUCCESS;

@Service
public class RetriveJokeService implements IRetrivePaddingContentService{


	@Autowired
	private JokeMapper mapper;

	
	private String[] data;
	
	private AtomicInteger requestCount=new AtomicInteger(0);
	
	private int cursor=0;

	private static int MAX_SIZE=500;
	
	private static int RETURN_SIZE=20;
	
	private static int REFRESH_INTERVAL=1000; 
	
	@Override
	public ListResultBean<String> retrivePaddingContent(int index) {
		ListResultBean<String> bean=new ListResultBean<String>();
		int requestAmount=requestCount.incrementAndGet();
		List<String> jokes=getJokes(index);
		bean.setData(jokes);
		bean.setStatusCode(SUCCESS);
		if(requestAmount%REFRESH_INTERVAL==0){
			requestCount.set(0);
			initData();
		}
		return bean;
	}
	
	
	private List<String> getJokes(int index){
		int step=RandomUtil.getRandomNum(RETURN_SIZE);
		if(data==null){
			initData();
		}
		List<String> returnData=new ArrayList<String>(RETURN_SIZE);
		for(int i=0;i<RETURN_SIZE;i++){
			returnData.add(data[(step+step*i)%MAX_SIZE]);
		}
		return returnData;
	}
	
	private synchronized void initData(){
		int data_size=mapper.getDataSize();
		//剩余数量小于规定最大数量
		if(data_size-cursor<MAX_SIZE){
			MAX_SIZE=data_size-cursor;
		}
		data=new String[MAX_SIZE];
		List<Joke> list=mapper.getJokes(cursor, MAX_SIZE);
		cursor+=MAX_SIZE;
		if(cursor>=data_size){
			cursor=0;
		}
		for(int i=0;i<list.size();i++){
			if(i<MAX_SIZE){
				data[i]=list.get(i).getContent();
			}
		}
	}



}
