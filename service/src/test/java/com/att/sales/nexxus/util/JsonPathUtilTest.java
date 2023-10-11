package com.att.sales.nexxus.util;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.output.entity.NxAvpnOutputBean;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.att.sales.nexxus.reteriveicb.model.Solution;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.TypeRef;

/**
 * The Class JsonPathUtilTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class JsonPathUtilTest {

	@Spy
	@InjectMocks
	private JsonPathUtil jsonPathUtil;
	
	@Test
	public void getConfigurationTest() {
		jsonPathUtil.getConfiguration();
	}
	
	@Test
	public void addTest1() {
		jsonPathUtil.add(new NxOutputBean(), "$..id", new NxOutputBean());
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		jsonPathUtil.add(solution, "$..userId", new Solution());
	}
	
	@Test
	public void addTest2() {
		jsonPathUtil.add("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean());
	}
	
	@Test
	public void addTest3() {
		jsonPathUtil.add(new NxOutputBean(), "$..id", new NxOutputBean(),true);
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		jsonPathUtil.add(solution, "$..userId", new Solution(), true);
	}
	
	@Test
	public void addTest4() {
		jsonPathUtil.add("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean(),true);
	}
	

	@Test
	public void addTest5() {
		jsonPathUtil.add("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean(),false);
	}
	
	@Test
	public void putTest1() {
		jsonPathUtil.put(new NxOutputBean(), "$..id","$..id", new NxOutputBean());
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		jsonPathUtil.put(solution, "$..userId","$..userId", new Solution());
	}
	
	@Test
	public void putTest2() {
		jsonPathUtil.put("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id","$..id", new NxOutputBean());
	}
	
	@Test
	public void putTest3() {
		jsonPathUtil.put(new NxOutputBean(), "$..id","$..id", new NxOutputBean(),true);
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		jsonPathUtil.put(solution, "$..userId","$..userId", new Solution(), true);
	}
	
	@Test
	public void putTest4() {
		jsonPathUtil.put("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id","$..id", new NxOutputBean(),true);
	}
	
	@Test
	public void putTest5() {
		jsonPathUtil.put("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id","$..id", new NxOutputBean(),false);
	}
	
	@Test
	public void setTest1() {
		jsonPathUtil.set(new NxOutputBean(), "$..id", new NxOutputBean());
		
		jsonPathUtil.set(new NxOutputBean(), "..id", new NxOutputBean());
	}
	
	@Test
	public void setTest2() {
		jsonPathUtil.set("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean());
	}
	
	@Test
	public void setTest3() {
		jsonPathUtil.set(new NxOutputBean(), "$..id", new NxOutputBean(),true);
		jsonPathUtil.set(new NxOutputBean(), "..id", new NxOutputBean(),true);
	}
	
	@Test
	public void setTest4() {
		jsonPathUtil.set("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean(),true);
	}
	
	@Test
	public void setTest5() {
		jsonPathUtil.set("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxOutputBean(),false);
	}
	
	
	@Test
	public void searchTest1() {
		jsonPathUtil.search(new NxOutputBean(), "$..id");
		jsonPathUtil.search(new NxOutputBean(), "..id");
	}
	
	@Test
	public void searchTest2() {
		jsonPathUtil.search("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id");
	}
	
	@Test
	public void searchTest3() {
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		jsonPathUtil.search(new NxOutputBean(), "$..id",mapType);
		jsonPathUtil.search(new NxOutputBean(), "..id",mapType);
	}
	
	@Test
	public void searchTest4() {
		TypeRef<List<Object>> mapType = new TypeRef<List<Object>>() {};
		jsonPathUtil.search("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id",mapType);
	}
	
	@Test
	public void deleteTest1() {
		jsonPathUtil.delete(new NxOutputBean(), "$..id");
		jsonPathUtil.delete(new NxOutputBean(), "..id");
	}
	
	@Test
	public void deleteTest2() {
		jsonPathUtil.delete("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id");
	}
	
	@Test
	public void deleteTest3() {
		jsonPathUtil.delete(new NxOutputBean(), "$..id",true);
		jsonPathUtil.delete(new NxOutputBean(), "..id",true);
	}
	
	@Test
	public void deleteTest4() {
		jsonPathUtil.delete("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id",false);
	}
	@Test
	public void deleteTest5() {
		jsonPathUtil.delete("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id",true);
	}
	
	@Test
	public void setTest7() {
		TypeReference<List<NxAvpnOutputBean>> mapType = new TypeReference<List<NxAvpnOutputBean>>() {};
		jsonPathUtil.set(new NxOutputBean(), "$..id", new NxAvpnOutputBean(), mapType);
		jsonPathUtil.set(new NxOutputBean(), "..id", new NxAvpnOutputBean(), mapType);
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		TypeReference<Solution> mapType1 = new TypeReference<Solution>() {};
		jsonPathUtil.set(solution, "$..userId", "newData", mapType1);
	}
	@Test
	public void setTest8() {
		TypeReference<List<NxAvpnOutputBean>> mapType = new TypeReference<List<NxAvpnOutputBean>>() {};
		jsonPathUtil.set("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", new NxAvpnOutputBean(), mapType);
	}
	
	@Test
	public void deleteTest7() {
		TypeReference<List<NxAvpnOutputBean>> mapType = new TypeReference<List<NxAvpnOutputBean>>() {};
		jsonPathUtil.delete(new NxOutputBean(), "$..city", mapType);
		jsonPathUtil.delete(new NxOutputBean(), "..city", mapType);
		
		Solution solution = new Solution();
		solution.setUserId("userId");
		TypeReference<Solution> mapType1 = new TypeReference<Solution>() {};
		jsonPathUtil.delete(solution, "$..userId", mapType1);
	}
	@Test
	public void deleteTest8() {
		TypeReference<List<NxAvpnOutputBean>> mapType = new TypeReference<List<NxAvpnOutputBean>>() {};
		jsonPathUtil.delete("\"{\\r\\n\" + \r\n" + 
				"				\"\\\"requestIds\\\":[\\\"1\\\",\\\"21\\\"]\\r\\n\" + \r\n" + 
				"				\"\\r\\n\" + \r\n" + 
				"				\"}\"", "$..id", mapType);
	}
}
