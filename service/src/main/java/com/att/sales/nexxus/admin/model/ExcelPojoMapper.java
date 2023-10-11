package com.att.sales.nexxus.admin.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.service.NxDataUploadService;

/**
 * The Class ExcelPojoMapper.
 *
 * @author vt393d
 */
public class ExcelPojoMapper {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxDataUploadService.class);
	
	/**
	 * Instantiates a new excel pojo mapper.
	 */
	private ExcelPojoMapper() {
		
	}
	
	/** The dtf. */
	private  static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	/**
	 * Gets the pojos.
	 *
	 * @param <T> the generic type
	 * @param excelValueConfigs the excel value configs
	 * @param clazz the clazz
	 * @return the pojos
	 */
	public static <T> List<T> getPojos(List<ExcelValueConfig[]> excelValueConfigs, Class<T> clazz) {
		log.info("Inside getPojos method  {}","");
		List<T> list = new ArrayList<>();
		Optional.ofNullable(excelValueConfigs).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull).forEach(evc -> {
			T t = null;
			try {
				t = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				log.error("Exception in creating POJO Obj", e1);
			}
			Class<? extends Object> classz = t.getClass();
			for (int i = 0; i < evc.length; i++) {
				for (Field field : classz.getDeclaredFields()) {
					String entityColumnName = null!=field.getAnnotation(Column.class)?field.getAnnotation(Column.class).name():null;
					String tableColumnName=evc[i].getTableColumnName();
					if(StringUtils.isNotEmpty(entityColumnName) && StringUtils.isNotEmpty(tableColumnName)
							&& entityColumnName.equalsIgnoreCase(tableColumnName)) {
						try {
							setFieldValue(t,field,evc[i].getExcelValue());
						} catch (IllegalArgumentException | IllegalAccessException e) {
							log.error("Exception in creating POJO", e);
						}
						break;
					}
				}
			}
			list.add(t);
		});
		return list;
	}

	
	
	/**
	 * Sets the field value.
	 *
	 * @param <T> the generic type
	 * @param t the t
	 * @param field the field
	 * @param value the value
	 * @throws IllegalAccessException the illegal access exception
	 */
	protected static <T>  void setFieldValue(T t, Field field, String value) throws IllegalAccessException {
		if(StringUtils.isNotEmpty(value)) {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
		    if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
		    	field.set(t, Boolean.valueOf(value));
		    }
		    else if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
		        field.set(t, Byte.valueOf(value));
		    }
		    else if (char.class.equals(fieldType) || Character.class.equals(fieldType)) {
		        field.set(t, value.charAt(0));
		    }
		    else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
		        field.set(t, Short.valueOf(value));
		    }
		    else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
		        field.set(t, Integer.valueOf(value));
		    }
		    else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
		        field.set(t, Long.valueOf(value));
		    }
		    else if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
		        field.set(t, Float.valueOf(value));
		    }
		    else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
		        field.set(t, Double.valueOf(value));
		    }
		    else if (Date.class.equals(fieldType) || Timestamp.class.equals(fieldType)) {
		        field.set(t, LocalDate.parse(value, dtf));
		    }
		    else if (String.class.equals(fieldType)) {
		        field.set(t, String.valueOf(value));
		    }
		    field.setAccessible(false);
		}
		
	}
	 
	
	
	


}
