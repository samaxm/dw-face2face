package online.decentworld.face2face.service.security.advice.impl;

import static online.decentworld.face2face.common.CommonProperties.HTTP;
import static online.decentworld.face2face.common.StatusCode.FAILED;
import static online.decentworld.face2face.common.StatusCode.SUCCESS;
import static online.decentworld.face2face.config.ConfigLoader.DomainConfig.FDFS_DOMAIN;
import online.decentworld.face2face.common.FileSubfix;
import online.decentworld.face2face.service.security.advice.IAdviceService;
import online.decentworld.face2face.tools.FastDFSClient;
import online.decentworld.rdb.entity.CustomAdvice;
import online.decentworld.rdb.entity.CustomAdvice.AdviceType;
import online.decentworld.rdb.mapper.CustomAdviceMapper;
import online.decentworld.rpc.dto.api.ResultBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AdviceService implements IAdviceService{

	private static Logger logger=LoggerFactory.getLogger(AdviceService.class);
	@Autowired
	private CustomAdviceMapper adviceMappper;
	
	@Override
	public ResultBean recordAdivce(String dwID, Object content, AdviceType type) {
		ResultBean bean=new ResultBean();
		if(type!=AdviceType.VOICE){
			bean.setMsg("内容类型错误");
			bean.setStatusCode(FAILED);
		}else{
			byte[] data=(byte[])content;
			try{
				String url=FastDFSClient.upload(data, FileSubfix.AMR, null);
				CustomAdvice advice=new CustomAdvice(dwID,type.toString(),HTTP+FDFS_DOMAIN+"/"+url);
				adviceMappper.insert(advice);
				bean.setStatusCode(SUCCESS);
			}catch(Exception ex){
				logger.warn("[UPLOAD_ADIVE_FAIL] dwID#"+dwID,ex);
				bean.setMsg("上传建议，请重试");
				bean.setStatusCode(FAILED);
			}
		}
		return bean;
	}

}
