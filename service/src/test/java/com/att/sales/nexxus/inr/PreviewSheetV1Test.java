package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class PreviewSheetV1Test {
	@Spy
	@InjectMocks
	private PreviewSheetV1 previewSheetV1;
	@Mock
	private Sheet sheet;
	@Mock
	private Map<String, Integer> tagToColumn;
	@Mock
	private Set<Integer> neededColumns;
	@Mock
	private Map<Integer, CellStyle> sequenceToCellStyle;
	@Mock
	private Map<String, List<Integer>> tagToLocation;
	@Mock
	private Map<Integer, String> columnToFormula;
	@Mock
	private Row row;
	@Mock
	private Cell cell;
	@Mock
	private CellStyle cellStyle;
	
	private ObjectMapper realMapper = new ObjectMapper();

	@Test
	public void previewSheetV1Test() {
		Sheet st = null;
		PreviewSheetV1 ps = new PreviewSheetV1(st);
		assertNull(ps.getSheet());
	}

	@Test
	public void initTest() {
		when(sheet.getRow(anyInt())).thenReturn(null, row);
		when(row.getCell(anyInt())).thenReturn(cell);
		when(previewSheetV1.getCellValueAsString(any())).thenReturn("definition starts", "1,1", "!formula", "needed",
				"needed", "rootTag", "tag", "tag", "1", "1", "definition ends");
		when(row.getLastCellNum()).thenReturn((short) 2);
		doNothing().when(previewSheetV1).removeRow(anyInt());
		previewSheetV1.init();
	}
	
	@Test
	public void removeRowTest() {
		when(sheet.getLastRowNum()).thenReturn(2);
		doNothing().when(sheet).shiftRows(anyInt(), anyInt(), anyInt());
		when(sheet.getRow(anyInt())).thenReturn(row);
		doNothing().when(sheet).removeRow(any());
		previewSheetV1.removeRow(1);
		previewSheetV1.removeRow(2);
	}
	
	@Test
	public void toStringTest() {
		previewSheetV1.toString();
	}
	
	@Test
	public void processNodeTest() {
		ReflectionTestUtils.setField(previewSheetV1, "rootTag", "rootTag");
		ObjectNode node = realMapper.createObjectNode();
		node.put("rootTag", "rootTag");
		node.put("sequence", 1);
		node.put("tag", "tagValue");
		Map<String, Integer> tagToColumnSample = new HashMap<>();
		tagToColumnSample.put("sequence", 1);
		tagToColumnSample.put("tag", 2);
		ReflectionTestUtils.setField(previewSheetV1, "tagToColumn", tagToColumnSample);
		Set<Integer> neededColumnsSample = new HashSet<>();
		ReflectionTestUtils.setField(previewSheetV1, "neededColumns", neededColumnsSample);
		when(sheet.createRow(anyInt())).thenReturn(row);
		doNothing().when(previewSheetV1).writeCell(any(), anyInt(), anyInt(), any());
		ReflectionTestUtils.setField(previewSheetV1, "lastCellNum", 2);
		Map<Integer, String> columnToFormulaSample = new LinkedHashMap<>();
		ReflectionTestUtils.setField(previewSheetV1, "columnToFormula", columnToFormulaSample);
		doNothing().when(previewSheetV1).writeFormula(anyInt(), any(), anyInt(), any(), anyInt());
		
		previewSheetV1.processNode(node);
	}
	
	@Test
	public void writeFormulaTest() {
		when(row.createCell(anyInt())).thenReturn(cell);
		Map<Integer, CellStyle> sequenceToCellStyleSample = new HashMap<>();
		sequenceToCellStyleSample.put(0, cellStyle);
		sequenceToCellStyleSample.put(1, cellStyle);
		ReflectionTestUtils.setField(previewSheetV1, "sequenceToCellStyle", sequenceToCellStyleSample);
		
		previewSheetV1.writeFormula(1, "formula", 1, row, 1);
	}
	
	@Test
	public void writeCellTest() {
		when(row.createCell(anyInt())).thenReturn(cell);
		Map<Integer, CellStyle> sequenceToCellStyleSample = new HashMap<>();
		sequenceToCellStyleSample.put(0, cellStyle);
		sequenceToCellStyleSample.put(1, cellStyle);
		ReflectionTestUtils.setField(previewSheetV1, "sequenceToCellStyle", sequenceToCellStyleSample);
		PreviewCell previewCell = new PreviewCell("String");
		
		previewSheetV1.writeCell(row, 1, 1, previewCell);
		
		previewCell = new PreviewCell(1.1);
		previewSheetV1.writeCell(row, 1, 1, previewCell);
	}
	
	@Test
	public void setGetTest() {
		previewSheetV1.setSheet(sheet);
		assertSame(sheet, previewSheetV1.getSheet());
		
		String rootTag = "tag";
		ReflectionTestUtils.setField(previewSheetV1, "rootTag", rootTag);
		assertSame(rootTag, previewSheetV1.getRootTag());
	}
	
	@Test
	public void populateHeaderTest() {
		Map<String, List<Integer>> tagToLocationSample = new HashMap<>();
		tagToLocationSample.put("tag", Arrays.asList(1, 2));
		ReflectionTestUtils.setField(previewSheetV1, "tagToLocation", tagToLocationSample);
		ObjectNode node = realMapper.createObjectNode();
		node.put("tag", "tagVlaue");
		when(sheet.getRow(anyInt())).thenReturn(row);
		when(row.getCell(anyInt())).thenReturn(cell);
		
		previewSheetV1.populateHeader(node);
	}
}
