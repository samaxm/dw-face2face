import com.alibaba.fastjson.JSON;
import online.decentworld.rpc.dto.message.MessageWrapper;
import online.decentworld.rpc.dto.message.Notice_VipRenewMessageBody;

import java.util.Date;

/**
 * Created by Sammax on 2016/9/9.
 */
public class JSONTest {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString( MessageWrapper.createMessageWrapper("SYSTEM", "1234567890", new Notice_VipRenewMessageBody("1234567890", new Date(),new Date(), 10000), 0)));
    }

}
