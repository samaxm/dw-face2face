package online.decentworld.face2face.config;

import static online.decentworld.face2face.tools.LogUtil.toLogString;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
/**
 * servlet config 
 * 
 * @author Sammax
 *
 */
public class Face2FaceWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer{

	private static Logger logger=LoggerFactory.getLogger(Face2FaceWebAppInitializer.class);
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		Class<?>[] rootConfig={ApplicationRootConfig.class};
		logger.debug("[LOADING ROOT] ROOT#"+toLogString(rootConfig));
		return rootConfig;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		Class<?>[] servletConfig={ServletConfig.class};
		logger.debug("[LOADING SERVLET] SERVLET#"+toLogString(servletConfig));
		return servletConfig;
	}
	
	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter filter=new CharacterEncodingFilter();
		filter.setEncoding("utf-8");
		filter.setForceEncoding(true);
		Filter[] filters={filter};
		logger.debug("[LOADING Filters] filters#"+toLogString(filters));
		return filters;
	}

	@Override
	protected String[] getServletMappings() {
		String[] mappingURL={"/"};
		logger.debug("[LOADING SERVLET MAPPING] URLS#"+toLogString(mappingURL));
		return mappingURL;
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement("f:/temp",2097152,4194304,0));
		registration.setLoadOnStartup(0);
	}
	
	

}
