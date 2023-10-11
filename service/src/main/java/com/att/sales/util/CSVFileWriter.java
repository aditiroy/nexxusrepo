package com.att.sales.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.att.sales.framework.exception.SalesBusinessException;

/**
 * The Class CSVFileWriter.
 */
public class CSVFileWriter {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(CSVFileWriter.class);
	
	/**
	 * Write CSV file.
	 *
	 * @param csvFileName the csv file name
	 * @param csvList the csv list
	 * @param header the header
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	
	
	/* commented this code as it is not used and causing vulnerability on 17/2/21
	 * public static void writeCSVFile(String csvFileName, List<?> csvList,String[] header) throws IOException{
		logger.info("Inside writeCSVFile method()");
		Path output = Paths.get(csvFileName); //NOSONAR
		Files.createDirectories(output.getParent());
		
		try (ICsvBeanWriter beanWriter = new CsvBeanWriter(Files.newBufferedWriter(output),
				new CsvPreference.Builder('"', '|', "\n").build())) {
			beanWriter.writeHeader(header);
			for (Object productCosPackage : csvList) {
				beanWriter.write(productCosPackage, header);
			}
		}
	}*/
	
	/**
	 * PIPE_DELIMITED =new CsvPreference.Builder('"', '|', "\n").build()))
	 *
	 * @param outputFile the output file
	 * @param inputFiles the input files
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	/* commented this code as it is not used and causing vulnerability on 17/2/21
	 * public static void generateZipfile(String outputFile, String... inputFiles) throws IOException {
		logger.info("Inside generateZipfile method()");
		Path output = Paths.get(outputFile); //NOSONAR
		Files.createDirectories(output.getParent());
		try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(output))) {
			for (String inputFile : inputFiles) {
				Path in =  Paths.get(inputFile); //NOSONAR
				addToZipFile(in, zos);
			}
		}
	}*/
	
	/**
	 * Adds the to zip file.
	 *
	 * @param in the in
	 * @param zos the zos
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void addToZipFile(Path in, ZipOutputStream zos) throws IOException {
		logger.info("Inside addToZipFile method()");
		try (InputStream is = Files.newInputStream(in)) {
			ZipEntry zipEntry = new ZipEntry(in.getFileName().toString());
			zos.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
		} finally {
			zos.closeEntry();
		}
	}
	
	/**
	 * Convert blob to file.
	 *
	 * @param blob the blob
	 * @param fileName the file name
	 * @return the file
	 * @throws SalesBusinessException the sales business exception
	 */
	public static File convertBlobToFile(Blob blob, String fileName) throws SalesBusinessException {
			logger.info("Inside convertBlobToFile method()");
			File newFile = new File(fileName);	//NOSONAR	
			try { 
				newFile.createNewFile();
				InputStream in = blob.getBinaryStream();
				OutputStream out =  new FileOutputStream(newFile);
				IOUtils.copy(in, out);
			} catch (Exception e) {
				logger.error("Exception occured while converting Blob to File",e);
				throw new SalesBusinessException(e.getMessage());
			}
			return newFile;
	}
	
	
	/**
	 * Public constructor.
	 */
	private CSVFileWriter () {
		//default constructor
	}
	
	/**
	 * Convert file to string.
	 *
	 * @param file the file
	 * @return the byte[]
	 * @throws SalesBusinessException the sales business exception
	 */
	public static byte[]  convertFileToString(File file) throws SalesBusinessException {
		logger.info("Inside convertFileToString method()");
		byte[] fileByte = new byte[(int) file.length()];
		
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
			fileInputStream.read(fileByte);
        } catch (FileNotFoundException e) {
        	logger.error("FileNotFoundException occured during converting file to byte String",e);
        	throw new SalesBusinessException(e.getMessage());
        } catch (IOException e) {
        	logger.error("IOException occured during converting file to byte String",e);
        	throw new SalesBusinessException(e.getMessage());
        }

		return fileByte;
	}

}
