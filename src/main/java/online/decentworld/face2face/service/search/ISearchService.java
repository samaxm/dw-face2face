package online.decentworld.face2face.service.search;

import online.decentworld.rdb.entity.BaseDisplayUserInfo;

import java.util.List;

/**
 * Created by Sammax on 2016/11/8.
 */
public interface ISearchService {

    List searchWholeContext(String searchValue,int page);

    boolean saveOrUpdateIndex(BaseDisplayUserInfo info);
}
