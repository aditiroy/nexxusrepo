package com.att.sales.nexxus.admin.model;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.LookupDataMapping;
@ExtendWith(MockitoExtension.class)
public class ExcelReaderTest {
	
	Map<String,Object> inputmap = new HashMap<String,Object>();
	@InjectMocks
	ExcelReader excelReader;
	
	@Test
	public void testGetCellValue() {
		Workbook workbook = new HSSFWorkbook();
		Sheet mockSheet = workbook.createSheet();
		Row row = mockSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(true);
		assertEquals("true", excelReader.getCellValue(cell));
		cell.setCellValue("test");
		assertEquals("test", excelReader.getCellValue(cell));
		cell.setCellValue(123d);
		assertEquals("123", excelReader.getCellValue(cell));
		cell.setCellFormula("1/2");
		assertEquals("1/2", excelReader.getCellValue(cell));
	}
	
	@Test
	public void testCreateExcelConfig() {
		LookupDataMapping data = new LookupDataMapping();
		data.setTableColName("LINE_ITEM_ID");
		data.setDefaultValue("N");
		data.setInputCell("B");
		excelReader.createExcelConfig(mock(Row.class), data, inputmap);
	}
	
	@Test
	public void testCreateExcelConfigFlowType() {
		LookupDataMapping data = new LookupDataMapping();
		data.setTableColName("LINE_ITEM_ID");
		data.setDefaultValue("N");
		data.setFieldName("flowType");
		excelReader.createExcelConfig(mock(Row.class), data, inputmap);
	}
	
	@Test
	public void testCreateExcelConfigTopProdId() {
		LookupDataMapping data = new LookupDataMapping();
		data.setTableColName("LINE_ITEM_ID");
		data.setDefaultValue("N");
		data.setFieldName("topProdId");
		excelReader.createExcelConfig(mock(Row.class), data, inputmap);
	}
	
	@Test
	public void testCreateExcelConfigLittleProdId() {
		LookupDataMapping data = new LookupDataMapping();
		data.setTableColName("LINE_ITEM_ID");
		data.setDefaultValue("N");
		data.setFieldName("littleProdId");
		excelReader.createExcelConfig(mock(Row.class), data, inputmap);
	}
	
	@Test
	public void testGetInputExcelRowValues() {
		Map<String,List<LookupDataMapping>> mappinglookupData = new HashMap<String,List<LookupDataMapping>>();
		List<LookupDataMapping> lookupDataMappings = new ArrayList<LookupDataMapping>();
		LookupDataMapping data = new LookupDataMapping();
		data.setTableColName("LINE_ITEM_ID");
		data.setDefaultValue("N");
		data.setFieldName("littleProdId");
		data.setFlowType("INR");
		lookupDataMappings.add(data);
		mappinglookupData.put("INR", lookupDataMappings);
		Workbook workbook = new HSSFWorkbook();
		Sheet mockSheet = workbook.createSheet();
		mockSheet.createRow(0);
		mockSheet.createRow(1);
		excelReader.getInputExcelRowValues(mockSheet, mappinglookupData, inputmap);
	}

}
