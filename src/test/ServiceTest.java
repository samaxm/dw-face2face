import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.config.ApplicationRootConfig;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.face2face.tools.UserInforTransfer;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.rpc.dto.message.Notice_RechargeMessageBody;
import online.decentworld.rpc.dto.message.Notice_TipMessageBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

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
        String dwID="1845581884";
        int amount=100;
        String tipedID="3251749164";
        BaseDisplayUserInfo info=userInfoService.getUserInfo(dwID);
        Notice_TipMessageBody tipMessageBody=new Notice_TipMessageBody(UserInforTransfer.transfer(info),amount,new Date());
        MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper(dwID,tipedID,tipMessageBody,0);
        System.out.println(JSON.toJSONString(messageWrapper));
        Notice_RechargeMessageBody rechargeMessageBody=new Notice_RechargeMessageBody(dwID,amount);
        MessageWrapper messageWrapper2=MessageWrapper.createMessageWrapper("",dwID,rechargeMessageBody,0);
        System.out.println(JSON.toJSONString(messageWrapper2));


//        System.out.println(ConfigLoader.AdminConfig.RSA_PRIVATE);
//        List<User> users=ds.getAllUsers();
////        User user=new User();
////        user.setId("4781801942");
////        user.setName("2016-11-09 13:54:49,046 DEBUG [online.decentworld.cache.config.CacheBeanConfig] - [INIT_REDIS_CACHE]");
//        users.forEach((User u)->{
//            if(u.getOpenid()!=null){
//                u.setAccount(u.getOpenid());
//                u.setAccountType(TransferAccountType.WXPAY.name());
//                ds.updateByPrimaryKeySelective(u);
//            }
//        });
    }
}
