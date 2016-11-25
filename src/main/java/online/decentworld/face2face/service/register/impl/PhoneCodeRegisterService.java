package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.common.RegisterChannel;
import online.decentworld.face2face.common.TokenType;
import online.decentworld.face2face.common.UserType;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.PhoneCodeRegisterInfo;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sammax on 2016/9/21.
 */
@Service(value = "phoneCodeRegisterService")
public class PhoneCodeRegisterService extends SaveNewUserService implements IRegisterService{

    private static Logger logger= LoggerFactory.getLogger(PhoneCodeRegisterService.class);
    @Autowired
    private ITokenCheckService tokenService;
    @Autowired
    private EasemobApiUtil easemobAPI;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WealthMapper wealthMapper;
    @Autowired
    private ISearchService searchService;

    @Override
    public ResultBean register(String info)  {
        ResultBean bean;
        PhoneCodeRegisterInfo infoBean=JSON.parseObject(info,PhoneCodeRegisterInfo.class);
        if(tokenService.checkToken(infoBean.getPhoneNum(), TokenType.BIND_PHONE, infoBean.getCode())){
            try {
                String password= AES.decode(infoBean.getPassword());
                String phone=infoBean.getPhoneNum();
                User user;
                user=userMapper.selectByPhoneNum(phone);
                if(user!=null){
                    return ResultBean.FAIL("手机号码已被绑定,请尝试找回密码");
                }
                String id= IDUtil.getDWID();
                user=new User(id,null,null,null,id,
                        password,null, CommonProperties.DEFAULT_WORTH,null,null,phone,0, UserType.UNCERTAIN.toString(),true, RegisterChannel.PHONE.name(),null,null,null,(byte)0);
                try {
                    return ObjectResultBean.SUCCESS(saveUser(user));
                }catch (RegisterFailException e){
                    return ObjectResultBean.FAIL(e.getMessage());
                }
            } catch (Exception e) {
                bean=ResultBean.FAIL("注册失败");
                logger.warn("[WX_REGISTER_RETRIVEINFO_FAILED]",e);
            }
        }else{
            bean=ResultBean.FAIL("手机验证码错误");
        }
        return bean;
    }

}
