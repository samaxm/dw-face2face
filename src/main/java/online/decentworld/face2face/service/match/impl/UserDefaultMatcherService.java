package online.decentworld.face2face.service.match.impl;


import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.cache.MatchQueueCache;
import online.decentworld.face2face.service.match.IUserMatcherService;
import online.decentworld.face2face.service.match.MatchUserInfo;
import online.decentworld.face2face.service.security.report.IReportService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.rdb.entity.LikeRecord;
import online.decentworld.rdb.entity.LikeRecordDetail;
import online.decentworld.rdb.mapper.LikeRecordMapper;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.rpc.dto.api.MapResultBean;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

import static online.decentworld.face2face.common.StatusCode.FAILED;
import static online.decentworld.face2face.common.StatusCode.SUCCESS;

/**
 * 获取匹配用户，通过redis队列实现
 * @author Sammax
 *
 */
@Service
public class UserDefaultMatcherService implements IUserMatcherService{
	

	@Autowired
	private MatchQueueCache matchCache;
	@Autowired
	private IReportService reportService;
	@Autowired
	private LikeRecordMapper likeMapper;
	@Autowired
	private IUserInfoService infoService;


	private static Logger logger= LoggerFactory.getLogger(UserDefaultMatcherService.class);

	/**
	 * 匹配等待队列数量，数量关系到了用户断线后重复遇到的几率以及用户等待的时间，该数值应当和当前在线人数相关，可弹性变化
	 */
	private int QUEUE_SIZE=3;
	/**
	 * 优先队列序号
	 */
	private int PRIORITY_QUEUE_NUM=100;

	/**
	 * 客户端调取该接口获取匹配的用户，匹配的模式如下：
	 * 根据匹配队列数目获取到随机的一个队列key值，然后通过redis的lpop命令从该key弹出一个值，若值为空表明该队列为空，则将本次请求用户ID插入队列，并返回告知客户端等待
	 * 若值不为空则直接返回给客户端进行匹配通话。
	 */
	@Override
	public ResultBean getMatchUser(String dwID,String name,String icon) {
		MapResultBean<String,MatchUserInfo> bean=new MapResultBean<String,MatchUserInfo>();
		if(reportService.isUserBlock(dwID)){
			bean.setStatusCode(FAILED);
			bean.setMsg("抱歉，您被举报次数过多已被封号");
		}else{
			MatchUserInfo info=new MatchUserInfo(dwID, name, icon);
			MatchUserInfo matched;
			do{
				int index=RandomUtil.getRandomNum(QUEUE_SIZE);
				String matchUserInfo=matchCache.getMatchUser(info, index);
				if(matchUserInfo==null){
					bean.setStatusCode(FAILED);
					bean.setMsg("搜索用户中...");
					return bean;
				}
				matched=JSON.parseObject(matchUserInfo, MatchUserInfo.class);
			}while (matched.getDwID().equals(dwID));
			bean.setStatusCode(SUCCESS);
			bean.getData().put("matchID", matched);
		}
		return bean;
	}

	@Override
	public ResultBean getMatchUserWithPriority(String dwID, String name, String icon, boolean isPrioritized) {
		MapResultBean<String,MatchUserInfo> bean=new MapResultBean<String,MatchUserInfo>();
		if(reportService.isUserBlock(dwID)){
			bean.setStatusCode(FAILED);
			bean.setMsg("抱歉，您被举报次数过多已被封号");
		}else{
			MatchUserInfo info=new MatchUserInfo(dwID, name, icon);
			MatchUserInfo matched;
			String matchUserInfo;
			do{
				if(isPrioritized){
					matchUserInfo=matchCache.getMatchUser(info, PRIORITY_QUEUE_NUM);
				}else {
					int index = RandomUtil.getRandomNum(QUEUE_SIZE);
					matchUserInfo= matchCache.getMatchUser(info, index);
				}
				if(matchUserInfo==null){
					return ObjectResultBean.FAIL("搜索用户中...");
				}
				matched=JSON.parseObject(matchUserInfo, MatchUserInfo.class);
			}while (matched.getDwID().equals(dwID));
			bean.setStatusCode(SUCCESS);
			bean.getData().put("matchID", matched);
		}
		return bean;
	}


	@Override
	public ResultBean likeUser(String dwID, String likedID) {
		LikeRecord record=new LikeRecord(dwID,likedID);
		try{
			likeMapper.insertSelective(record);

		}catch(DuplicateKeyException ex){
		} catch (Exception e) {
			logger.warn("[SEND_MQ_FAILED] LIKE_MESSAGE dwID#"+dwID+" likedID#"+likedID,e);
		}
		return ResultBean.SUCCESS;
	}



	@Override
	public ListResultBean<LikeRecordDetail> getLikeRecords(String dwID) {
		ListResultBean<LikeRecordDetail> list=new ListResultBean<LikeRecordDetail>();
		List<LikeRecordDetail> records=likeMapper.getLikeRecords(dwID);
		list.setStatusCode(SUCCESS);
		list.setData(records);
		return list;
	}

	@Override
	public void removeMatch(String dwID, String name, String icon) {
		matchCache.removeMatchUser(new MatchUserInfo(dwID,name,icon));
	}
}
