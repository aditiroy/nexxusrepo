package com.att.slesmarketing.soma.pric.nexxus.unittest.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import com.att.sales.nexxus.controller.CpcTransformDaoImpl;


@ExtendWith(MockitoExtension.class)
public class CpcTransformDaoImplTest {
	
	@InjectMocks
	CpcTransformDaoImpl cpcTransformDaoImpl;
	
	@Mock
	private JdbcTemplate oracleJdbcTemplate;
	
	@Test
	public void testDBConnection() {
		cpcTransformDaoImpl.testDBConnection();
	}

	@Test
	public void testCheckTable() {
		final Map<String, Integer> header = new HashMap<String, Integer>();
		final String fileNameAstableName = "PRICE_CATALOGUE";
		header.put("test", 1);
		Mockito.when(oracleJdbcTemplate.queryForObject(anyString(), eq(Long.class), anyString())).thenReturn(0L);
		cpcTransformDaoImpl.checkTable(fileNameAstableName, header); 
	}
	
	@Test
	public void testCreateCSVTable() {
		final Map<String, Integer> header = new HashMap<String, Integer>();
		final String fileNameAstableName = "PRICE_CATALOGUE";
		header.put("test", 1);
		doNothing().when(oracleJdbcTemplate).execute(anyString());
		cpcTransformDaoImpl.createCSVTable(fileNameAstableName, header);
	}
}
