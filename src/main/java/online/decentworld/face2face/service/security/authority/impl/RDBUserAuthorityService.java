package online.decentworld.face2face.service.security.authority.impl;

import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import online.decentworld.face2face.tools.MD5;
import online.decentworld.rdb.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RDBUserAuthorityService implements IUserAuthorityService{

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public boolean checkPassword(String dwID, String password) {
		password=MD5.GetMD5Code(password);
		String storePWD=userMapper.getUserPassword(dwID);
		if(storePWD.equals(password))
			return true;
		else
			return false;
	}
	
}
