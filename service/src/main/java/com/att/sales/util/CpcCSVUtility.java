package com.att.sales.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.controller.CpcTransformDaoImpl;

/**
 * The Class CpcCSVUtility.
 *
 * @author DevChouhan
 */
@Component
public class CpcCSVUtility {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(CpcCSVUtility.class);

	/** The cpc transform dao impl. */
	@Autowired
	private CpcTransformDaoImpl cpcTransformDaoImpl;

	/**
	 * readCSVHeader method reads the file and store the values in the database.
	 *
	 * @param fileNameWithPath the file name with path
	 * @param transactionId the transaction id
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean readCSVHeader(String fileNameWithPath, String transactionId) throws IOException {
		boolean flag = false;

		Reader fileReader = Files.newBufferedReader(Paths.get(fileNameWithPath)); //NOSONAR
		String tableName = trimFileName(fileNameWithPath);
		if (tableName.contains("PRICE_CATALOGUE")) {
			String tableName1 = tableName.substring(0, 5);

			CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
					.withIgnoreHeaderCase().withTrim().withDelimiter('|').withQuote(null));
			Map<String, Integer> header = csvParser.getHeaderMap();
			cpcTransformDaoImpl.checkTable(tableName1, header);
			log.info("Header--::--" + header + "::::" + tableName1 + "::REcord Number::"
					+ csvParser.getCurrentLineNumber());

			String sqlInsertUpdateStatement = prepareInsertStatement(tableName1, header, transactionId);
			log.info("Insert Statement::" + sqlInsertUpdateStatement);

			// Alternative way
			TreeMap<Integer, String> sortedHeader2 = new TreeMap<Integer, String>();
			header.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
					.forEachOrdered(x -> sortedHeader2.put(x.getValue(), x.getKey()));

			List<CSVRecord> recordList = new ArrayList<CSVRecord>();
			for (CSVRecord record : csvParser) {
				recordList.add(record);

			}
			cpcTransformDaoImpl.batchUpdate(sqlInsertUpdateStatement, sortedHeader2, recordList);
		} else {
			CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
					.withIgnoreHeaderCase().withTrim().withDelimiter('|').withQuote(null));
			Map<String, Integer> header = csvParser.getHeaderMap();
			cpcTransformDaoImpl.checkTable(tableName, header);
			log.info("Header--::--" + header + "::::" + tableName + "::REcord Number::"
					+ csvParser.getCurrentLineNumber());

			String sqlInsertUpdateStatement = prepareInsertStatement(tableName, header, transactionId);
			log.info("Insert Statement::" + sqlInsertUpdateStatement);
			// Alternative way
			TreeMap<Integer, String> sortedHeader2 = new TreeMap<Integer, String>();
			header.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
					.forEachOrdered(x -> sortedHeader2.put(x.getValue(), x.getKey()));

			List<CSVRecord> recordList = new ArrayList<CSVRecord>();
			for (CSVRecord record : csvParser) {
				recordList.add(record);

			}
			cpcTransformDaoImpl.batchUpdate(sqlInsertUpdateStatement, sortedHeader2, recordList);
		}

		return flag;
	}

	/**
	 * prepareInsertStatement method is the one which is helps to create table
	 * columns and values.
	 *
	 * @param tableName the table name
	 * @param header the header
	 * @param transactionId the transaction id
	 * @return the string
	 */
	public String prepareInsertStatement(String tableName, Map<String, Integer> header, String transactionId) {
		StringBuilder sqlInsertStmt = new StringBuilder("INSERT INTO  " + tableName + "  (");
		StringBuilder sqlValueStmt = new StringBuilder(" VALUES  (");

		TreeMap<Integer, String> sortedHeader1 = header.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.naturalOrder())).collect(Collectors
						.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue, TreeMap::new));
		log.info("Sorted Header 1::" + sortedHeader1);

		sortedHeader1.forEach((key, value) -> {

			sqlInsertStmt.append(value).append(",");
			sqlValueStmt.append("? ,");

		});
		sqlInsertStmt.append("TRANSACTIONID").append(" ) ");
		sqlValueStmt.append("'" + transactionId + "'").append(")");

		return sqlInsertStmt.append(sqlValueStmt).toString();
	}

	/**
	 * trimFileName method will give the fileName that will same as TableName.
	 *
	 * @param onlyFileName the only file name
	 * @return the string
	 */
	public String trimFileName(String onlyFileName) {
		onlyFileName = FilenameUtils.getBaseName(onlyFileName);
		onlyFileName = onlyFileName.replaceAll("^[\\s\\.\\d]+", "").toUpperCase();
		onlyFileName = onlyFileName.substring(onlyFileName.indexOf("_") + 1);

		if (onlyFileName.length() > 30) {
			onlyFileName = onlyFileName.replaceAll("PRODUCT", "PROD");
			onlyFileName = onlyFileName.replaceAll("COMPONENT", "COMP");
			onlyFileName = onlyFileName.replaceAll("ATTRIBUTE", "ATTR");
			onlyFileName = onlyFileName.replaceAll("VALUE", "VAL");
			onlyFileName = onlyFileName.replaceAll("MATRIX", "MAT");
			onlyFileName = onlyFileName.replaceAll("METADATA", "META");
			onlyFileName = onlyFileName.replaceAll("HIERARCHY", "HIERA");
			onlyFileName = onlyFileName.replaceAll("INSTANCE", "INSTA");

		}
		return onlyFileName;
	}

}
