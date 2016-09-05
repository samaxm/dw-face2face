package online.decentworld.face2face.controller;

import online.decentworld.face2face.service.match.IRetrivePaddingContentService;
import online.decentworld.rpc.dto.api.ListResultBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/padding")
@Controller
public class PaddingController {

	@Autowired
	private IRetrivePaddingContentService paddingService;
	
	
	@RequestMapping("/jokes")
	@ResponseBody
//	@Frequency(limit=10,time=60000)
	public ListResultBean<String> retriveJokes(@RequestParam int index){
		return paddingService.retrivePaddingContent(index);
	}
	
}
