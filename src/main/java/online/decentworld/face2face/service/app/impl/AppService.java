package online.decentworld.face2face.service.app.impl;

import online.decentworld.cache.redis.ApplicationInfoCache;
import online.decentworld.cache.redis.SessionCache;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.OnlineStatus;
import online.decentworld.face2face.service.app.OnlineStatusDB;
import online.decentworld.face2face.tools.Jpush;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	@Autowired
	private ApplicationInfoCache applicationInfoCache;
	@Autowired
	private SessionCache sessionCache;
	@Autowired
	private Jpush jpush;
	private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmm");
	private static byte[] ONLINE_NUM_TABLE="ONLINE_NUM_TABLE_2016".getBytes();
	private static byte[] COLUMN_FAMILY="ONLINENUM".getBytes();

	private static Logger logger= LoggerFactory.getLogger(AppService.class);
	private static int onlineCheckPeriod=60000;
	public AppService(){

	}

	@PostConstruct
	public void startTimer(){
		new Timer().schedule(new TimerTask() {

			private boolean isZero=false;

			@Override
			public void run() {
				try {
					long currentOnline=applicationInfoCache.checkOnline();
					if(currentOnline==0){
						isZero=true;
					}else{
						if(isZero==true){
							//new user coming
							jpush.pushOnlineNotice();
							isZero=false;
						}
					}
					OnlineStatusDB.storeCurrentOnlineNum(currentOnline);
				}catch (Exception e){
					logger.debug("",e);
				}

			}
		}, 10000, onlineCheckPeriod);
	}


	@Override
	public ObjectResultBean checkVersion(String type) {
		ObjectResultBean bean=new ObjectResultBean();
		bean.setStatusCode(StatusCode.SUCCESS);
		AppVersion version=versionMapper.selectByAppType(type);
		bean.setData(version);
		return bean;
	}


	public ResultBean markOnline(String dwID){
		applicationInfoCache.markOnline(dwID);
		return ResultBean.SUCCESS;
	}

	@Override
	public MapResultBean getOnlineUsers(int page) {
		long onlineNum=applicationInfoCache.getOnlineNum();
		List<BaseDisplayUserInfo> list=sessionCache.getSessionInfos(page);
		MapResultBean bean=new MapResultBean();
		bean.getData().put("onlineNum",onlineNum);
		if(list==null||list.isEmpty()){
			bean.getData().put("userInfos",null);
		}else {
			bean.getData().put("userInfos", list);
		}
		return bean;
	}

	@Override
	public ResultBean getOnlineStatus(String fromtime,String totime) {
		MapResultBean bean=new MapResultBean<>();
		bean.getData().put("maxOnline",applicationInfoCache.getMAX_ONLINE());
		bean.getData().put("statusList",queryOnlineStatus(fromtime,totime));
		bean.setStatusCode(StatusCode.SUCCESS);
		return  bean;
	}



	private List<OnlineStatus> queryOnlineStatus(String fromtime,String totime){
		/*
			time pattern yyyyMMddHH row key of hbase
		 */

		List<OnlineStatus> onlineStatuses;
		Result result= null;
		try {
			//query one hour data use get
			if(fromtime.equals(totime)){
				result = HbaseClient.instance().get(ONLINE_NUM_TABLE, fromtime.getBytes());
				onlineStatuses=extractResult(result);
			}else{
				//scan
				onlineStatuses=new LinkedList<>();
				List<Result> data=HbaseClient.instance().scan(ONLINE_NUM_TABLE,null,fromtime.getBytes(),totime.getBytes());
				data.forEach((Result r)->
					onlineStatuses.addAll(extractResult(r))
				);
			}
		return onlineStatuses;
		} catch (Exception e) {
			logger.warn("",e);
			return null;
		}
	}

	private List<OnlineStatus> extractResult(Result result){
		List<OnlineStatus> onlineStatuses=new LinkedList<>();
		NavigableMap<byte[], byte[]> familyMap =result.getFamilyMap(COLUMN_FAMILY);

		if(familyMap==null){
			return onlineStatuses;
		}

		int counter = 0;
		String[] Quantifers = new String[familyMap.size()];
		String[] value=new String[familyMap.size()];
		String row=Bytes.toString(result.getRow());
		for(byte[] bQunitifer : familyMap.keySet())
		{
			Quantifers[counter] = Bytes.toString(bQunitifer);
			value[counter]=Bytes.toString(familyMap.get(bQunitifer));
			counter++;
		}
		for(int i=0;i<familyMap.size();i++){
			String min=Quantifers[i];
			String num=value[i];
			if (min.length()==1){
				min="0"+min;
			}
			OnlineStatus status= null;
			try {
				status = new OnlineStatus(Long.parseLong(num),format.parse(row+min).getTime());
			} catch (ParseException e) {
			}
			onlineStatuses.add(status);
		}
		return onlineStatuses;
	}
}
