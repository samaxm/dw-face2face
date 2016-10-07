import com.alibaba.fastjson.JSON;
import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.OrderReceipt;
import online.decentworld.charge.service.OrderType;
import online.decentworld.charge.service.PayChannel;
import online.decentworld.charge.service.spi.OrderService;
import online.decentworld.face2face.config.ApplicationRootConfig;
import online.decentworld.face2face.config.ConfigLoader;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.rdb.mapper.OrderMapper;
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
    private OrderMapper mapper;
    @Autowired
    private IUserAuthorityService authorityService;

    @Test
    public void test() throws Exception {
        System.out.println(ConfigLoader.AdminConfig.RSA_PRIVATE);


    }
}
