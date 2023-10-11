package com.att.sales.nexxus.inr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class PreviewRow.
 */
public class PreviewRow {
	
	/** The column to cell. */
	private Map<Integer, PreviewCell> columnToCell = new HashMap<>();
	
	/**
	 * Creates the cell.
	 *
	 * @param column the column
	 * @param cell the cell
	 */
	public void createCell(int column, PreviewCell cell) {
		columnToCell.put(column, cell);
	}
	
	/**
	 * Checks if is row with any of needed column.
	 *
	 * @param neededColumns the needed columns
	 * @return true, if is row with any of needed column
	 */
	public boolean isRowWithAnyOfNeededColumn(Set<Integer> neededColumns) {
		if (neededColumns == null || neededColumns.isEmpty()) {
			return true;
		}
		for (Integer i : neededColumns) {
			if (columnToCell.containsKey(i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the column to cell.
	 *
	 * @return the column to cell
	 */
	public Map<Integer, PreviewCell> getColumnToCell() {
		return columnToCell;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PreviewRow [columnToCell=").append(columnToCell).append("]");
		return builder.toString();
	}
}
