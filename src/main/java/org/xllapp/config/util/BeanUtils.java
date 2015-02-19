package org.xllapp.config.util;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.util.ReflectionUtils;

/**
 * Bean操作工具类.
 *
 * @author dylan.chen Sep 1, 2014
 * 
 */
public abstract class BeanUtils {

	public static List<Field> getFields(Class<?> clazz, final String... exclusions) {
		final List<Field> fields = new ArrayList<Field>();
		ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if (!ArrayUtils.contains(exclusions, field.getName())) {
					fields.add(field);
				}
			}
		});
		return fields;
	}

	public static void setPropertyValue(String propertyName, Object target, Object newValue) throws Exception {
		DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(target);
		fieldAccessor.setPropertyValue(propertyName, newValue);
	}

	public static String getPropertyValueAsString(String propertyName, Object target) throws IllegalArgumentException, IllegalAccessException {
		DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(target);
		Object value = fieldAccessor.getPropertyValue(propertyName);
		return null != value ? toString(value) : null;
	}

	private static String toString(Object input) {
		if (input instanceof String) {
			return (String) input;
		}
		PropertyEditorRegistrySupport editorRegistry = new MyPropertyEditorRegistry();
		PropertyEditor propertyEditor = editorRegistry.getDefaultEditor(input.getClass());
		propertyEditor.setValue(input);
		return propertyEditor.getAsText();
	}

	@SuppressWarnings("unchecked")
	public static <T> T copyObject(T source, String... exclusions) throws InstantiationException, IllegalAccessException {
		T copy = (T) source.getClass().newInstance();
		copyObject(source, copy, exclusions);
		return copy;
	}

	public static <T> T copyObject(T source, T copy, String... exclusions) throws InstantiationException, IllegalAccessException {
		DirectFieldAccessor sourceFieldAccessor = new DirectFieldAccessor(source);
		DirectFieldAccessor copyFieldAccessor = new DirectFieldAccessor(copy);
		List<Field> fields = getFields(source.getClass(),exclusions);
		for (Field field : fields) {
			String propertyName = field.getName();
			copyFieldAccessor.setPropertyValue(propertyName, sourceFieldAccessor.getPropertyValue(propertyName));
		}
		return copy;
	}

	private static class MyPropertyEditorRegistry extends PropertyEditorRegistrySupport {
		{
			registerDefaultEditors();
		}
	}
}
