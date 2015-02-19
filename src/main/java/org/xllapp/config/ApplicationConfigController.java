package org.xllapp.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xllapp.config.ApplicationConfigHolder;
import org.xllapp.config.BaseApplicationConfig;
import org.xllapp.config.BaseApplicationConfig.FieldDescription;
import org.xllapp.config.util.BeanUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * 修改应用配置信息.
 *
 * @author dylan.chen Aug 25, 2014
 * 
 */
@Controller
public class ApplicationConfigController implements HttpRequestHandler {

	private final static Logger logger = LoggerFactory.getLogger(ApplicationConfigController.class);

	@RequestMapping("/applicationConfig")
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getMethod();
		try {
			if ("get".equalsIgnoreCase(method)) {
				doGet(request, response);
			} else {
				doPost(request, response);
			}
		} catch (Throwable throwable) {
			doException(request, response, throwable);
		}
	}

	private void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String act = request.getParameter("act");
		if (StringUtils.isNotBlank(act)) {
			doPost(request, response);
		} else {
			Map<String, Object> model = new HashMap<String, Object>();
			List<ConfigItem> configItems = resolveConfigItems();
			model.put("configItems", configItems);
			out(response, processFtl(model));
		}
	}

	private void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String act = request.getParameter("act");
		if ("reset".equalsIgnoreCase(act)) {
			doReset(request, response);
		} else if ("reset_all".equalsIgnoreCase(act)) {
			doResetAll(request, response);
		} else {
			doModify(request, response);
		}
		response.sendRedirect(request.getRequestURL().toString());
	}

	private void doModify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String propertyName = request.getParameter("configItem.name");
		String propertyValue = request.getParameter("configItem.value");
		Object applicationConfig = getConfigBean();
		String oldPropertyValue = BeanUtils.getPropertyValueAsString(propertyName, applicationConfig);
		saveConfigValue(propertyName, propertyValue);
		logger.info("modified property[{},new value[{}],old value[{}]] - {}", propertyName, propertyValue, oldPropertyValue, applicationConfig);
	}

	private void doReset(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String propertyName = request.getParameter("configItem.name");
		BaseApplicationConfig applicationConfig = getConfigBean();
		Object currPropertyValue = BeanUtils.getPropertyValueAsString(propertyName, applicationConfig);
		applicationConfig.resetProperty(propertyName);
		Object origPropertyValue = BeanUtils.getPropertyValueAsString(propertyName, applicationConfig);
		logger.info("reseted property[{},current value[{}],original value[{}]] - {}", propertyName, currPropertyValue, origPropertyValue, applicationConfig);
	}

	private void doResetAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BaseApplicationConfig applicationConfig = getConfigBean();
		Object currentApplicationConfig = BeanUtils.copyObject(applicationConfig);
		applicationConfig.resetPropertys();
		logger.info("reseted all properties - current values[{}],new values[{}]", currentApplicationConfig, applicationConfig);
	}

	private List<ConfigItem> resolveConfigItems() throws Exception {
		List<ConfigItem> configItems = new ArrayList<ApplicationConfigController.ConfigItem>();
		BaseApplicationConfig applicationConfig = getConfigBean();
		List<Field> fields = applicationConfig.getPropertys();
		for (Field field : fields) {
			String propertyName = field.getName();
			ConfigItem configItem = new ConfigItem();
			configItem.setName(propertyName);
			configItem.setValue(BeanUtils.getPropertyValueAsString(propertyName, applicationConfig));
			FieldDescription fieldDescription = field.getAnnotation(FieldDescription.class);
			if (null != fieldDescription) {
				configItem.setDesc(fieldDescription.value());
			}
			configItems.add(configItem);
		}
		return configItems;
	}

	private void saveConfigValue(String propertyName, String propertyValue) throws Exception {
		Object configBean = getConfigBean();
		BeanUtils.setPropertyValue(propertyName, configBean, propertyValue);
	}

	private BaseApplicationConfig getConfigBean() {
		return ApplicationConfigHolder.getApplicationConfig();
	}
	
	private void doException(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("throwableMessage", throwable.getLocalizedMessage());
		String strThrowable = ExceptionUtils.getStackTrace(throwable);
		model.put("throwableStackTrace", strThrowable);
		model.put("url", request.getRequestURL().toString());
		try {
			out(response, processFtl(model));
		} catch (Exception e) {
			out(response, strThrowable);
		}
	}

	private String processFtl(Map<String, Object> model) throws Exception {
		Configuration configuration = new Configuration();
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setClassForTemplateLoading(this.getClass(), "/html");
		Template temp = configuration.getTemplate("config.ftl");
		StringWriter out = new StringWriter();
		temp.process(model, out);
		out.flush();
		return out.toString();
	}

	private void out(HttpServletResponse response, String content) {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(content);
		} catch (Exception e) {
			logger.error("failure to send response data.caused by:" + e.getLocalizedMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static class ConfigItem {

		private String name;

		private String value;

		private String desc;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getDesc() {
			return this.desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}

	}

}
