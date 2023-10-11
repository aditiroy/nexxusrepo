package com.att.sales.nexxus.controllerTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVRecord;
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
	private CpcTransformDaoImpl cpcTransformDaoImpl;

	@Mock
	private JdbcTemplate oracleJdbcTemplate;

	@Test
	public void testDBConnection() {
		String string="\"SELECT COUNT(*) FROM TAB \"";
		Long longValue=12345L;
		Mockito.when(oracleJdbcTemplate.queryForObject(string, Long.class)).thenReturn( longValue);
cpcTransformDaoImpl.testDBConnection();
	}
	
	@Test
	public void testCheckTable()
	{
	String  fileNameAstableName="fileNameAstableName"; 
	Long longValue=12345L;
	String sqlCheckTable = "SELECT Count(*) FROM TAB  where TNAME=UPPER(?)";
	Map<String, Integer> header=new HashMap<>();
	Mockito.when(oracleJdbcTemplate.queryForObject(sqlCheckTable, Long.class,fileNameAstableName)).thenReturn( longValue);
		cpcTransformDaoImpl.checkTable(fileNameAstableName, header);
	}
	
	@Test
	public void testCreateCSVTable()
	{String  fileNameAstableName="fileNameAstableName"; 
	Map<String, Integer> header=new HashMap<>();
	header.put("key",1233);
		cpcTransformDaoImpl.createCSVTable(fileNameAstableName, header);
	}
	
	@Test
	public void testbatchUpdate()
	{
		String sqlInsertStatement="sqlInsertStatement";
		TreeMap<Integer, String> columnHeaderMap=new TreeMap<>();
		
		List<CSVRecord> recordList = new ArrayList<>();
		
		cpcTransformDaoImpl.batchUpdate(sqlInsertStatement, columnHeaderMap, recordList);
	}
	
	
}
