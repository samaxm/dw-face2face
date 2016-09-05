package online.decentworld.face2face.service.security.advice;

import online.decentworld.rdb.entity.CustomAdvice.AdviceType;
import online.decentworld.rpc.dto.api.ResultBean;

public interface IAdviceService {
	ResultBean recordAdivce(String dwID,Object content,AdviceType type);
}
