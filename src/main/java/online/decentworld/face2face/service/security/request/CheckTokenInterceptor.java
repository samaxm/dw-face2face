package online.decentworld.face2face.service.security.request;

import online.decentworld.face2face.common.AttributeKey;
import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

/**
 * Created by Sammax on 2016/10/7.
 */
@Component(value = "checkTokenInterceptor")
public class CheckTokenInterceptor extends HandlerInterceptorAdapter {

    private HashSet<String> check_list;
    private static String DIRECT_KEY="RequestDirect";
    private static String APP_PATH="face2face";
    private static Logger logger= LoggerFactory.getLogger(CheckTokenInterceptor.class);
    @Autowired
    private IUserAuthorityService authorityService;

    @PostConstruct
    public void init(){
        check_list=new HashSet<>();
        check_list.add("/wealth");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri=((HttpServletRequest)request).getRequestURI();
        String direct=(String)request.getAttribute(DIRECT_KEY);
        String test=request.getParameter("test");
        //do not check out direct and test request
        if("1".equals(test)||"out".equals(direct)){
            return true;
        }else{
            String path=uri.substring(uri.indexOf(APP_PATH)+APP_PATH.length());
            if(check_list.contains(path)){
                String dwID=request.getParameter("dwID");
                String token=request.getParameter("token");
                if(dwID==null||token==null){
                    request.setAttribute(AttributeKey.isValidate,false);
                }else {
//                    if (authorityService.checkToken(dwID, token)) {
                        request.setAttribute(AttributeKey.dwID, dwID);
                        request.setAttribute(AttributeKey.isValidate, true);
//                    } else {
//                        logger.debug("[TOKEN_CHECK_FAIL]");
//                        request.setAttribute(AttributeKey.isValidate, false);
//                    }
                }
            }
            return true;
        }
    }
}
