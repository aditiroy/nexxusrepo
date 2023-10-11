package com.att.sales.nexxus.inr;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
@ExtendWith(MockitoExtension.class)
public class PreviewWorkbookV1Test {
	@Spy
	private PreviewWorkbookV1 previewWorkbookV1;
	@Mock
	private UnmockableWrapper unmockableWrapper;
	@Mock
	private FileInputStream fis;
	@Mock
	private Workbook wb;
	@Mock
	private Iterator<Sheet> iterator;
	@Mock
	private Sheet sheet;
	@Mock
	private PreviewSheetV1 previewSheet;
	@Mock
	private FileOutputStream fileOut;
	
	private ObjectMapper realMapper = new ObjectMapper();

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(previewWorkbookV1, "templatePath", "src/main/resources/nexxusTemplate/address_edit_template.xlsx");
		ReflectionTestUtils.setField(previewWorkbookV1, "p8dLocalPath", "");
		ReflectionTestUtils.setField(previewWorkbookV1, "unmockableWrapper", unmockableWrapper);
		ReflectionTestUtils.setField(previewWorkbookV1, "hash", 1);
	}
	
	@Test
	public void initTest() throws IOException {
		when(unmockableWrapper.newFileInputStream(any())).thenReturn(fis);
		doReturn(wb).when(previewWorkbookV1).newXSSFWorkbook(any());
		when(wb.sheetIterator()).thenReturn(iterator);
		when(iterator.hasNext()).thenReturn(true, false);
		when(iterator.next()).thenReturn(sheet);
		doReturn(previewSheet).when(previewWorkbookV1).newPreviewSheetV1(any());
		doNothing().when(previewSheet).init();
		ArrayNode cdirDataArray = realMapper.createArrayNode();
		ObjectNode node = realMapper.createObjectNode();
		node.withArray("rootTag").add("tag1");
		cdirDataArray.add(node);
		ObjectNode headerElement = realMapper.createObjectNode();
		headerElement.put("key", "value");
		node.withArray("header").add(headerElement);
		when(wb.getSheetAt(anyInt())).thenReturn(sheet);
		doNothing().when(previewWorkbookV1).useSXSSF();
		previewWorkbookV1.init(cdirDataArray);
	}
	
	@Test
	public void newPreviewSheetV1Test()  {
		previewWorkbookV1.newPreviewSheetV1(sheet);
	}
	
	@Test
	public void processNodeTest() {
		List<PreviewSheetV1> sheets = new LinkedList<>();
		sheets.add(previewSheet);
		ReflectionTestUtils.setField(previewWorkbookV1, "sheets", sheets);
		doNothing().when(previewSheet).processNode(any());
		previewWorkbookV1.processNode(null);
	}
	
	@Test
	public void writeTest() throws IOException {
		ReflectionTestUtils.setField(previewWorkbookV1, "wb", wb);
		when(unmockableWrapper.newFileOutputStream(any())).thenReturn(fileOut);
		previewWorkbookV1.write();
	}
	
	@Test
	public void useSXSSFTest() {
		doReturn(wb).when(previewWorkbookV1).newSXSSFWorkbook(any(), anyInt());
		List<PreviewSheetV1> sheets = new LinkedList<>();
		sheets.add(previewSheet);
		ReflectionTestUtils.setField(previewWorkbookV1, "sheets", sheets);
		when(previewSheet.getSheet()).thenReturn(sheet);
		when(sheet.getSheetName()).thenReturn("sheetName");
		previewWorkbookV1.useSXSSF();
	}
}
