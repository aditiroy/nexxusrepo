package com.att.sales.nexxus.admin.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpStagingModel;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
public class PojoTest {

	@BeforeAll
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.OFFER, "2");
		map.put(ServiceMetaData.VERSION, "v2");
		map.put(ServiceMetaData.METHOD, "post");
		map.put(ServiceMetaData.URI, "hghg");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.SERVICE_FILTER, "AVPN");
		map.put(ServiceMetaData.SERVICEID, "SERVICEID");
		ServiceMetaData.add(map);

	}
	
	@Test
	public void test_pojo_structure_and_behavior() {
		List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.admin.model");
		
		Validator pojoValidator = ValidatorBuilder.create()
				.with(new SetterTester()).with(new GetterTester())
				.build();
		pojoValidator.validate(pojoClasses);
		
	}
	
	@Test
	public void testExcelPojoMapper() {
		ExcelValueConfig config = new ExcelValueConfig();
		config.setTableColumnName("LINE_ITEM_ID");
		config.setExcelValue("INR");
		ExcelValueConfig[] excelValueConfigArr = new ExcelValueConfig[1];
		excelValueConfigArr[0] = config;
  		List<ExcelValueConfig[]> excelValueConfigs = new ArrayList<>();
		excelValueConfigs.add(excelValueConfigArr);
		ExcelPojoMapper.getPojos(excelValueConfigs, NxLineItemLookUpStagingModel.class);
	}
	
	@Test
	public void testSetFieldValue() throws IllegalAccessException {
		String value = null;
		NxLineItemLookUpStagingModel t = new NxLineItemLookUpStagingModel();
		for (Field field : NxLineItemLookUpStagingModel.class.getDeclaredFields()) {
			field.setAccessible(true);
			Class<?> fieldType = field.getType();
			field.setAccessible(false);
		    if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
		    	value = "true";
		    }
		    else if (byte.class.equals(fieldType) || Byte.class.equals(fieldType)) {
		    	value = "20";
		    }
		    else if (char.class.equals(fieldType) || Character.class.equals(fieldType)) {
		    	value = "C";
		    }
		    else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
		    	value = "1";
		    }
		    else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
		    	value = "1";
		    }
		    else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
		    	value = String.valueOf(1111L);
		    }
		    else if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
		    	value = String.valueOf(1.1);
		    }
		    else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
		    	value = String.valueOf(1111.1d);
		    }
		    else if (String.class.equals(fieldType)) {
		    	value = "test";
		    }
		    ExcelPojoMapper.setFieldValue(t, field, value);
		}
	}
	
	@Test
	public void testDataUploadRequest() {
		DataUploadRequest dataUploadRequest = new DataUploadRequest();
		dataUploadRequest.setUserId("test");
		dataUploadRequest.getUserId();
		dataUploadRequest.getAction();
		dataUploadRequest.getActivity();
		dataUploadRequest.getInputStream();
		dataUploadRequest.getId();
	}

}
