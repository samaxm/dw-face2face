package online.decentworld.face2face.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * load config from properties files
 * @author Sammax
 *
 */
public class ConfigLoader {
	
	private static Logger logger=LoggerFactory.getLogger(ConfigLoader.class);
	final private static String API_CONFIG_FILE="api.properties";
	final private static String DATASOURCE_CONFIG_FILE= "dataSource.properties";
	final private static String CODIS_CONFIG_FILE="codis_config.properties";
	final private static String SECURITY_CONFIG_FILE="security.properties";
	final private static String AMIN_CONFIG_FILE="admin.properties";
	final private static String DOMAIN_CONFIG_FILE="domain.properties";


	public static class DataSourceConfig{
		static{
			Properties dataSourcePro=new Properties();
			try {
				dataSourcePro.load(ConfigLoader.class.getClassLoader().getResourceAsStream(DATASOURCE_CONFIG_FILE));
				for(String property:dataSourcePro.stringPropertyNames()){
					DataSourceConfig.class.getField(property).set(null, dataSourcePro.getProperty(property));;					
				}
				checkNull(DataSourceConfig.class);
			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		public static String ENVIORMENT;
	}
	
	public static class APIConfig{
		
		static{
			Properties apiPro=new Properties();
			try {
				apiPro.load(ConfigLoader.class.getClassLoader().getResourceAsStream(API_CONFIG_FILE));
				for(String property:apiPro.stringPropertyNames()){
					APIConfig.class.getField(property).set(null, apiPro.getProperty(property));;					
				}			
				checkNull(APIConfig.class);

			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		/**
		 * 環信API
		 */
		public static String APP_CLIENT_ID;
		public static String APP_CLIENT_SECRET;
		public static String APPKEY;
		public static String API_SERVER_HOST;
		public static String GROUP_NAME;
		public static String APP_NAME;
		/**
		 * 騰訊API
		 */
		public static String WX_APP_ID;
		public static String WX_ACCESS_TOKEN_URL;
		public static String WX_APP_SECRET;
		public static String GET_WX_USER_INFO;
		/**
		 * 阿里API
		 */
		public static String ALI_SMS_URL;
		public static String ALI_SMS_APPKEY;
		public static String ALI_SMS_APP_SECRET;
		public static String ALI_SMS_SIGN_TEMPLATE_DW;
		public static String ALI_SMS_REGISTER_NOTICE;
		public static String ALI_SMS_ADMIN_ALARM_NOTICE;
		public static String ALI_SMS_STAR_ACTIVE_NOTICE_TEMPLATE;
	}
	
	public static class CodisConfig{
		static{
			Properties codisPro=new Properties();
			try {
				codisPro.load(ConfigLoader.class.getClassLoader().getResourceAsStream(CODIS_CONFIG_FILE));
				for(String property:codisPro.stringPropertyNames()){
					CodisConfig.class.getField(property).set(null, codisPro.getProperty(property));;					
				}	
				checkNull(CodisConfig.class);
			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		public static String CODIS_ZK_CONNECTSTR;
		public static String CODIS_PROXY_NAMESPACE;
		
	}
	
	
	public static class SecurityConfig{
		static{
			Properties expirePro=new Properties();
			try {
				expirePro.load(SecurityConfig.class.getClassLoader().getResourceAsStream(SECURITY_CONFIG_FILE));
				for(String property:expirePro.stringPropertyNames()){
					SecurityConfig.class.getField(property).setInt(null, Integer.valueOf(expirePro.getProperty(property)));					
				}	
				checkNull(SecurityConfig.class);
			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		public static int REGISTER_CODE_EXPIRE;
		public static int REQUEST_LIMIT;
		public static int REQUEST_LIMIT_TIME;
		public static int REQUEST_CACHE_SIZE;
		public static int INVALID_IP_BLOCK_TIME;
		public static int MAX_REPORTED;
		public static int REQUEST_TIMEOUT;
		public static int BLOCK_TIME;
		public static int CACHE_EXPIRE;
		public static int TOKEN_EXPIRE;
		public static int AES_EXPIRE;
	}
	
	public static class AdminConfig{
		static{
			Properties expirePro=new Properties();
			try {
				expirePro.load(AdminConfig.class.getClassLoader().getResourceAsStream(AMIN_CONFIG_FILE));
				for(String property:expirePro.stringPropertyNames()){
					AdminConfig.class.getField(property).set(null, expirePro.getProperty(property));					
				}	
				checkNull(AdminConfig.class);
			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		public static String ADMIN_PHONE;
		public static String DEFAULT_MATCH_QUEUE_SIZE;
		public static String RSA_PUBLIC;
		public static String RSA_PRIVATE;
		public static String NEED_EASEMON;
	}
	
	public static class DomainConfig{
		static{
			Properties expirePro=new Properties();
			try {
				expirePro.load(DomainConfig.class.getClassLoader().getResourceAsStream(DOMAIN_CONFIG_FILE));
				for(String property:expirePro.stringPropertyNames()){
					DomainConfig.class.getField(property).set(null, expirePro.getProperty(property));					
				}	
				checkNull(AdminConfig.class);
			} catch (Exception e) {
				logger.error("[LOAD_CONFIG_FAILED]",e);
			}
		}
		public static String FDFS_DOMAIN;
		public static String MQ_DOMAIN;
	}
	
	private static void checkNull(Class<?> clazz){
		for(Field field:clazz.getDeclaredFields()){
			try {
				Object o=field.get(null);
				if(o==null){
					logger.warn("[NULL_PROPERTIES] property#"+field.getName());
					throw new RuntimeException();
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				logger.warn("[INIT_PROPERTIES_ERROR]",e);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(AdminConfig.DEFAULT_MATCH_QUEUE_SIZE);
	}
}
