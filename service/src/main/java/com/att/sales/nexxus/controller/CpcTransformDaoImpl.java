package com.att.sales.nexxus.controller;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * The Class CpcTransformDaoImpl.
 *
 * @author DevChouhan
 */
@Component
public class CpcTransformDaoImpl {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(CpcTransformDaoImpl.class);

	/** The oracle jdbc template. */
	@Autowired
	private JdbcTemplate oracleJdbcTemplate;

	/**
	 * Test DB connection.
	 *
	 * @return the long
	 */
	public Long testDBConnection() {
		Long count = oracleJdbcTemplate.queryForObject("SELECT COUNT(*) FROM TAB ", Long.class);
		log.info("Count---->>{}", count);
		return count;
	}

	/**
	 * checkTable checks for corresponding tables in database.
	 *
	 * @param fileNameAstableName the file name astable name
	 * @param header the header
	 * @return the long
	 */
	public Long checkTable(final String fileNameAstableName, final Map<String, Integer> header) {
		String sqlCheckTable = "SELECT Count(*) FROM TAB  where TNAME=UPPER(?)";
		Long count = oracleJdbcTemplate.queryForObject(sqlCheckTable, Long.class, fileNameAstableName);
		log.info("{}<<::Table Present---->>{}", fileNameAstableName, count);
		if (count == 0) {
			createCSVTable(fileNameAstableName, header);
		}
		return count;
	}

	/**
	 * createCSVTable creates tables for corresponding text files, if tables don't
	 * exist.
	 *
	 * @param fileNameAstableName the file name astable name
	 * @param header the header
	 */
	public void createCSVTable(final String fileNameAstableName, final Map<String, Integer> header) {
		StringBuilder commandCreateTable = new StringBuilder(
				"CREATE TABLE " + fileNameAstableName + " ( " + System.lineSeparator());
		header.forEach((key, value) -> {

			log.info("Column Name :{}:: Int-Seq : {} :: Index value {} :: HeaderSize {} ", key, value, value,
					header.size());

			commandCreateTable.append(key).append("  ").append("VARCHAR2(100)").append(" ,")
					.append(System.lineSeparator());

		});
		commandCreateTable.append("TRANSACTIONID").append("  ").append("VARCHAR2(100)").append(System.lineSeparator());

		commandCreateTable.append("  )");
		log.info("Command-Create-Table::{}", commandCreateTable);

		try {
			oracleJdbcTemplate.execute(commandCreateTable.toString());
		} catch (DataAccessException e) {
			log.error("getMessage()", e);
		}

	}

	/**
	 * batchUpdate Inserts data in to tables in batch.
	 *
	 * @param sqlInsertStatement the sql insert statement
	 * @param columnHeaderMap the column header map
	 * @param recordList the record list
	 * @return the int[]
	 */
	public int[] batchUpdate(String sqlInsertStatement, final TreeMap<Integer, String> columnHeaderMap,
			final List<CSVRecord> recordList) {

		int[] updateCounts = null;

		updateCounts = oracleJdbcTemplate.batchUpdate(sqlInsertStatement, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CSVRecord record = recordList.get(i);
				Map<String, String> recordMap = record.toMap();
				final AtomicInteger indexHolder = new AtomicInteger(1);
				columnHeaderMap.forEach((key, value) -> {
					final int index = indexHolder.getAndIncrement();
					try {
						ps.setString(index, recordMap.get(value));
					} catch (SQLException e) {
						log.error("getMessage()", e);
					}
				});

			}

			@Override
			public int getBatchSize() {
				return recordList.size();
			}

		});

		return updateCounts;
	}

}
