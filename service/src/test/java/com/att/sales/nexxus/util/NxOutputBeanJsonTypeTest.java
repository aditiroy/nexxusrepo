package com.att.sales.nexxus.util;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.repository.NxOutputBeanJsonType;
import com.att.sales.nexxus.output.entity.NxOutputBean;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * The Class NxOutputBeanJsonTypeTest.
 */
/**
 * @author vt393d
 *
 */
@ExtendWith(MockitoExtension.class)
public class NxOutputBeanJsonTypeTest {

	@Spy
	@InjectMocks
	private NxOutputBeanJsonType  nxOutputBeanJsonType;
	
	
	@Test
	public void convertToDatabaseColumnTest1() {
		nxOutputBeanJsonType.convertToDatabaseColumn(new NxOutputBean());
	}
	
	
	@Test
	public void convertToDatabaseColumnTest2() {
		nxOutputBeanJsonType.convertToDatabaseColumn(null);
	}
	
	
	@SuppressWarnings("serial")
	@Test
	public void convertToDatabaseColumnExceptionTest() throws JsonProcessingException {
		doThrow(new JsonProcessingException("Error") {}).when(nxOutputBeanJsonType).serialize(any());
		nxOutputBeanJsonType.convertToDatabaseColumn(new NxOutputBean());
	}
	
	@Test
	public void convertToEntityAttributeTest1() {
		nxOutputBeanJsonType.convertToEntityAttribute("{\r\n" + 
				"\"requestIds\":[\"1\",\"21\"]\r\n" + 
				"\r\n" + 
				"}");
	}
	
	@Test
	public void convertToEntityAttributeTest2() {
		nxOutputBeanJsonType.convertToEntityAttribute(null);
	}
	
	@Test
	public void convertToEntityAttributeTestException() throws IOException{
		doThrow(new IOException()).when(nxOutputBeanJsonType).deserialize(any());
		nxOutputBeanJsonType.convertToEntityAttribute("{\r\n" + 
				"\"requestIds\":[\"1\",\"21\"]\r\n" + 
				"\r\n" + 
				"}");
	}
}
