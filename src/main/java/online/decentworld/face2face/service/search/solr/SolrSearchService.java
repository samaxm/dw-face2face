package online.decentworld.face2face.service.search.solr;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by Sammax on 2016/11/8.
 */
@Service
public class SolrSearchService implements ISearchService {

    private static Logger logger= LoggerFactory.getLogger(SolrSearchService.class);

    @Override
    public List searchWholeContext(String searchValue,int page) {
        HttpSolrClient client=SolrSearchClient.getSolrClient();
        SolrQuery query = new SolrQuery();
        query.setQuery("info_text:"+searchValue);
        query.setStart(50*page);
        query.setRows(50);
        List list;
        try {
            list = client.query(query).getBeans(SolrUserInfoBean.class);
            return list;
        } catch (Exception e){
            logger.warn("",e);
            return Collections.EMPTY_LIST;
        }

    }

    @Override
    public boolean saveOrUpdateIndex(User user) {
        if(user==null){
            return false;
        }
        try {

            HttpSolrClient client=SolrSearchClient.getSolrClient();
            BaseDisplayUserInfo userInfo=new BaseDisplayUserInfo(user);
            if(userInfo==null||userInfo.getName()==null||userInfo.getDwID()==null){
                return false;
            }
            client.addBean(SolrUserInfoBean.convert(userInfo));
            client.optimize();
            client.commit();
            return true;

        } catch (Exception e) {
            logger.warn("[UPDATE_FAILED] info#"+ JSON.toJSONString(user),e);
            return false;
        }
    }

    @Override
    public boolean batchSave(List<User> user) {
        if(user==null){
            return false;
        }
        try {

            HttpSolrClient client=SolrSearchClient.getSolrClient();
            user.forEach(u->{
                BaseDisplayUserInfo userInfo=new BaseDisplayUserInfo(u);
                if(userInfo!=null&&userInfo.getName()!=null&&userInfo.getDwID()!=null){
                    try {
                        client.addBean(SolrUserInfoBean.convert(userInfo));
                    } catch (Exception e) {
                        logger.debug("insert fail",e);
                    }
                }

            });
            client.optimize();
            client.commit();
            return true;
        } catch (Exception e) {
            logger.warn("[UPDATE_FAILED] info#"+ JSON.toJSONString(user),e);
            return false;
        }
    }
}
