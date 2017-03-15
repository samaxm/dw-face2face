import online.decentworld.charge.charger.DBCharger;
import online.decentworld.face2face.cache.ActivityCache;
import online.decentworld.face2face.cache.UserInfoCache;
import online.decentworld.face2face.config.ApplicationRootConfig;
import online.decentworld.face2face.service.app.IAppService;
import online.decentworld.face2face.service.history.IMessageHistroyService;
import online.decentworld.face2face.service.register.IVipRegisterCheckService;
import online.decentworld.face2face.service.register.RegisterStrategyFactory;
import online.decentworld.face2face.service.search.solr.SolrSearchService;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.face2face.service.wealth.IWealthService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
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
    private UserMapper ds;
    @Autowired
    private LikeRecordMapper mapper;
    @Autowired
    private IMessageHistroyService messageHistroyService;
    @Autowired
    private IAppService appService;
    @Autowired
    private IVipRegisterCheckService vipRegisterCheckService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RegisterStrategyFactory registerService;
    @Autowired
    private WealthMapper wealthMapper;
    @Autowired
    private DBCharger dbCharger;
    @Autowired
    private VipRecordsMapper vipRecordsMapper;
    @Autowired
    private ActivityCache activityCache;
    @Autowired
    private SolrSearchService solrSearchService;
    @Autowired
    private UserInfoCache userInfoCache;

    @Test
    public void insertSolr(){
        List<User> user=ds.getAllUsers();
        solrSearchService.batchSave(user);
    }

    @Test
    public void insertTagsAndGet(){
        List<String> list=new ArrayList<>(11);
        list.add("腹黑直男");
        list.add("高能鬼畜逗逼");
        list.add("说闹觉余");
        list.add("怪蜀黍");
        list.add("软妹子~~~");
        list.add("会算命");
        list.add("傲娇御姐");
        list.add("毒舌腐女");
        list.add("战五渣");
        list.add("伸手党");
        list.add("脸盲症患者");
        userInfoCache.setTags(list);
        List<String> tags=userInfoCache.getUserTags();
        for(String tag:tags){
            System.out.println(tag);
        }
    }

    @Test
    public void test2(){
        String dwID="0334631949";
        User user=new User();
        user.setId(dwID);
        user.setIcon("123123");
        userInfoService.updateUserInfo(user,1);


    }

    @Test
    public void test() throws Exception {
        activityCache.writeAnswerCache(42);
//        User user=ds.selectByPrimaryKey("0215043174");
//        user.setPayPassword(null);
//        user.setPassword(null);
//        VipRecords vipRecords=vipRecordsMapper.selectByPrimaryKey("0215043174");
//        UserInfoWithVipInfo infoWithVipInfo=new UserInfoWithVipInfo(user,vipRecords);
//        System.out.println(JSON.toJSONString(infoWithVipInfo));

//        JSONObject jsonObject=new JSONObject();
//        jsonObject.put("registerType","VIP");
//
//        VipRegisterInfo info=new VipRegisterInfo();
//        info.setFormat("PHONECODE");
//        info.setCode("123");
//        info.setRegisterInfo(JSON.toJSONString(new PhoneCodeRegisterInfo("13714179589", AES.encode("123"),"2570")));
//        jsonObject.put("registerInfo",JSON.toJSONString(info));
//
//        registerService.getService(RegisterType.VIP.name()).register(JSON.toJSONString(info));

//        System.out.println(JSON.toJSONString(list));
//        System.out.println(JSON.toJSONString(mapper.getLikeRecords("0334631949")));
//        String dwID="1845581884";
//        int amount=100;
//        String tipedID="3251749164";
//        BaseDisplayUserInfo info=userInfoService.getUserInfo(dwID);
//        Notice_TipMessageBody tipMessageBody=new Notice_TipMessageBody(UserInforTransfer.transfer(info),amount,new Date());
//        MessageWrapper messageWrapper=MessageWrapper.createMessageWrapper(dwID,tipedID,tipMessageBody,0);
//        System.out.println(JSON.toJSONString(messageWrapper));
//        Notice_RechargeMessageBody rechargeMessageBody=new Notice_RechargeMessageBody(dwID,amount);
//        MessageWrapper messageWrapper2=MessageWrapper.createMessageWrapper("",dwID,rechargeMessageBody,0);
//        System.out.println(JSON.toJSONString(messageWrapper2));


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
