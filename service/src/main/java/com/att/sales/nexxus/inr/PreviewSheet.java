package com.att.sales.nexxus.inr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PreviewSheet.
 */
public class PreviewSheet {
//	
//	/** The log. */
//	private static Logger log = LoggerFactory.getLogger(PreviewSheet.class);
//	
//	/** The Constant DEFINITION_STARTS. */
//	private static final String DEFINITION_STARTS = "definition starts";
//	
//	private static final String DEFINITION_ENDS = "definition ends";
//	
//	/** The Constant SEQUENCE. */
//	private static final JsonPath SEQUENCE = new JsonPath("sequence");
//	
//	/** The sheet. */
//	private Sheet sheet;
//	
//	/** The formatter. */
//	private DataFormatter formatter = new DataFormatter();
//	
//	/** The start row num. */
//	private int startRowNum;
//	
//	/** The row index. */
//	private int rowIndex;
//	
//	/** The last cell num. */
//	private int lastCellNum;
//	
//	/** The tag to column. */
//	private Map<JsonPath, Integer> tagToColumn = new HashMap<>();
//	
//	/** The tag to value. */
//	private Map<JsonPath, String> tagToValue = new HashMap<>();
//	
//	/** The parent tag to columns. */
//	private Map<JsonPath, List<Integer>> parentTagToColumns = new HashMap<>();
//	
//	/** The row tracker. */
//	private Map<JsonPath, List<PreviewRow>> rowTracker = new HashMap<>();
//	
//	/** The row data. */
//	private List<PreviewRow> rowData = new LinkedList<>();
//	
//	/** The tag for clear row list. */
//	private JsonPath tagForClearRowList;
//	
//	/** The needed columns. */
//	private Set<Integer> neededColumns = new HashSet<>();
//	
//	/** The sequence to cell style. */
//	private Map<Integer, CellStyle> sequenceToCellStyle = new HashMap<>();
//	
//	/** The fixed value. */
//	private Map<Integer, PreviewCell> fixedValue = new HashMap<>();
//	
//	private Map<JsonPath, List<Integer>> tagToLocation = new HashMap<>();
//	
//	/** The top object. */
//	private JsonPath topObject;
//
//	/**
//	 * Instantiates a new preview sheet.
//	 *
//	 * @param sheet the sheet
//	 */
//	public PreviewSheet(Sheet sheet) {
//		super();
//		this.sheet = sheet;
//	}
//
//	/**
//	 * Inits the.
//	 */
//	public void init() {
//		int rIndex = 0;
//		String cellValue = null;
//		do {
//			Row row = sheet.getRow(rIndex++);
//			if (row == null) {
//				continue;
//			}
//			Cell cell = row.getCell(0);
//			cellValue = getCellValueAsString(cell);
//		} while (!DEFINITION_STARTS.equals(cellValue));
//		startRowNum = rIndex - 1;
//		rowIndex = startRowNum;
//
//		Row row = sheet.getRow(rIndex++);
//		lastCellNum = row.getLastCellNum();
//		for (int i = 0; i < lastCellNum; i++) {
//			cellValue = getCellValueAsString(row.getCell(i)).trim();
//			if (!StringUtils.isBlank(cellValue)) {
//				JsonPath tag = new JsonPath(cellValue);
//				tagToColumn.put(tag, i);
//				if (cellValue.startsWith(JsonPath.SEPARATOR)) {
//					JsonPath parentTag = tag.parent();
//					if (!parentTagToColumns.containsKey(parentTag)) {
//						parentTagToColumns.put(parentTag, new LinkedList<>());
//					}
//					parentTagToColumns.get(parentTag).add(i);
//					if (topObject == null) {
//						int index = parentTag.getPath().indexOf(JsonPath.SEPARATOR, 1);
//						String substring = parentTag.getPath().substring(0, index);
//						topObject = new JsonPath(substring);
//					}
//				}
//				if (cellValue.startsWith("[") && cellValue.endsWith("]")) {
//					fixedValue.put(i, new PreviewCell(cellValue.substring(1, cellValue.length() - 1)));
//				}
//			}
//		}
//
//		row = sheet.getRow(rIndex++);
//		if (row != null) {
//			for (int i = 0; i < lastCellNum; i++) {
//				cellValue = getCellValueAsString(row.getCell(i)).trim();
//				if (!StringUtils.isBlank(cellValue)) {
//					neededColumns.add(i);
//				}
//			}
//		}
//
//		row = sheet.getRow(rIndex++);
//		if (row != null) {
//			Cell cell = row.getCell(0);
//			if (cell != null) {
//				CellStyle cellStyleForOddRow = cell.getCellStyle();
//				sequenceToCellStyle.put(1, cellStyleForOddRow);
//			}
//		}
//
//		row = sheet.getRow(rIndex++);
//		if (row != null) {
//			Cell cell = row.getCell(0);
//			if (cell != null) {
//				CellStyle cellStyleForEvenRow = cell.getCellStyle();
//				sequenceToCellStyle.put(0, cellStyleForEvenRow);
//			}
//		}
//
//		row = sheet.getRow(rIndex++);
//		tagForClearRowList = new JsonPath(getCellValueAsString(row.getCell(1)).trim());
//		
//		// skip root tag
//		row = sheet.getRow(rIndex++);
//		
//		row = sheet.getRow(rIndex++);
//		Cell cell = row.getCell(0);
//		cellValue = getCellValueAsString(cell);
//		while (!DEFINITION_ENDS.equals(cellValue)) {
//			Cell tagCell = row.getCell(1);
//			String tag = getCellValueAsString(tagCell);
//			Cell rowIndexCell = row.getCell(2);
//			Cell columnIndexCell = row.getCell(3);
//			int rowIndex = Integer.parseInt(getCellValueAsString(rowIndexCell));
//			int columnIndex = Integer.parseInt(getCellValueAsString(columnIndexCell));
//			List<Integer> rowColumn = Arrays.asList(rowIndex, columnIndex);
//			tagToLocation.put(new JsonPath(tag), rowColumn);
//			row = sheet.getRow(rIndex++);
//			cell = row.getCell(0);
//			cellValue = getCellValueAsString(cell);
//		}		
//
//		for (int i = startRowNum; i < rIndex; i++) {
//			removeRow(startRowNum);
//		}
//		log.info("initialized PoiSheet object: {}", this);
//	}
//
//	/**
//	 * Returns the formatted value of a cell as a String regardless of the cell
//	 * type. If the Excel format pattern cannot be parsed then the cell value will
//	 * be formatted using a default format.
//	 * 
//	 * When passed a null or blank cell, this method will return an empty String
//	 * (""). Formulas in formula type cells will not be evaluated.
//	 *
//	 * @param cell the cell
//	 * @return the cell value as string
//	 */
//	protected String getCellValueAsString(Cell cell) {
//		return formatter.formatCellValue(cell);
//	}
//
//	/**
//	 * Removes the row.
//	 *
//	 * @param rowIndex the row index
//	 */
//	protected void removeRow(int rowIndex) {
//		int lastRowNum = sheet.getLastRowNum();
//		if (rowIndex >= 0 && rowIndex < lastRowNum) {
//			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
//		}
//		if (rowIndex == lastRowNum) {
//			Row removingRow = sheet.getRow(rowIndex);
//			if (removingRow != null) {
//				sheet.removeRow(removingRow);
//			}
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see java.lang.Object#toString()
//	 */
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("PreviewSheet [sheet=").append(sheet.getSheetName()).append(", startRowNum=").append(startRowNum)
//				.append(", lastCellNum=").append(lastCellNum).append(", tagToColumn=").append(tagToColumn)
//				.append(", parentTagToColumns=").append(parentTagToColumns).append(", tagForClearRowList=")
//				.append(tagForClearRowList).append(", neededColumns=").append(neededColumns)
//				.append(", sequenceToCellStyle=").append(sequenceToCellStyle).append(", tagToLocation=")
//				.append(tagToLocation).append("]");
//		return builder.toString();
//	}
//
//	/**
//	 * Value node.
//	 *
//	 * @param data the data
//	 * @param path the path
//	 */
//	public void valueNode(String data, JsonPath path) {
//		if (tagToColumn.containsKey(path)) {
//			List<PreviewRow> previewRows = rowTracker.get(path.parent());
//			previewRows.forEach(row -> row.createCell(tagToColumn.get(path), new PreviewCell(data)));
//			tagToValue.put(path, data);
//		}
//		if (tagToLocation.containsKey(path)) {
//			List<Integer> rowColumn = tagToLocation.get(path);
//			Row row = sheet.getRow(rowColumn.get(0));
//			Cell cell = row.getCell(rowColumn.get(1));
//			cell.setCellValue(data);
//		}
//	}
//
//	/**
//	 * Creates the new preview row.
//	 *
//	 * @param path the path
//	 */
//	protected void createNewPreviewRow(JsonPath path) {
//		PreviewRow previewRow = new PreviewRow();
//		tagToValue.entrySet().forEach(
//				entry -> previewRow.createCell(tagToColumn.get(entry.getKey()), new PreviewCell(entry.getValue())));
//		addRowTracker(path, previewRow);
//		rowTracker.entrySet().stream().filter(entry -> entry.getKey().isAncestor(path))
//				.forEach(entry -> addRowTracker(entry.getKey(), previewRow));
//	}
//
//	/**
//	 * Start container node.
//	 *
//	 * @param path the path
//	 */
//	public void startContainerNode(JsonPath path) {
//		if (topObject.equals(path) || topObject.isAncestor(path)) {
//			List<JsonPath> keysToBeRemoved = tagToValue.keySet().stream().filter(key -> key.isDescendant(path))
//					.collect(Collectors.toList());
//			keysToBeRemoved.forEach(key -> tagToValue.remove(key));
//			if (isRowTrackerEmpty(path) && isRowTrackerEmpty(path.parent())) {
//				if (parentTagToColumns.containsKey(path)) {
//					createNewPreviewRow(path);
//				}
//			} else if (isRowTrackerEmpty(path)) {
//				rowTracker.put(path, new ArrayList<>(rowTracker.get(path.parent())));
//			} else {
//				if (parentTagToColumns.containsKey(path)) {
//					rowTracker.get(path).clear();
//					createNewPreviewRow(path);
//				}
//			} 
//		}
//	}
//
//	/**
//	 * Checks if is row tracker empty.
//	 *
//	 * @param path the path
//	 * @return true, if is row tracker empty
//	 */
//	protected boolean isRowTrackerEmpty(JsonPath path) {
//		return rowTracker.get(path) == null || rowTracker.get(path).isEmpty();
//	}
//
//	/**
//	 * Adds the row tracker.
//	 *
//	 * @param path the path
//	 * @param row the row
//	 */
//	protected void addRowTracker(JsonPath path, PreviewRow row) {
//		if (!rowTracker.containsKey(path)) {
//			rowTracker.put(path, new ArrayList<>());
//		}
//		rowTracker.get(path).add(row);
//	}
//
//	/**
//	 * End container node.
//	 *
//	 * @param path the path
//	 */
//	public void endContainerNode(JsonPath path) {
//		if (topObject.equals(path) || topObject.isAncestor(path)) {
//			rowTracker.entrySet().stream().filter(entry -> entry.getKey().isDescendant(path))
//					.forEach(entry -> entry.getValue().clear());
//			if (tagForClearRowList.equals(path)) {
//				List<PreviewRow> cachedRows = rowTracker.get(path);
//				cachedRows.removeIf(previewRow -> !previewRow.isRowWithAnyOfNeededColumn(neededColumns));
//				for (PreviewRow previewRow : cachedRows) {
//					rowData.add(previewRow);
//				}
//				rowTracker.clear();
//			}
//		}
//	}
//
//	/**
//	 * Gets the sheet.
//	 *
//	 * @return the sheet
//	 */
//	public Sheet getSheet() {
//		return sheet;
//	}
//
//	/**
//	 * Sets the sheet.
//	 *
//	 * @param sheet the new sheet
//	 */
//	public void setSheet(Sheet sheet) {
//		this.sheet = sheet;
//	}
//
//	/**
//	 * Write.
//	 */
//	protected void write() {
//		for (PreviewRow previewRow : rowData) {
//			Row row = sheet.createRow(rowIndex++);
//			int sequence = rowIndex - startRowNum;
//			if (tagToColumn.containsKey(SEQUENCE)) {
//				previewRow.createCell(tagToColumn.get(SEQUENCE), new PreviewCell((double) sequence));
//			}
//			for (int i = 0; i < lastCellNum; i++) {
//				if (fixedValue.containsKey(i)) {
//					writeCell(row, i, sequence, fixedValue.get(i));
//				} else {
//					writeCell(row, i, sequence, previewRow.getColumnToCell().get(i));
//				}
//			}
//		}
//	}
//
//	/**
//	 * Write cell.
//	 *
//	 * @param row the row
//	 * @param column the column
//	 * @param sequence the sequence
//	 * @param previewCell the preview cell
//	 */
//	protected void writeCell(Row row, int column, int sequence, PreviewCell previewCell) {
//		Cell cell = row.createCell(column);
//		CellStyle cellStyle = sequenceToCellStyle.get(sequence % 2);
//		if (previewCell != null) {
//			if (previewCell.getStrVal() != null) {
//				cell.setCellValue(previewCell.getStrVal());
//			} else if (previewCell.getNumVal() != null) {
//				cell.setCellValue(previewCell.getNumVal());
//			}
//		}
//		if (cellStyle != null) {
//			cell.setCellStyle(cellStyle);
//		}
//	}
//
//	/**
//	 * Checks if is empty.
//	 *
//	 * @return true, if is empty
//	 */
//	public boolean isEmpty() {
//		return rowData.isEmpty();
//	}
}
