package com.att.sales.nexxus.inr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
public class PreviewRowTest {
	@Test
	public void test() {
		PreviewRow previewRow = new PreviewRow();
		PreviewCell previewCell = new PreviewCell("value");
		previewRow.createCell(1, previewCell);
		Set<Integer> neededColumns = new HashSet<>();
		assertTrue(previewRow.isRowWithAnyOfNeededColumn(neededColumns));
		neededColumns.add(2);
		assertFalse(previewRow.isRowWithAnyOfNeededColumn(neededColumns));
		neededColumns.add(1);
		assertTrue(previewRow.isRowWithAnyOfNeededColumn(neededColumns));
		assertEquals(1, previewRow.getColumnToCell().size());
		previewRow.toString();
	}
}
