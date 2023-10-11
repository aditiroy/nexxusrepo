package com.att.sales.nexxus.admin.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.dao.model.LookupDataMapping;

/**
 * The Class ExcelReader.
 */
/**
 * @author vt393d
 *
 */
@Component
public class ExcelReader {
	
	
	/**
	 * Gets the input excel row values new.
	 *
	 * @param sheet the sheet
	 * @param mappinglookupData the mappinglookup data
	 * @param inputmap the inputmap
	 * @return the input excel row values new
	 */
	public  Map<String, List<ExcelValueConfig[]>> getInputExcelRowValues(Sheet sheet,
			Map<String,List<LookupDataMapping>> mappinglookupData,Map<String,Object> inputmap) {
		Map<String, List<ExcelValueConfig[]>> excelRowValuesMap=new HashMap<>();
		List<ExcelValueConfig[]> excelValueConfigList=new ArrayList<>();
		for (Row r : sheet) {
			if(skipHeader(r,DataUploadConstants.START_INDEX)) {
				Optional.ofNullable(mappinglookupData).orElseGet(Collections::emptyMap).entrySet().stream()
				.filter(lookupdata -> CollectionUtils.isNotEmpty(lookupdata.getValue())).forEach(entry -> {
					inputmap.put(DataUploadConstants.FLOW_TYPE, entry.getKey());
					List<LookupDataMapping> lookupdataLst=entry.getValue();
					ExcelValueConfig[] excelValueConfigArr = new ExcelValueConfig[lookupdataLst.size()];
					int k = 0;
					for (LookupDataMapping data : lookupdataLst) {
						ExcelValueConfig config=this.createExcelConfig(r,data,inputmap);
						excelValueConfigArr[k++] =config;
					}
					excelValueConfigList.add(excelValueConfigArr);
				});
			}
		}
		excelRowValuesMap.put(DataUploadConstants.FILE_DATA, excelValueConfigList);
		return excelRowValuesMap;
	}
	
	/**
	 * Skip header.
	 *
	 * @param r the r
	 * @param startIndex the start index
	 * @return the boolean
	 */
	protected static Boolean skipHeader(Row r,int startIndex) {
		return r.getRowNum()>=startIndex;
	}
	
	/**
	 * Creates the excel config.
	 *
	 * @param r the r
	 * @param data the data
	 * @param inputmap the inputmap
	 * @return the excel value config
	 */
	protected ExcelValueConfig createExcelConfig(Row r,LookupDataMapping data,Map<String,Object> inputmap) {
		ExcelValueConfig config=new ExcelValueConfig();
		config.setTableColumnName(data.getTableColName());
		config.setExcelValue(data.getDefaultValue());
		
		DataFormatter dataFormatter = new DataFormatter();
		if(StringUtils.isNotEmpty(data.getInputCell())) {
			String cellAddress=data.getInputCell().concat(String.valueOf(r.getRowNum()));
			CellReference cellReference = new CellReference(cellAddress);
			Cell cell = r.getCell(cellReference.getCol());
			
			if(null!=cell && cell.getCellType() != CellType.BLANK && 
					StringUtils.isNotBlank(cell.toString())) {
				config.setExcelValue(dataFormatter.formatCellValue(cell));
				config.setExcelIndex(cell.getColumnIndex());
				config.setExcelCellAddress(cellAddress);
			}
		}else if(StringUtils.isNotEmpty(data.getFieldName()) && data.getFieldName().
				equalsIgnoreCase(DataUploadConstants.FLOW_TYPE)){
			config.setExcelValue(String.valueOf(inputmap.get(DataUploadConstants.FLOW_TYPE)));
		}else if(StringUtils.isNotEmpty(data.getFieldName()) && data.getFieldName().
				equalsIgnoreCase(DataUploadConstants.TOP_PROD_ID)){
			config.setExcelValue(String.valueOf(inputmap.get(DataUploadConstants.TOP_PROD_ID)));
		}else if(StringUtils.isNotEmpty(data.getFieldName()) && data.getFieldName().
				equalsIgnoreCase(DataUploadConstants.LITTLE_PROD_ID)){
			config.setExcelValue(String.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID)));
		}
		return config;
	}
	
	
	/**
	 * Gets the cell value.
	 *
	 * @param cell the cell
	 * @return the cell value
	 */
	public String getCellValue(Cell cell) {
	    String result;
	    switch (cell.getCellType()) {
	        case BLANK:
	            result = cell.getStringCellValue();
	            break;
	        case BOOLEAN:
	            result = String.valueOf(cell.getBooleanCellValue());
	            break;
	        case ERROR:
	            result = String.valueOf(cell.getErrorCellValue());
	            break;
	        case FORMULA:
	            result = cell.getCellFormula();
	            break;
	        case NUMERIC:
	        	 result = String.valueOf(new DecimalFormat("0").format(cell.getNumericCellValue()));
	            break;
	        case STRING:
	            result = cell.getRichStringCellValue().getString();
	            break;
	        default:
	            result = cell.getStringCellValue();
	            break;
	    }
	    return result;
	}
	 
	

}
