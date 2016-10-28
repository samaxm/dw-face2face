import online.decentworld.face2face.config.ApplicationRootConfig;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.service.user.IUserInfoService;
import online.decentworld.rdb.entity.BaseDisplayUserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Test
    public void test() throws Exception {
//        System.out.println(ConfigLoader.AdminConfig.RSA_PRIVATE);
        for(int i=0;i<10;i++){
            BaseDisplayUserInfo info=userInfoService.getUserInfo("9344031574");
//            authorityService.getUserKey("123");
        }
    }
}
