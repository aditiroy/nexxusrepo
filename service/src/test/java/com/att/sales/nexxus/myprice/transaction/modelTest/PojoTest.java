package com.att.sales.nexxus.myprice.transaction.modelTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.att.sales.framework.model.ServiceMetaData;
import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

/**
 * @author IndraSingh
 */


public class PojoTest {

	@BeforeEach
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
	public void test_pojo_structure_and_behaviour() {
		List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.myprice.transaction.model");

		Validator pojoValidator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();

		pojoValidator.validate(pojoClasses);
	}

}
