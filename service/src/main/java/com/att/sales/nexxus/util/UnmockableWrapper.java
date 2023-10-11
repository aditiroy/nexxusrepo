package com.att.sales.nexxus.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

/**
 * The Class UnmockableWrapper.
 */
@Component
public class UnmockableWrapper {
	
	/**
	 * New file input stream.
	 *
	 * @param path the path
	 * @return the file input stream
	 * @throws FileNotFoundException the file not found exception
	 */
	public FileInputStream newFileInputStream(String path) throws FileNotFoundException {
		return new FileInputStream(path); //NOSONAR
	}
	
	/**
	 * New file output stream.
	 *
	 * @param path the path
	 * @return the file output stream
	 * @throws FileNotFoundException the file not found exception
	 */
	public FileOutputStream newFileOutputStream(String path) throws FileNotFoundException {
		return new FileOutputStream(path); //NOSONAR
	}
	
	/**
	 * Read file.
	 *
	 * @param path the path
	 * @param encoding the encoding
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path)); //NOSONAR
		return new String(encoded, encoding);
	}
}
