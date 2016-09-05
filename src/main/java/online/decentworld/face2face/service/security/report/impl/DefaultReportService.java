package online.decentworld.face2face.service.security.report.impl;

import static online.decentworld.face2face.common.StatusCode.SUCCESS;
import static online.decentworld.face2face.config.ConfigLoader.SecurityConfig.BLOCK_TIME;
import static online.decentworld.face2face.config.ConfigLoader.SecurityConfig.MAX_REPORTED;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import online.decentworld.face2face.cache.SecurityCache;
import online.decentworld.face2face.service.security.report.IReportService;
import online.decentworld.face2face.tools.DateFormater;
import online.decentworld.rdb.entity.ReportRecord;
import online.decentworld.rdb.mapper.ReportRecordMapper;
import online.decentworld.rpc.dto.api.ResultBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultReportService implements IReportService{

	private static Logger logger=LoggerFactory.getLogger(DefaultReportService.class);
	@Autowired
	private ReportRecordMapper reportMapper;
	@Autowired
	private SecurityCache securityCache;
	
	private ConcurrentHashMap<String,Long> blockTable=new ConcurrentHashMap<String, Long>();
	
	@Override
	public ResultBean reportUser(String reporterID, String reportedID,
			String reason) {
		ReportRecord record=new ReportRecord(reporterID, reportedID, reason);
		reportMapper.insert(record);
		int reportedTimes=securityCache.incrReportNum(reportedID);
		if(reportedTimes<-1){
			reportedTimes=reportMapper.countReportedTimes(reportedID);
		}
		if(reportedTimes>MAX_REPORTED){
			logger.info("[BLOCK_USER] reportedID#"+reportedID+" time#"+DateFormater.formatReadableString(new Date()));
			blockTable.put(reportedID, System.currentTimeMillis());
		}
		ResultBean bean=new ResultBean();
		bean.setStatusCode(SUCCESS);
		return bean;
	}

	@Override
	public boolean isUserBlock(String dwID) {
		Long reportTime=blockTable.get(dwID);
		if(reportTime!=null){
			if(System.currentTimeMillis()-reportTime>BLOCK_TIME){
				blockTable.remove(dwID);
				return false;
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	public ResultBean customerAdvise(String dwID, InputStream is) {
		return null;
	}

}
