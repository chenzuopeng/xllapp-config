package org.xllapp.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import org.xllapp.config.util.BeanUtils;

/**
 * 此类用于存放框架相关的配置.
 *
 * @author dylan.chen Aug 20, 2014
 * 
 */
public abstract class BaseApplicationConfig implements InitializingBean {

	private BaseApplicationConfig originalApplicationConfig;

	@Value("${log.isDumpRequest:false}")
	@FieldDescription("是否dump请求")
	private boolean isDumpRequest = false;
	
	public boolean isDumpRequest() {
		return this.isDumpRequest;
	}

	public void resetProperty(String propertyName) throws Exception {
		Object originalValue = BeanUtils.getPropertyValueAsString(propertyName, this.originalApplicationConfig);
		BeanUtils.setPropertyValue(propertyName, this, originalValue);
	}

	public void resetPropertys() throws Exception {
		BeanUtils.copyObject(this.originalApplicationConfig, this, "originalApplicationConfig");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.originalApplicationConfig = BeanUtils.copyObject(this, "originalApplicationConfig");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

	public List<Field> getPropertys() {
		return BeanUtils.getFields(this.getClass(), "originalApplicationConfig");
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface FieldDescription {
		String value();
	}

}
