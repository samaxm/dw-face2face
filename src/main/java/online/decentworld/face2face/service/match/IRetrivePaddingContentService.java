package online.decentworld.face2face.service.match;

import online.decentworld.rpc.dto.api.ListResultBean;

public interface IRetrivePaddingContentService {
	ListResultBean<String> retrivePaddingContent(int index);
}
