package online.decentworld.face2face.controller;

import online.decentworld.face2face.service.history.IMessageHistroyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Sammax on 2016/10/5.
 */
@Controller
@RequestMapping("/history")
public class HistoryController {

    private static Logger logger= LoggerFactory.getLogger(HistoryController.class);
    @Autowired
    private IMessageHistroyService messageHistroyService;

    @RequestMapping("/messages")
    public void getMessageHistroy(String dwID,String contactID,HttpServletResponse response){
        byte[] data=messageHistroyService.getChatRecords(dwID,contactID,0);
        OutputStream outputStream=null;
        try{
            outputStream=response.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        }catch (Exception e){
            logger.debug("[WRITE_FAILED] dwID#" + dwID, e);
        }finally {
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.debug("",e);
                }
            }
        }
    }

}
