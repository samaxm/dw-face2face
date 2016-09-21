package online.decentworld.face2face.service.register;

import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.transaction.annotation.Transactional;

public interface IRegisterService {
	@Transactional
	ResultBean register(String info);
}
