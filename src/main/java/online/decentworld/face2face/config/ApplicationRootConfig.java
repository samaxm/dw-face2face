package online.decentworld.face2face.config;

import javax.sql.DataSource;

import online.decentworld.rdb.config.DBConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * @author Sammax
 */
@Configuration
@EnableCaching
@ComponentScan(basePackages={"online.decentworld.face2face.*"},excludeFilters={
		@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)
})
@EnableTransactionManagement
@Import(DBConfig.class)
public class ApplicationRootConfig {
	
	@SuppressWarnings("unused")
	private static Logger logger=LoggerFactory.getLogger(ApplicationRootConfig.class);
	
	@Bean
	public DataSourceTransactionManager getTXManager(DataSource ds){
		DataSourceTransactionManager manager=new DataSourceTransactionManager();
		manager.setDataSource(ds);
		return manager;
	}
}
