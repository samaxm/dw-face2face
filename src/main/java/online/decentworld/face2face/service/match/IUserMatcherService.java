package online.decentworld.face2face.service.match;

import online.decentworld.rpc.dto.api.ResultBean;

/**
 * 匹配用戶
 * @author Sammax
 *
 */
public interface IUserMatcherService {
	
	public ResultBean getMathUser(String dwID,String name,String icon);
	
}

