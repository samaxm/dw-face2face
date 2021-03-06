package online.decentworld.face2face.controller;

import online.decentworld.face2face.annotation.Frequency;
import online.decentworld.face2face.service.match.IUserMatcherService;
import online.decentworld.face2face.service.match.MatchUserInfo;
import online.decentworld.rpc.dto.api.ObjectResultBean;
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

	@Frequency(limit=150,time=15000)
	@RequestMapping("/getMatch")
	@ResponseBody
	public ResultBean getMatchUser(@RequestParam String dwID,@RequestParam String name,String icon,HttpServletRequest request){
		return matchService.getMatchUser(dwID, name, icon);
	}

	@RequestMapping("/like")
	@ResponseBody
	public ResultBean like(@RequestParam String dwID,@RequestParam String likedID){
		return matchService.likeUser(dwID,likedID);
	}

	@RequestMapping("/like/requests")
	@ResponseBody
	public ResultBean likeRequest(@RequestParam String dwID){
		return matchService.getLikeRequests(dwID);
	}

	@RequestMapping("/like/records")
	@ResponseBody
	public ResultBean likeRecords(@RequestParam String dwID){
		return matchService.getLikeRecords(dwID);
	}

	@RequestMapping("/like/refuse")
	@ResponseBody
	public ResultBean refuseLike(@RequestParam String dwID,@RequestParam String likedID){
		return matchService.refuseLikeRequest(dwID,likedID);
	}

	@RequestMapping("/like/accept")
	@ResponseBody
	public ResultBean acceptLike(@RequestParam String dwID,@RequestParam String likedID){
		return matchService.acceptLikeRequest(dwID,likedID);
	}
//	@Frequency(limit=150,time=15000)
	@RequestMapping("/getMatch/v2")
	@ResponseBody
	public ResultBean getMatchUserWithPriority(@RequestParam String dwID,@RequestParam String name,String icon,String sign,String tag,@RequestParam boolean isPrioritized,HttpServletRequest request){
		return matchService.getMatchUserWithPriority(dwID,name,icon,sign,tag,isPrioritized);
	}


	@RequestMapping("/getMatch/vip")
	@ResponseBody
	public ResultBean getMatchVipUser(@RequestParam String dwID,@RequestParam String name,String icon,String sign,String tag){
		MatchUserInfo info=new MatchUserInfo(dwID,name,icon,sign,tag);
		return matchService.getVipMatch(info);
	}

//	@Frequency(limit=150,time=15000)
	@RequestMapping("/remove")
	@ResponseBody
	public ResultBean remove(@RequestParam String dwID,@RequestParam String name,String icon,String sign,String tag,@RequestParam boolean isPrioritized,HttpServletRequest request){
		matchService.removeMatch(dwID, name, icon,sign,tag);
		return ObjectResultBean.SUCCESS;
	}

	@RequestMapping("/remove/vip")
	@ResponseBody
	public ResultBean removeVip(@RequestParam String dwID,@RequestParam String name,String icon,String sign,String tag,HttpServletRequest request){
		matchService.removeVIPMatch(new MatchUserInfo(dwID,name,icon, sign,tag));
		return ObjectResultBean.SUCCESS;
	}
}
