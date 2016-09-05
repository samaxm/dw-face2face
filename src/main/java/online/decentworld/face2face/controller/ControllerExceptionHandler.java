package online.decentworld.face2face.controller;

import static online.decentworld.face2face.common.StatusCode.FAILED;
import online.decentworld.rpc.dto.api.ResultBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	private static Logger logger=LoggerFactory.getLogger(ControllerExceptionHandler.class);
	
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResultBean handleException(Exception ex){
		logger.warn("[UNKNOWN EXCEPTION]",ex);
		ResultBean bean=new ResultBean();
		bean.setStatusCode(FAILED);
		bean.setMsg("系统错误");
		return bean;
	}
	
}
