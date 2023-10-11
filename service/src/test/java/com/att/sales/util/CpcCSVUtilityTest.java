package com.att.sales.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.controller.CpcTransformDaoImpl;


@ExtendWith(MockitoExtension.class)
public class CpcCSVUtilityTest {

	@InjectMocks
	CpcCSVUtility cpcCSVUtility;
	
	@Mock
	private CpcTransformDaoImpl cpcTransformDaoImpl;
	
	@Test
	public void testReadCSVHeader() throws IOException {
		String fileNameWithPath="etc/process/";
	//	cpcCSVUtility.readCSVHeader(fileNameWithPath);
	}
	
	@Test
	public void testPrepareInsertStatement()
	{
		String  tableName="fileNameAstableName"; 
		String transactionId="transactionId";
		Map<String, Integer> header=new HashMap<>();
		header.put("key",1233);
		cpcCSVUtility.prepareInsertStatement(tableName, header,transactionId);
	}
	
	@Test
	public void testTrimFileName()
	{
		String  onlyFileName="onlyFileName";
		cpcCSVUtility.trimFileName(onlyFileName);
	}

	
	@Test
	public void testTrimFileNameIf()
	{
		String  onlyFileName="onlyFileNameonlyFileNameonlyFileNameonlyFileNameonlyFileName";
		cpcCSVUtility.trimFileName(onlyFileName);
	}
}
