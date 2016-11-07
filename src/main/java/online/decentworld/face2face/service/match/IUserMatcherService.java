package online.decentworld.face2face.service.match;

import online.decentworld.rdb.entity.LikeRecordDetail;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * 匹配用戶
 * @author Sammax
 *
 */
public interface IUserMatcherService {
	
	public ResultBean getMatchUser(String dwID,String name,String icon);

	public ResultBean getMatchUserWithPriority(String dwID,String name,String icon,boolean isPrioritized);

	@Transactional
	public ResultBean likeUser(String dwID,String likedID);

	public ListResultBean<LikeRecordDetail> getLikeRecords(String dwID);

	public void removeMatch(String dwID,String name,String icon);
}

