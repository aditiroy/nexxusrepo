package com.att.sales.nexxus.dao.model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.edf.model.ValidateAccountDataRequestDetails;
import com.att.sales.nexxus.edf.model.ValidateAccountDataResponse;
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

		List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.dao");
		List<PojoClass> pojoClassesOne = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.dao.model");
		List<PojoClass> pojoClassesEdf = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.edf.model");
		List<PojoClass> pojoClassSolution = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.dao.model.solution");
		List<PojoClass> pojoClassEntity = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.output.entity");
		List<PojoClass> pojoClassRome = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.rome.model");
		List<PojoClass> pojoClassTemplate = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.template.model");
		//List<PojoClass> pojoClassesTwo = PojoClassFactory.getPojoClasses("com.att.sales.exampledomainobject.model");
		
		// assertEquals(125, pojoClasses.size());

		Validator pojoValidator = ValidatorBuilder.create()
				// .with(new GetterMustExistRule())
				// .with(new SetterMustExistRule())
				.with(new SetterTester()).with(new GetterTester())

				// .with(new BusinessKeyMustExistRule())
				// .with(new BusinessIdentityTester())
				.build();

		pojoValidator.validate(pojoClasses);
		pojoValidator.validate(pojoClassesOne);
		pojoValidator.validate(pojoClassesEdf);
		pojoValidator.validate(pojoClassSolution);
	//	pojoValidator.validate(pojoClassEntity);
		pojoValidator.validate(pojoClassRome);
		pojoValidator.validate(pojoClassTemplate);
		//pojoValidator.validate(pojoClassesTwo);
	}
	
	@Test
	public void testConstructorsAndToString() {
		ValidateAccountDataResponse validateAccountDataResponse = new ValidateAccountDataResponse();
		validateAccountDataResponse.toString();
		ValidateAccountDataRequestDetails validateAccountDataRequestDetails = new ValidateAccountDataRequestDetails();
		validateAccountDataRequestDetails.toString();
	}

}
