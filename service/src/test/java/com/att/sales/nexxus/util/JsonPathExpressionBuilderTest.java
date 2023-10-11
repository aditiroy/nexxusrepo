package com.att.sales.nexxus.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.FmoConstants;

/**
 * The Class JsonPathExpressionBuilderTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class JsonPathExpressionBuilderTest {
	
	

	@Spy
	@InjectMocks
	private JsonPathExpressionBuilder jsonPathExpressionBuilder;
	
	
	@Test
	public void objectnodeTest1() {
		jsonPathExpressionBuilder.objectnode(1l);
	}
	
	@Test
	public void objectnodeTest2() {
		jsonPathExpressionBuilder.objectnode();
	}
	
	@Test
	public void keyTest() {
		jsonPathExpressionBuilder.key("test");
	}
	
	@Test
	public void arraynodeTest1() {
		new JsonPathExpressionBuilder().arraynode(FmoConstants.DISCOUNT_DETAILS).select().
				where(FmoConstants.BEID).is(1).objectnode(FmoConstants.ICB_DESIRED_DISCOUNT).build();
		jsonPathExpressionBuilder.arraynode(1l,true);
	}
	
	@Test
	public void arraynodeTest2() {
		jsonPathExpressionBuilder.arraynode(1l,true,3l);
	}
	
	@Test
	public void arraynodeTest3() {
		jsonPathExpressionBuilder.arraynode(1l,true);
	}
	
	@Test
	public void arraynodeTest4() {
		jsonPathExpressionBuilder.arraynode();
	}
	
	@Test
	public void selectTest() {
		jsonPathExpressionBuilder.select("www");
	}
	
	@Test
	public void whereTest() {
		jsonPathExpressionBuilder.where("ss",true);
	}
	
	@Test
	public void isTest1() {
		jsonPathExpressionBuilder.is("ss",true);
	}
	
	@Test
	public void isTest2() {
		jsonPathExpressionBuilder.is("ss");
	}
	
	@Test
	public void ltTest1() {
		jsonPathExpressionBuilder.lt("ss",true);
	}
	
	@Test
	public void ltTest2() {
		jsonPathExpressionBuilder.lt("ss");
	}
	
	@Test
	public void gtTest1() {
		jsonPathExpressionBuilder.gt("ss");
	}
	
	@Test
	public void gtTest2() {
		jsonPathExpressionBuilder.gt("ss",true);
	}
	
	@Test
	public void gteTest1() {
		jsonPathExpressionBuilder.gte("ss");
	}
	
	@Test
	public void gteTest2() {
		jsonPathExpressionBuilder.gte("ss",true);
	}
	
	@Test
	public void neTest1() {
		jsonPathExpressionBuilder.ne("ss");
	}
	
	@Test
	public void neTest2() {
		jsonPathExpressionBuilder.ne("ss",true);
	}
	
	@Test
	public void regexTest1() {
		jsonPathExpressionBuilder.regex("ss");
	}
	
	@Test
	public void regexTest2() {
		jsonPathExpressionBuilder.regex("ss",true);
	}
	
	@Test
	public void InTest1() {
		jsonPathExpressionBuilder.in("ss");
	}
	
	@Test
	public void InTest2() {
		jsonPathExpressionBuilder.in("ss",true);
	}
	
	@Test
	public void lteTest1() {
		jsonPathExpressionBuilder.lte("ss");
	}
	
	@Test
	public void lteTest2() {
		jsonPathExpressionBuilder.lte("ss",true);
	}
	
	@Test
	public void NinTest1() {
		jsonPathExpressionBuilder.nin("ss");
	}
	
	@Test
	public void NinTest2() {
		jsonPathExpressionBuilder.nin("ss",true);
	}
	
	@Test
	public void orTest() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", true);
		jsonPathExpressionBuilder.or();
	}
	
	@Test
	public void orTest2() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", false);
		jsonPathExpressionBuilder.or();
	}
	
	@Test
	public void notTest() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", true);
		jsonPathExpressionBuilder.not();
	}
	
	@Test
	public void notTest2() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", false);
		jsonPathExpressionBuilder.not();
	}
	

	@Test
	public void andTest() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", true);
		
		jsonPathExpressionBuilder.and();
	}
	
	@Test
	public void andTest2() {
		ReflectionTestUtils.setField(jsonPathExpressionBuilder, "isArrayNodeOfData", false);
		jsonPathExpressionBuilder.and();
	}
	
	@Test
	public void test() {
		jsonPathExpressionBuilder.arraynode(CommonConstants.COMPONENT).select()
		.where(CommonConstants.COMPONENT_CODE_ID).is(1L)
		.arraynode(CommonConstants.DESIGN_DETAILS).select().where(CommonConstants.UDF_ID).is(1L)
		.build();
	}
	
	@Test
	public void test1() {
		jsonPathExpressionBuilder.arraynode(CommonConstants.COMPONENT, true).select("*").build();
	}
	
	@Test
	public void test2() {
		jsonPathExpressionBuilder.arraynode(CommonConstants.COMPONENT).select().where("1").is("1").and().where("1").is("1").build();
	}
}

