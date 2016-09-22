package online.decentworld.face2face.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;
/**
 * 访问次数限制
 * @author Sammax
 *
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Frequency {
	/**
	 * million seconds
	 * @return
	 */
	int time() default 0;

	/**
	 * roof
	 * @return
	 */
	int limit() default 0;
	
}
