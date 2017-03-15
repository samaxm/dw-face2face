package online.decentworld.face2face.service.app.impl;

import online.decentworld.cache.redis.ApplicationInfoCache;
import online.decentworld.cache.redis.SessionCache;
import online.decentworld.face2face.cache.ActivityCache;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.service.app.ActivityList;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.app.OnlineStatus;
import online.decentworld.rdb.entity.ActivityAnswer;
import online.decentworld.rdb.entity.AppVersion;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.WebGame;
import online.decentworld.rdb.hbase.HbaseClient;
import online.decentworld.rdb.mapper.ActivityAnswerMapper;
import online.decentworld.rdb.mapper.ActivityMapper;
import online.decentworld.rdb.mapper.AppVersionMapper;
import online.decentworld.rdb.mapper.WebGameMapper;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;

@Service
public class AppService implements IAppService{

	@Autowired
	private AppVersionMapper versionMapper;
	@Autowired
	private ApplicationInfoCache applicationInfoCache;
	@Autowired
	private SessionCache sessionCache;
	@Autowired
	private ActivityMapper activityMapper;
	@Autowired
	private ActivityAnswerMapper activityAnswerMapper;
	@Autowired
	private ActivityCache activityCache;
	@Autowired
	private WebGameMapper webGameMapper;


	private static SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmm");
	private static byte[] ONLINE_NUM_TABLE="ONLINE_NUM_TABLE_2016".getBytes();
	private static byte[] COLUMN_FAMILY="ONLINENUM".getBytes();

	private static Logger logger= LoggerFactory.getLogger(AppService.class);
	public AppService(){
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

	@Override
	public ResultBean getiphoneStatus(Integer versionNum) {
		if(applicationInfoCache.getIphoneStatus()>versionNum)
			return ObjectResultBean.SUCCESS(false);
		else
			return ObjectResultBean.SUCCESS(true);
	}

	@Override
	public ResultBean setiphoneStatus(IphoneOnlineStatusCommand command) {
		if(command.getToken().equals("sammaxsimple")){
			if(applicationInfoCache.setIphoneStatus(command.getVersionNum())){
				return ResultBean.SUCCESS;
			}
		}
		return ResultBean.FAIL("");
	}

	@Override
	public ResultBean getActivityList(long dateNum) {
		ActivityList list=new ActivityList();
		list.setDateNum(dateNum);
		list.setList(activityMapper.getActivityByDateNum(dateNum));
		return ObjectResultBean.SUCCESS(list);
	}

	@Override
	public ResultBean checkActivityAnswer(int activityID, String answer,String dwID) {
		Integer id=activityCache.isAnswerRight(activityID, answer);
		if(id==null){
			return ResultBean.FAIL("抱歉，答案错误哦，你是不是被戏弄了啊= =");
		}else{
			activityAnswerMapper.updateAnswerStatus(id,true,dwID);
			ActivityAnswer an=activityAnswerMapper.selectByPrimaryKey(id);
			return ObjectResultBean.SUCCESS(an);
		}
	}

	@Override
	public ResultBean uploadAdress(String dwID, int answerID, String address) {
		ActivityAnswer answer=activityAnswerMapper.selectForUpdate(answerID);
		if(answer!=null&&answer.getChecked()&&dwID.equals(answer.getDwid())){
			answer.setContact(address);
			activityAnswerMapper.updateByPrimaryKey(answer);
			return ObjectResultBean.SUCCESS(answer);
		}else {
			return ResultBean.FAIL("");
		}
	}

	@Override
	public ResultBean getWebGame() {
		WebGame webGame=webGameMapper.selectByPrimaryKey(1);
		return ObjectResultBean.SUCCESS(webGame);
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
