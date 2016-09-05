package online.decentworld.face2face.service.security.report;

import java.io.InputStream;

import online.decentworld.rpc.dto.api.ResultBean;

public interface IReportService {
	
	ResultBean reportUser(String reporterID,String reportedID,String reason);
	
	boolean isUserBlock(String dwID);
	
	ResultBean customerAdvise(String dwID,InputStream is);
}
