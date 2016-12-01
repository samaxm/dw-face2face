package online.decentworld.face2face.service.match;

import online.decentworld.rdb.entity.LikeRecordDetail;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.rpc.dto.api.ResultBean;

/**
 * 匹配用戶
 * @author Sammax
 *
 */
public interface IUserMatcherService {
	
	public ResultBean getMatchUser(String dwID,String name,String icon);

	public ResultBean getVipMatch(MatchUserInfo  info);

	public ResultBean getMatchUserWithPriority(String dwID,String name,String icon,String sign,boolean isPrioritized);

	public ResultBean likeUser(String dwID,String likedID);

	public ListResultBean<LikeRecordDetail> getLikeRecords(String dwID);

	public ListResultBean<LikeRecordDetail> getLikeRequests(String dwID);

	public void removeMatch(String dwID,String name,String icon,String sign);


	public void removeVIPMatch(MatchUserInfo info);


	public ResultBean acceptLikeRequest(String dwID,String likedID);

	public ResultBean refuseLikeRequest(String dwID,String likedID);
}

