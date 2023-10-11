package com.att.sales.nexxus.inr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.sales.nexxus.constant.InrConstants;
import com.fasterxml.jackson.databind.JsonNode;

public class PreviewSheetV1 {
	private static Logger log = LoggerFactory.getLogger(PreviewSheetV1.class);
	private static final String DEFINITION_STARTS = "definition starts";
	private static final String DEFINITION_ENDS = "definition ends";
	private static final String SEQUENCE = "sequence";
	private Sheet sheet;
	private DataFormatter formatter = new DataFormatter();
	private int startRowNum;
	private int rowIndex;
	private int lastCellNum;
	private Map<String, Integer> tagToColumn = new HashMap<>();
	private Set<Integer> neededColumns = new HashSet<>();
	private Map<Integer, CellStyle> sequenceToCellStyle = new HashMap<>();
	private Map<String, List<Integer>> tagToLocation = new HashMap<>();
	private Map<Integer, String> columnToFormula = new LinkedHashMap<>();
	private String rootTag;

	public PreviewSheetV1(Sheet sheet) {
		super();
		this.sheet = sheet;
	}

	protected void init() {
		int rIndex = 0;
		String cellValue = null;
		do {
			Row row = sheet.getRow(rIndex++);
			if (row == null) {
				continue;
			}
			Cell cell = row.getCell(0);
			cellValue = getCellValueAsString(cell);
		} while (!DEFINITION_STARTS.equals(cellValue));
		startRowNum = rIndex - 1;
		rowIndex = startRowNum;

		Row row = sheet.getRow(rIndex++);
		lastCellNum = row.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			cellValue = getCellValueAsString(row.getCell(i)).trim();
			if (!StringUtils.isBlank(cellValue)) {
				if (!cellValue.startsWith("!")) {
					String[] tags = cellValue.split("\\s*,\\s*");
					for (String tag : tags) {
						tagToColumn.put(tag, i);
					}
				} else {
					columnToFormula.put(i, cellValue);
				}
			}
		}

		row = sheet.getRow(rIndex++);
		if (row != null) {
			for (int i = 0; i < lastCellNum; i++) {
				cellValue = getCellValueAsString(row.getCell(i)).trim();
				if (!StringUtils.isBlank(cellValue)) {
					neededColumns.add(i);
				}
			}
		}

		row = sheet.getRow(rIndex++);
		if (row != null) {
			Cell cell = row.getCell(0);
			if (cell != null) {
				CellStyle cellStyleForOddRow = cell.getCellStyle();
				sequenceToCellStyle.put(1, cellStyleForOddRow);
			}
		}

		row = sheet.getRow(rIndex++);
		if (row != null) {
			Cell cell = row.getCell(0);
			if (cell != null) {
				CellStyle cellStyleForEvenRow = cell.getCellStyle();
				sequenceToCellStyle.put(0, cellStyleForEvenRow);
			}
		}

		// skip tagForClearRowList
		row = sheet.getRow(rIndex++);

		row = sheet.getRow(rIndex++);
		rootTag = getCellValueAsString(row.getCell(1)).trim();

		row = sheet.getRow(rIndex++);
		Cell cell = row.getCell(0);
		cellValue = getCellValueAsString(cell);
		while (!DEFINITION_ENDS.equals(cellValue)) {
			Cell tagCell = row.getCell(1);
			String tag = getCellValueAsString(tagCell);
			Cell rowIndexCell = row.getCell(2);
			Cell columnIndexCell = row.getCell(3);
			int rowIndex = Integer.parseInt(getCellValueAsString(rowIndexCell));
			int columnIndex = Integer.parseInt(getCellValueAsString(columnIndexCell));
			List<Integer> rowColumn = Arrays.asList(rowIndex, columnIndex);
			tagToLocation.put(tag, rowColumn);
			row = sheet.getRow(rIndex++);
			cell = row.getCell(0);
			cellValue = getCellValueAsString(cell);
		}

