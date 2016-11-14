import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.TransferAccountType;
import online.decentworld.face2face.config.ApplicationRootConfig;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Sammax on 2016/9/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ApplicationRootConfig.class})
public class ServiceTest {

    @Autowired
    private IUserAuthorityService authorityService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private IWealthService wealthService;
    @Autowired
    private ISearchService solrSearchService;
    @Autowired
    private UserMapper ds;

    @Test
    public void test() throws Exception {

//        System.out.println(ConfigLoader.AdminConfig.RSA_PRIVATE);
        List<User> users=ds.getAllUsers();
////        User user=new User();
////        user.setId("4781801942");
////        user.setName("2016-11-09 13:54:49,046 DEBUG [online.decentworld.cache.config.CacheBeanConfig] - [INIT_REDIS_CACHE]");
        users.forEach((User u)->{
            if(u.getOpenid()!=null){
                u.setAccount(u.getOpenid());
                u.setAccountType(TransferAccountType.WXPAY.name());
                ds.updateByPrimaryKeySelective(u);
            }
        });
    }
}
