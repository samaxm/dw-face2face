package online.decentworld.face2face.controller;

import javax.servlet.http.HttpServletRequest;

import online.decentworld.face2face.service.match.IUserMatcherService;
import online.decentworld.rpc.dto.api.ResultBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 获取匹配用户
 * @author Sammax
 *
 */
@RequestMapping("/match")
@Controller
public class MatchController {

	@Autowired
	private IUserMatcherService matchService;
	
//	@Frequency(limit=15,time=15000)
	@RequestMapping("/getMatch")
	@ResponseBody
	public ResultBean getMatchUser(@RequestParam String dwID,@RequestParam String name,@RequestParam String icon,HttpServletRequest request){
		return matchService.getMathUser(dwID,name,icon);	
	}
	
	
		
}
