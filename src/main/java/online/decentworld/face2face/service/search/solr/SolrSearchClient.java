package online.decentworld.face2face.service.search.solr;


import org.apache.solr.client.solrj.impl.HttpSolrClient;

/**
 * 尚未创建连接池
 * @author Sammax
 *
 */
public class SolrSearchClient {
	private static HttpSolrClient solrClient;
    private static String solrHost = SolrConfig.solrHost;
    private static int solrClientTimeout = SolrConfig.solrClientTimeout;
    private static int solrConnectTimeout = SolrConfig.solrConnectTimeout;
    private static int maxConnectionsPerHost = SolrConfig.maxConnectionsPerHost;
    private static int maxTotalConnection = SolrConfig.maxTotalConnection;
    static{
    	init();
    }
    private SolrSearchClient() {
    }
    
    private  static void init(){
    	solrClient = new HttpSolrClient(solrHost);
        solrClient.setSoTimeout(solrClientTimeout);  // socket read timeout
        solrClient.setConnectionTimeout(solrConnectTimeout);
        solrClient.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
        solrClient.setMaxTotalConnections(maxTotalConnection);
        solrClient.setFollowRedirects(false);  // defaults to false
         //allowCompression defaults to false.
         //Server side must support gzip or deflate for this to have any effect.
        solrClient.setAllowCompression(true);
    }
    public static HttpSolrClient getSolrClient() {
         return solrClient;
    }

  
}