		for (int i = startRowNum; i < rIndex; i++) {
			removeRow(startRowNum);
		}
//		log.info("initialized PoiSheet object: {}", this);
	}

	/**
	 * Returns the formatted value of a cell as a String regardless of the cell
	 * type. If the Excel format pattern cannot be parsed then the cell value will
	 * be formatted using a default format.
	 * 
	 * When passed a null or blank cell, this method will return an empty String
	 * (""). Formulas in formula type cells will not be evaluated.
	 *
	 * @param cell the cell
	 * @return the cell value as string
	 */
	protected String getCellValueAsString(Cell cell) {
		return formatter.formatCellValue(cell);
	}

	/**
	 * Removes the row.
	 *
	 * @param rowIndex the row index
	 */
	protected void removeRow(int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			Row removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreviewSheetV1 [sheet=").append(sheet).append(", startRowNum=").append(startRowNum)
				.append(", lastCellNum=").append(lastCellNum).append(", tagToColumn=").append(tagToColumn)
				.append(", neededColumns=").append(neededColumns).append(", sequenceToCellStyle=")
				.append(sequenceToCellStyle).append(", tagToLocation=").append(tagToLocation).append(", rootTag=")
				.append(rootTag).append("]");
		return builder.toString();
	}

	public void processNode(JsonNode node) {
		if (rootTag.equals(node.path(InrConstants.FLATTEN_ROOT_TAG).asText())) {
			int sequence = node.path(SEQUENCE).asInt();
			PreviewRow previewRow = new PreviewRow();
			tagToColumn.entrySet().forEach(entry -> {
				String field = entry.getKey();
				int column = entry.getValue();
				if (node.hasNonNull(field)) {
					if (SEQUENCE.equals(field)) {
						previewRow.createCell(column, new PreviewCell(node.path(field).asDouble()));
					} else {
						previewRow.createCell(column, new PreviewCell(node.path(field).asText()));
					}
				}
			});
			if (previewRow.isRowWithAnyOfNeededColumn(neededColumns)) {
				Row row = sheet.createRow(rowIndex++);
				for (int i = 0; i < lastCellNum; i++) {
					writeCell(row, i, sequence, previewRow.getColumnToCell().get(i));
				}
				columnToFormula.entrySet().stream()
						.forEach(entry -> writeFormula(entry.getKey(), entry.getValue(), rowIndex, row, sequence));
			}
		}
	}

	protected void writeFormula(int column, String formula, int rowNum, Row row, int sequence) {
		Cell cell = row.createCell(column);
		CellStyle cellStyle = sequenceToCellStyle.get(sequence % 2);
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
		//cell.setCellType(CellType.FORMULA);
		cell.setCellFormula(formula.substring(1).replaceAll("#", String.valueOf(rowNum)));
		//eval.evaluateFormulaCellEnum(cell);
	}

	protected void writeCell(Row row, int column, int sequence, PreviewCell previewCell) {
		Cell cell = row.createCell(column);
		CellStyle cellStyle = sequenceToCellStyle.get(sequence % 2);
		if (previewCell != null) {
			if (previewCell.getStrVal() != null) {
				cell.setCellValue(previewCell.getStrVal());
			} else if (previewCell.getNumVal() != null) {
				cell.setCellValue(previewCell.getNumVal());
			}
		}
		if (cellStyle != null) {
			cell.setCellStyle(cellStyle);
		}
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public String getRootTag() {
		return rootTag;
	}

	public void populateHeader(JsonNode node) {
		if (!tagToLocation.isEmpty()) {
			tagToLocation.entrySet().forEach(entry -> {
				String field = entry.getKey();
				List<Integer> rowColumn = entry.getValue();
				if (node.hasNonNull(field)) {
					Row row = sheet.getRow(rowColumn.get(0));
					Cell cell = row.getCell(rowColumn.get(1));
					cell.setCellValue(node.path(field).asText());
				}
			});
		}
	}
}
