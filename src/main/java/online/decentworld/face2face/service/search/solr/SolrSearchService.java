package online.decentworld.face2face.service.search.solr;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public boolean saveOrUpdateIndex(BaseDisplayUserInfo userInfo) {
        if(userInfo==null){
            return false;
        }
        HttpSolrClient client=SolrSearchClient.getSolrClient();
        try {
            client.addBean(SolrUserInfoBean.convert(userInfo));
            client.optimize();
            client.commit();
            return true;

        } catch (IOException | SolrServerException e) {
            logger.warn("[UPDATE_FAILED] info#"+ JSON.toJSONString(userInfo),e);
            return false;
        }
    }
}
