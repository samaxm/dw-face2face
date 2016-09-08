package online.decentworld.face2face.service.match;

import online.decentworld.rdb.entity.LikeRecord;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * 匹配用戶
 * @author Sammax
 *
 */
public interface IUserMatcherService {
	
	public ResultBean getMathUser(String dwID,String name,String icon);

	@Transactional
	public ResultBean likeUser(String dwID,String likedID);

	public ListResultBean<LikeRecord> getLikeRecords(String dwID);
}

