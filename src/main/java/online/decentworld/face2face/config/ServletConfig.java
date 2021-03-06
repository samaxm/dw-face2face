package online.decentworld.face2face.config;

import com.alibaba.fastjson.JSON;
import online.decentworld.face2face.service.security.request.impl.LocalRequestLimit;
import online.decentworld.face2face.tools.WebLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 
 * @author Sammax
 *
 */
@Configuration
@EnableWebMvc
@EnableCaching
@ComponentScan(basePackages={"online.decentworld.face2face.controller"})
public class ServletConfig extends WebMvcConfigurerAdapter{

	@Resource(name = "checkTokenInterceptor")
	private HandlerInterceptor checkTokenInterceptor;

	@Bean
	public ViewResolver viewResolver(){
		InternalResourceViewResolver resolver=new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		resolver.setExposeContextBeansAsAttributes(true);
		return resolver;
	}
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(){
		RequestMappingHandlerAdapter handlerAdapter=new RequestMappingHandlerAdapter();
		handlerAdapter.setAsyncRequestTimeout(20000);
		List<HttpMessageConverter<?>> list=new LinkedList<HttpMessageConverter<?>>();
		MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
		list.add(converter);
		handlerAdapter.setMessageConverters(list);
		
		ResponseBodyAdvice<Object> advice=new ResponseBodyAdvice<Object>() {
			private Logger logger=LoggerFactory.getLogger("online.decentworld.response.advice");
			
			@Override
			public boolean supports(MethodParameter returnType,
					Class<? extends HttpMessageConverter<?>> converterType) {
				return true;
			}

			@Override
			public Object beforeBodyWrite(
					Object body,
					MethodParameter returnType,
					MediaType selectedContentType,
					Class<? extends HttpMessageConverter<?>> selectedConverterType,
					ServerHttpRequest request, ServerHttpResponse response) {
				logger.debug("[CLASS:"+returnType.getContainingClass().getSimpleName().toUpperCase()+" METHOD:"+returnType.getMethod().getName().toUpperCase()+
						"] PARAMS#"+ WebLogUtil.toLogString(request)+" RETURN#"+JSON.toJSONString(body));

				return body;
			}
		};
		List<ResponseBodyAdvice<?>> responseAdvices=new LinkedList<ResponseBodyAdvice<?>>();
		responseAdvices.add(advice);
		handlerAdapter.setResponseBodyAdvice(responseAdvices);
		return handlerAdapter;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		registry.addInterceptor(new LocalRequestLimit());
		registry.addInterceptor(checkTokenInterceptor);
	}
	
	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();

	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
		registry.addResourceHandler("/view/**").addResourceLocations("/WEB-INF/view/").setCacheControl(CacheControl.noCache());
		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");


	}

	/**
	 * 异常脑残。。方法名字必须这样
	 * @return
	 */
	@Bean
	public MultipartResolver multipartResolver(){
			return new StandardServletMultipartResolver();
	}
}
