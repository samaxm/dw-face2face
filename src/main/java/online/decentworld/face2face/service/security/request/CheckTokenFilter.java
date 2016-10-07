package online.decentworld.face2face.service.security.request;

import online.decentworld.face2face.service.security.authority.IUserAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;

/**
 * Created by Sammax on 2016/10/7.
 */
@Component
public class CheckTokenFilter implements Filter {

    private HashSet<String> check_list;
    private static String DIRECT_KEY="RequestDirect";
    private static String APP_PATH="face2face";
    private static Logger logger= LoggerFactory.getLogger(CheckTokenFilter.class);
    @Autowired
    private IUserAuthorityService authorityService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        check_list=new HashSet<>();
        check_list.add("/wealth");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri=((HttpServletRequest)request).getRequestURI();
        String direct=(String)request.getAttribute(DIRECT_KEY);
        String test=request.getParameter("test");
        //do not check out direct and test request
        if("1".equals(test)||"out".equals(direct)){
            chain.doFilter(request, response);
        }else{
            String path=uri.substring(uri.indexOf(APP_PATH)+APP_PATH.length());
            if(check_list.contains(path)){
                String dwID=request.getParameter("dwID");
                String token=request.getParameter("token");
                if(authorityService.checkToken(dwID,token)){
                    request.setAttribute("dwID",dwID);
                }else{
                    logger.debug("[TOKEN_CHECK_FAIL]");
                }
            }
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }



}
