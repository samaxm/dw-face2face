package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.service.match.IUserMatcherService;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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



	@Frequency(limit=15,time=15000)
	@RequestMapping("/getMatch")
	@ResponseBody
	public ResultBean getMatchUser(@RequestParam String dwID,@RequestParam String name,@RequestParam String icon,HttpServletRequest request){
		return matchService.getMatchUser(dwID, name, icon);
	}

	@RequestMapping("/like")
	@ResponseBody
	public ResultBean like(@RequestParam String dwID,@RequestParam String likedID){
		return matchService.likeUser(dwID,likedID);
	}


	@RequestMapping("/like/records")
	@ResponseBody
	public ResultBean likeRecords(@RequestParam String dwID){
		return matchService.getLikeRecords(dwID);
	}

	@Frequency(limit=15,time=15000)
	@RequestMapping("/getMatch/v2")
	@ResponseBody
	public ResultBean getMatchUserWithPriority(@RequestParam String dwID,@RequestParam String name,@RequestParam String icon,@RequestParam boolean isPrioritized,HttpServletRequest request){
		return matchService.getMatchUserWithPriority(dwID,name,icon,isPrioritized);
	}


}
