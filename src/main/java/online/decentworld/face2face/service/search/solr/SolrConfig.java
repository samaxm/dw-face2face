package online.decentworld.face2face.service.search.solr;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SolrConfig {

    private final static Logger logger = Logger.getLogger(SolrConfig.class);

    
    public static String solrHost;

    public static int solrConnectTimeout;

    public static int solrClientTimeout;
    
    public static int maxConnectionsPerHost;
    
    public static int maxTotalConnection;

    static {
        InputStream is = SolrConfig.class.getClassLoader()
                .getResourceAsStream("solr.properties");
        if (is != null) {
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                e.printStackTrace();
            }finally{
            	try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            
            solrHost = properties.getProperty("solr.host");
            String solrClientTimeoutStr = properties
                    .getProperty("solr.client.timeout");
            if (!StringUtils.isEmpty(solrClientTimeoutStr)) {
                solrClientTimeout = Integer.parseInt(solrClientTimeoutStr);
            }
            String solrConnectTimeoutStr = properties
                    .getProperty("solr.connect.timeout");
            if (!StringUtils.isEmpty(solrConnectTimeoutStr)) {
                solrConnectTimeout = Integer.parseInt(solrConnectTimeoutStr);
            }
            
            String maxConnectionsPerHostStr = properties
                    .getProperty("max.connections.perhost");
            if (!StringUtils.isEmpty(maxConnectionsPerHostStr)) {
                maxConnectionsPerHost = Integer.parseInt(maxConnectionsPerHostStr);
            }
            
            String maxTotalConnectionStr = properties
                    .getProperty("max.total.connection");
            if (!StringUtils.isEmpty(maxTotalConnectionStr)) {
                maxTotalConnection = Integer.parseInt(maxTotalConnectionStr);
            }

        }
    }
//    public static void main(String[] args) {
//		System.out.println(SolrConfig.solrClientTimeout);
//	}
}
