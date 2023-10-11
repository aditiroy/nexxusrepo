package com.att.sales.nexxus.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import com.att.sales.framework.model.ServiceMetaData;
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
		List<PojoClass> pojoClasses = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.model");
		
		List<PojoClass> pojoClasses1 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.fallout.model");
		
		List<PojoClass> pojoClasses2 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.model.previewinr");
		
		List<PojoClass> pojoClasses3 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.myprice.publishValidatedAddresses.model");
		
		List<PojoClass> pojoClasses4 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.rateletter.model");

		List<PojoClass> pojoClasses5 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.serviceValidation.model");
		
		List<PojoClass> pojoClasses6 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.myprice.transaction.model");
		
		List<PojoClass> pojoClasses8 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.custompricing.model");
		
		List<PojoClass> pojoClasses9 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.nxPEDstatus.model");
		
		List<PojoClass> pojoClasses10 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.ped.dmaap.model");
		
		List<PojoClass> pojoClasses11 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.pricing.model");
		
		List<PojoClass> pojoClasses12 = PojoClassFactory.getPojoClasses("com.att.sales.nexxus.transmitdesigndata.model");
		
		Validator pojoValidator = ValidatorBuilder.create()
				.with(new SetterTester()).with(new GetterTester())
				.build();
		pojoClasses.removeIf(p -> ( p.getClazz().toString().contains("com.att.sales.nexxus.model.UploadASENexxusFileRequest") ||  p.getClazz().toString().contains("com.att.sales.nexxus.model.ProductDataLoadRequest")));
		
		pojoValidator.validate(pojoClasses);
		pojoValidator.validate(pojoClasses1);
		pojoValidator.validate(pojoClasses2);
		pojoValidator.validate(pojoClasses3);
		pojoValidator.validate(pojoClasses4);
		pojoValidator.validate(pojoClasses5);
		pojoValidator.validate(pojoClasses6);
		pojoValidator.validate(pojoClasses8);
		pojoValidator.validate(pojoClasses9);
		pojoValidator.validate(pojoClasses10);
		pojoValidator.validate(pojoClasses11);
		pojoValidator.validate(pojoClasses12);
		
		/*UploadASENexxusFileRequest uploadAseTest = new UploadASENexxusFileRequest(null);
		uploadAseTest.setMultipartBody(null);
		uploadAseTest.getMultipartBody();
		
		ProductDataLoadRequest  ProductDataLoadTest = new ProductDataLoadRequest(null);
		ProductDataLoadTest.setMultipartBody(null);
		ProductDataLoadTest.getMultipartBody();	*/	
	}
	
	@Test
	public void testConstructorsAndToString() {
	/*	ProductDataLoadRequest pddmTest = new ProductDataLoadRequest(null);
		pddmTest.toString();
		
		UploadASENexxusFileRequest uploadAseTest = new UploadASENexxusFileRequest(null);
		uploadAseTest.toString();
		*/
		NexxusOutputRequest outputTest = new NexxusOutputRequest();
		outputTest.setRequestIds(null);
	}
}
