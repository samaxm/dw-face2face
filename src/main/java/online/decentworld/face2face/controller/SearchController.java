package online.decentworld.face2face.controller;

import online.decentworld.face2face.service.search.ISearchService;
import online.decentworld.rpc.dto.api.ListResultBean;
import online.decentworld.rpc.dto.api.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Sammax on 2016/11/8.
 */

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ISearchService searchService;

    @RequestMapping("/user")
    @ResponseBody
    public ResultBean searchUser(@RequestParam String keyword,Integer page){
        if(page==null){
            page=0;
        }
        ListResultBean result=new ListResultBean();
        result.setData(searchService.searchWholeContext(keyword,page));
        return result;
    }

}
