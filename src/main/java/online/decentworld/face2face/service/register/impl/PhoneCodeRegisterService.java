package online.decentworld.face2face.service.register.impl;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import online.decentworld.face2face.api.easemob.EasemobApiUtil;
import online.decentworld.face2face.common.*;
import online.decentworld.face2face.exception.RegisterFailException;
import online.decentworld.face2face.service.register.IRegisterService;
import online.decentworld.face2face.service.register.PhoneCodeRegisterInfo;
import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.face2face.service.security.token.ITokenCheckService;
import online.decentworld.rdb.entity.User;
import online.decentworld.rdb.entity.Wealth;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rdb.mapper.WealthMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.IDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Created by Sammax on 2016/9/21.
 */
@Service(value = "phoneCodeRegisterService")
public class PhoneCodeRegisterService implements IRegisterService{

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
                easemobAPI.registerEasemobUser(id,password);
                id=easemobAPI.registerEasemobUser(id,password);
                user=new User(id,null,null,null,id,
                        password,null, CommonProperties.DEFAULT_WORTH,null,null,phone,0, UserType.UNCERTAIN.toString(),true, RegisterChannel.PHONE.name(),null,null,null,(byte)0);

                tryStoreUser(user);
                Wealth w=new Wealth();
                w.setDwid(id);
                w.setWealth(0);
                wealthMapper.insert(w);
                bean=new ObjectResultBean();
                bean.setStatusCode(StatusCode.SUCCESS);
                ((ObjectResultBean)bean).setData(resetFields(user));
            } catch (Exception e) {
                bean=ResultBean.FAIL("注册失败");
                logger.warn("[WX_REGISTER_RETRIVEINFO_FAILED]",e);
            }
        }else{
            bean=ResultBean.FAIL("手机验证码错误");
        }
        return bean;
    }

    private User trimName(User info){
        if(info.getName().length()>CommonProperties.MAX_NAME_LENGTH){
            info.setName(info.getName().substring(info.getName().length()-CommonProperties.MAX_NAME_LENGTH,info.getName().length()));
        }
        return info;
    }


    private void tryStoreUser(User user) throws RegisterFailException {
        int attemp=0;
        while(true){
            attemp++;
            trimName(user);
            try{
                userMapper.insert(user);
                break;
            }catch(Exception e){
                if(e instanceof MySQLIntegrityConstraintViolationException ||
                        e instanceof DuplicateKeyException){
                    logger.info("[REPEAT_NAME]");
                    user.setName(user.getName()+attemp);
                    user.setId(IDUtil.getDWID());
                }
            }
            //.....不會這麼倒霉有20個重名的註冊吧
            if(attemp>20){
                throw new RegisterFailException();
            }
        }
    }


    private User resetFields(User user){
        user.setPassword(null);
        return user;
    }

}
