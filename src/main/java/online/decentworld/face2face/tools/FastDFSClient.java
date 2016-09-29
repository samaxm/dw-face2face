package online.decentworld.face2face.tools;

import online.decentworld.face2face.common.CommonProperties;
import online.decentworld.face2face.config.ConfigLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.IOException;

public class FastDFSClient implements FileManagerConfig{
	 
	  private static final long serialVersionUID = 1L;

	  private static Log logger  = LogFactory.getLog(FastDFSClient.class);
	  
	  private static TrackerClient  trackerClient;
	  private static TrackerServer  trackerServer;
	  private static StorageServer  storageServer;
	  private static StorageClient  storageClient;

	  static { // Initialize Fast DFS Client configurations
	    try {
	      String path=ConfigLoader.class.getClassLoader().getResource("").getPath();
	       path=path+CLIENT_CONFIG_FILE;
//	      path=new File().getCanonicalPath();
//	      logger.info("Fast DFS configuration file path:" + fdfsClientConfigFilePath);
//	      String path=ResourceUtils.getFile("classpath:"+CLIENT_CONFIG_FILE).getAbsolutePath();
//	      ClientGlobal.init(path+File.separator+CLIENT_CONFIG_FILE);
	      ClientGlobal.init(path);
	      trackerClient = new TrackerClient();
	      trackerServer = trackerClient.getConnection();
	      storageClient = new StorageClient(trackerServer, storageServer);
	    } catch (Exception e) {
	    	logger.warn("【FDFS_START_FAILED】", e);
	    }
	  }
	  

	  
	  public static String upload(byte[] content,String ext_name,NameValuePair[] meta_list) throws Exception {
		    String[] uploadResults = null;
		    try{
		    	uploadResults = storageClient.upload_file(content, ext_name, meta_list);
		    }catch(IOException io){
		    	//再次重试
		    	uploadResults = storageClient.upload_file(content, ext_name, meta_list);
		    }
		    if (uploadResults == null) {
		    	throw new Exception("no response");
		    }
		    String groupName 		= uploadResults[0];
		    String remoteFileName   = uploadResults[1];
		    String fileAbsolutePath = SEPARATOR + groupName + SEPARATOR + remoteFileName;
		    logger.debug("【UPLOAD_FILE】 url#"+fileAbsolutePath);
		    return fileAbsolutePath;
		  }
	  
	  public static void remove(String url) throws Exception{
		  int index=url.indexOf("group");
		  if(index==-1){
			  return;
		  }
		  url=url.substring(index);
		  int split=url.indexOf("/");
		  String groupName=url.substring(0,split);
		  int split2=url.lastIndexOf("/");
		  String fileName=url.substring(split2+1);
		  logger.debug("【REMOVE_FILE】 groupName#"+groupName+" fileName#"+fileName);
		  storageClient.delete_file(groupName,fileName);
	  }
	  
	  public static String upload(byte[] content,String ext_name,NameValuePair[] meta_list,String masterGroupName,String masterName,String prefix) {
		    String[] uploadResults = null;
		    try {
		      uploadResults = storageClient.upload_file(masterGroupName,masterName,prefix,content, ext_name, meta_list);
		    }catch (Exception e) {
		    }
		    if (uploadResults == null) {
		    }
		    String groupName 		= uploadResults[0];
		    String remoteFileName   = uploadResults[1];
		    String fileAbsolutePath = SEPARATOR + groupName + SEPARATOR + remoteFileName;
		    return fileAbsolutePath;
	}
	  
	  
	  
	  public static FileInfo getFile(String groupName, String remoteFileName) {
	    try {
//	      return storageClient.get_file_info(groupName, remoteFileName);
	      byte[] b=storageClient.download_file(groupName, remoteFileName);
	      System.out.println(new String(b));
	    } catch (IOException e) {
	    	e.printStackTrace();
//	      logger.error("IO Exception: Get File from Fast DFS failed", e);
	    } catch (Exception e) {
	    	e.printStackTrace();
//	      logger.error("Non IO Exception: Get File from Fast DFS failed", e);
	    }
	    return null;
	  }
	  
	  public static void deleteFile(String groupName, String remoteFileName) throws Exception {
	    storageClient.delete_file(groupName, remoteFileName);
	  }
	  
	  public static StorageServer[] getStoreStorages(String groupName) throws IOException {
	    return trackerClient.getStoreStorages(trackerServer, groupName);
	  }
	  
	  public static ServerInfo[] getFetchStorages(String groupName, String remoteFileName) throws IOException {
	    return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
	  }

	public static String getFullURL(String url){
		return CommonProperties.HTTP+ ConfigLoader.DomainConfig.FDFS_DOMAIN+url;
	}

	public static void deleteByFullName(String url){
		String sub=url.substring(url.indexOf("group"));
		try {
			String group=sub.substring(0,sub.indexOf("/"));
			String file=sub.substring(sub.indexOf("/") + 1);
			deleteFile(group,file);
		} catch (Exception e) {
			logger.info("[DELETE_FILE_FAILED]",e);
		}
	}

	public static void main(String[] args) {
		deleteByFullName("http://dev.service.dawan.online//group1/M00/00/12/cEodc1ftBdSASWZfAABxeKsD33E118.jpg");
	}
}
