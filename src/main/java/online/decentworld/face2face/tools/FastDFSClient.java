package online.decentworld.face2face.tools;

import java.io.IOException;

import online.decentworld.face2face.config.ConfigLoader;

import org.csource.common.NameValuePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastDFSClient implements FileManagerConfig{
	 
	  private static final long serialVersionUID = 1L;

	  private static Log logger  = LogFactory.getLog(FastDFSClient.class);
	  
	  private static TrackerClient  trackerClient;
	  private static TrackerServer  trackerServer;
	  private static StorageServer  storageServer;
	  private static StorageClient  storageClient;

	  static { // Initialize Fast DFS Client configurations
	    try {
	      String path=ConfigLoader.class.getResource("").getPath();
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
	  
	  public static void main(String[] args) {
		  try {
			Class.forName("online.decentworld.face2face.tools.FastDFSClient");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
