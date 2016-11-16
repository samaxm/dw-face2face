package online.decentworld.face2face.config;

import online.decentworld.cache.config.CacheBeanConfig;
import online.decentworld.charge.ChargeService;
import online.decentworld.charge.ChargeServiceTemplate;
import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.spi.OrderService;
import online.decentworld.rdb.config.DBConfig;
import online.decentworld.rdb.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;


/**
 * @author Sammax
 */
@Configuration
@ComponentScan(basePackages={"online.decentworld.face2face.*"},excludeFilters={
		@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)
})
@EnableTransactionManagement
@Import(value = {DBConfig.class,CacheBeanConfig.class})
public class ApplicationRootConfig {
	
	@SuppressWarnings("unused")
	private static Logger logger=LoggerFactory.getLogger(ApplicationRootConfig.class);
	
	@Bean
	public DataSourceTransactionManager getTXManager(DataSource ds){
		DataSourceTransactionManager manager=new DataSourceTransactionManager();
		manager.setDataSource(ds);
		return manager;
	}

	@Bean
	public IOrderService getOrderService(OrderMapper orderMapper){
		OrderService orderService=new OrderService();
		orderService.setOrderMapper(orderMapper);
		return orderService;
	}

	@Bean
	public ChargeService getChargeService(WealthMapper wealthMapper,ConsumePriceMapper consumePriceMapper,OrderMapper orderMapper,TransferHistoryMapper transferHistoryMapper,TipRecordsMapper tipRecordsMapper){
		return ChargeServiceTemplate.defaultService(wealthMapper,consumePriceMapper,orderMapper,transferHistoryMapper,tipRecordsMapper);
	}


//	@Bean
//	public Sender getSender(){
//		SimpleProtosCodec codec= new SimpleProtosCodec();
//		codec.setConverterFactory(new ReflectConverterFactory());
//		PooledActiveMQSender sender=new PooledActiveMQSender();
//		sender.setCodec(codec);
//		return sender;
//	}
}
