package online.decentworld.face2face.service.security.authority.impl;

import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.common.StatusCode;
import online.decentworld.face2face.config.ConfigLoader;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.rdb.mapper.UserMapper;
import online.decentworld.rpc.dto.api.ObjectResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import online.decentworld.tools.AES;
import online.decentworld.tools.MD5;
import online.decentworld.tools.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service
public class RDBUserAuthorityService implements IUserAuthorityService{

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private SecurityCache securityCache;
	private static Logger logger= LoggerFactory.getLogger(RDBUserAuthorityService.class);
	final private static String RSA_PUBLIC ;
	final private static String RSA_PRIVATE ;

	static {
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		try {
			InputStream in = RDBUserAuthorityService.class.getClassLoader().getResourceAsStream(ConfigLoader.AdminConfig.RSA_PUBLIC);
			InputStream in2 = RDBUserAuthorityService.class.getClassLoader().getResourceAsStream(ConfigLoader.AdminConfig.RSA_PRIVATE);


			int c;
			while ((c = in.read()) != -1) {
				sb.append((char) c);
			}
			while ((c = in2.read()) != -1) {
				sb2.append((char) c);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		RSA_PUBLIC=sb.toString().replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n?|-+END PUBLIC KEY-+\\r?\\n?)", "").replace("\n", "");
		RSA_PRIVATE=sb2.toString().replaceAll("(-+BEGIN PRIVATE KEY-+\\r?\\n?|-+END PRIVATE KEY-+\\r?\\n?)", "").replace("\n", "");

	}

	public static void main(String[] args) {
		System.out.println(RDBUserAuthorityService.RSA_PUBLIC);
	}

	@Override
	public boolean checkPassword(String dwID, String password) {
		password= AES.decode(password);
		password= MD5.GetMD5Code(password);
		String storePWD=userMapper.getUserPassword(dwID);
		if(storePWD.equals(password))
			return true;
		else
			return false;
	}

	@Override
	public ObjectResultBean getRSAKey() {
		ObjectResultBean bean=new ObjectResultBean();
		bean.setStatusCode(StatusCode.SUCCESS);
		bean.setData(RSA_PUBLIC);
		return bean;
	}

	@Override
	public ResultBean uploadKey(String dwID, String password,String key) {
		ResultBean bean=new ResultBean();
		try{
			if(StringUtils.isEmpty(dwID)||StringUtils.isEmpty(password)||StringUtils.isEmpty(key)) {
				bean.setStatusCode(StatusCode.FAILED);
				bean.setMsg("null primary element!");
				return bean;
			}
			//AES解密password密文，还原原文
			boolean flag=checkPassword(dwID, password);
			if(!flag){
				bean.setStatusCode(StatusCode.FAILED);
				bean.setMsg("账号密码错误");
			}else{
				key= RSA.decrypt(key, RSA_PRIVATE, "UTF-8");
				securityCache.cacheAES(dwID,key);
				bean.setStatusCode(StatusCode.SUCCESS);
			}
		}catch(Exception ex){
			logger.error("", ex);
			bean.setStatusCode(StatusCode.FAILED);
			bean.setMsg("system error,key error!");
		}
		return bean;
	}

}
