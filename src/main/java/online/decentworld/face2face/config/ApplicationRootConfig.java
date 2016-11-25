package online.decentworld.face2face.config;

import online.decentworld.cache.config.CacheBeanConfig;
import online.decentworld.charge.service.IOrderService;
import online.decentworld.charge.service.spi.OrderService;
import online.decentworld.rdb.config.DBConfig;
import online.decentworld.rdb.mapper.OrderMapper;
import online.decentworld.rpc.codc.Codec;
import online.decentworld.rpc.codc.MessageConverterFactory;
import online.decentworld.rpc.codc.ReflectConverterFactory;
import online.decentworld.rpc.codc.protos.SimpleProtosCodec;
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
@ComponentScan(basePackages={"online.decentworld.*"},excludeFilters={
		@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)
})
@EnableTransactionManagement
@Import( value = {DBConfig.class,CacheBeanConfig.class})
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
	public MessageConverterFactory codecFactory(){
		return new ReflectConverterFactory();
	}

	@Bean
	public Codec getCodec(MessageConverterFactory codecFactory){
		SimpleProtosCodec codec= new SimpleProtosCodec();
		codec.setConverterFactory(codecFactory);
		return codec;
	}

//	@Bean
//	public Sender getSender(){
//
////		PooledActiveMQSender sender=new PooledActiveMQSender();
////		sender.setCodec(codec);
////		return sender;
//	}
}
