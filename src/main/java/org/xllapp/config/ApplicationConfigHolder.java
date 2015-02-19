package org.xllapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author dylan.chen Aug 20, 2014
 * 
 */
@Lazy(false)
@Component
public class ApplicationConfigHolder implements ApplicationContextAware {

	private final static Logger logger = LoggerFactory.getLogger(ApplicationConfigHolder.class);

	private static BaseApplicationConfig applicationConfig;

	@SuppressWarnings("unchecked")
	public static <T> T getApplicationConfig() {
		return (T) applicationConfig;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		applicationConfig = applicationContext.getBean(BaseApplicationConfig.class);
		logger.info("loaded initial application config:{}", applicationConfig);
	}

}
